package school.bonobono.fyb.domain.user.Service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.user.Dto.*;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Config.Jwt.SecurityUtil;
import school.bonobono.fyb.global.Config.Jwt.TokenProvider;
import school.bonobono.fyb.global.Config.Redis.RedisDao;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.StatusTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static school.bonobono.fyb.global.Exception.CustomErrorCode.*;
import static school.bonobono.fyb.global.Model.Model.*;
import static school.bonobono.fyb.global.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RedisDao redisDao;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AmazonS3Client amazonS3Client;
    Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // validate 및 단순 메소드화

    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    private void PHONE_NUM_LENGTH_CHECK(PhoneCheckDto.Request request) {
        if (!(request.getPnum().length() == 13)) {
            throw new CustomException(PHONE_NUM_ERROR);
        }
    }

    private void REGISTER_VALIDATION(UserRegisterDto.Request request) {
        if (request.getEmail() == null || request.getPw() == null || request.getName() == null
                || request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(REGISTER_INFO_NULL);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new CustomException(DUPLICATE_USER);

        if (!request.getEmail().contains("@"))
            throw new CustomException(NOT_EMAIL_FORM);

        if (!(request.getPw().length() > 5))
            throw new CustomException(PASSWORD_SIZE_ERROR);

        if (!(request.getPw().contains("!") || request.getPw().contains("@") || request.getPw().contains("#")
                || request.getPw().contains("$") || request.getPw().contains("%") || request.getPw().contains("^")
                || request.getPw().contains("&") || request.getPw().contains("*") || request.getPw().contains("(")
                || request.getPw().contains(")"))
        ) {
            throw new CustomException(NOT_CONTAINS_EXCLAMATIONMARK);
        }
    }

    private void UPDATE_VALIDATION(UserUpdateDto.Request request) {
        if (request.getName() == null || request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(UPDATE_INFO_NULL);
    }

    private void PWCHANGE_VALIDATION(PwChangeDto.Request request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPw(), getTokenInfo().getPw())) {
            throw new CustomException(PASSWORD_CHANGE_STATUS_FALSE);
        }
    }

    private void PWLOSTCHANGE_VALIDATION(PwChangeDto.lostRequest request, String pw) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_USER)
                );
        if (passwordEncoder.matches(request.getNewPw(), pw)) {
            throw new CustomException(PASSWORD_IS_NOT_CHANGE);
        }
    }

    private void LOGIN_VALIDATION(UserLoginDto.Request request) {
        if (request.getPw().equals("google"))
            throw new CustomException(NOT_SOCIAL_LOGIN);

        if (!request.getEmail().contains("@"))
            throw new CustomException(NOT_EMAIL_FORM);

        userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(LOGIN_FALSE)
                );

        if (!passwordEncoder.matches(
                request.getPw(),
                userRepository.findByEmail(request.getEmail())
                        .get()
                        .getPw()
        )
        ) {
            throw new CustomException(LOGIN_FALSE);
        }
    }

    // Service
    // 로그인
    public ResponseEntity<StatusTrue> loginUser(UserLoginDto.Request request) {
        LOGIN_VALIDATION(request);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPw());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String atk = tokenProvider.createToken(authentication);
        String rtk = tokenProvider.createRefreshToken(request.getEmail());

        redisDao.setValues(request.getEmail(), rtk, Duration.ofDays(14));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + atk);
        return new ResponseEntity<>(StatusTrue.LOGIN_STATUS_TRUE, httpHeaders, HttpStatus.OK);
    }

    // 회원가입
    @Transactional
    public ResponseEntity<StatusTrue> registerUser(UserRegisterDto.Request request) {
        REGISTER_VALIDATION(request);

        String bmiGrade;
        String userForm = request.getForm() + request.getPelvis() + request.getShoulder() + request.getLeg();

        double BMI = ((double) request.getWeight() / (double) request.getHeight() / (double) request.getHeight()) * 10000;
        if (BMI <= 18.5) {
            bmiGrade = "A";
        } else if (BMI <= 22.9) {
            bmiGrade = "B";
        } else if (BMI <= 24.9) {
            bmiGrade = "C";
        } else if (BMI <= 29.9) {
            bmiGrade = "D";
        } else {
            bmiGrade = "E";
        }
        userRepository.save(
                FybUser.builder()
                        .email(request.getEmail())
                        .pw(passwordEncoder.encode(request.getPw()))
                        .name(request.getName())
                        .authorities(Collections.singleton(authority))
                        .gender(request.getGender())
                        .height(request.getHeight())
                        .weight(request.getWeight())
                        .age(request.getAge())
                        .userData(request.getGender() + bmiGrade + userForm)
                        .build()
        );

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPw());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String atk = tokenProvider.createToken(authentication);
        String rtk = tokenProvider.createRefreshToken(request.getEmail());

        redisDao.setValues(request.getEmail(), rtk, Duration.ofDays(14));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + atk);

        return new ResponseEntity<>(REGISTER_STATUS_TRUE, httpHeaders, HttpStatus.CREATED);
    }

    // 프로필 이미지 업로드
    @Transactional
    public ResponseEntity<StatusTrue> updateImage(MultipartFile multipartFile) throws IOException {

        // String ext = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        String profile_image_name = "profile/" + getTokenInfo().getEmail() + ".jpg";
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());
        amazonS3Client.putObject(bucket, profile_image_name, multipartFile.getInputStream(), objMeta);

        userRepository.save(
                FybUser.builder()
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(getTokenInfo().getPw())
                        .name(getTokenInfo().getName())
                        .authorities(Collections.singleton(authority))
                        .gender(getTokenInfo().getGender())
                        .height(getTokenInfo().getHeight())
                        .weight(getTokenInfo().getWeight())
                        .age(getTokenInfo().getAge())
                        .userData(getTokenInfo().getUserData())
                        .profileImagePath(amazonS3Client.getUrl(bucket, profile_image_name).toString())
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );

        return new ResponseEntity<>(PROFILE_IMAGE_UPLOAD_TRUE, HttpStatus.OK);
    }

    // 내 정보 조회
    @Transactional
    public ResponseEntity<List<UserReadDto.UserResponse>> getMyInfo() {

        List<UserReadDto.UserResponse> list = userRepository.findById(getTokenInfo().getId())
                .stream()
                .map(UserReadDto.UserResponse::Response)
                .toList();

        // getCurrentUsername 은 해당 프젝에서는 email 임 !
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 내 정보 수정
    @Transactional
    public ResponseEntity<StatusTrue> updateUser(UserUpdateDto.Request request) {

        UPDATE_VALIDATION(request);

        userRepository.save(
                FybUser.builder()
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(getTokenInfo().getPw())
                        .name(request.getName())
                        .authorities(Collections.singleton(authority))
                        .gender(request.getGender())
                        .height(request.getHeight())
                        .weight(request.getWeight())
                        .age(request.getAge())
                        .userData(getTokenInfo().getUserData())
                        .profileImagePath(getTokenInfo().getProfileImagePath())
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );

        return new ResponseEntity<>(UPDATE_STATUS_TURE, HttpStatus.OK);
    }

    // 로그아웃
    @Transactional
    public ResponseEntity<StatusTrue> logoutUser(PwDeleteDto.Request2 request) {
        String atk = request.getToken().substring(7);
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        if (redisDao.getValues(email) != null) {
            redisDao.deleteValues(email);
        }

        redisDao.setValues(atk, "logout", Duration.ofMillis(
                tokenProvider.getExpiration(atk)
        ));

        return new ResponseEntity<>(LOGOUT_STATUS_TRUE, HttpStatus.OK);
    }

    // 휴대폰 인증
    @Transactional
    public Map<Object, Object> certifiedPhoneNumber(PhoneCheckDto.Request request) throws CoolsmsException {
        // 핸드폰 번호 - 포함 13글자 지정
        PHONE_NUM_LENGTH_CHECK(request);

        String randNum = RandomStringUtils.randomNumeric(6);

        Message coolsms = new Message(CHECK_API_KEY, CHEKC_API_SECRET);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", request.getPnum());
        params.put("from", "010-4345-4377");
        params.put("type", "SMS");
        params.put("text", "FYB 휴대폰인증 인증번호는" + "[ " + randNum + " ]" + "입니다.");
        params.put("app_version", "test app 1.2");

        JSONObject obj = (JSONObject) coolsms.send(params);

        Map<Object, Object> send = new HashMap<>();
        send.put("randNum", randNum);
        return send;
    }

    // 비밀번호 변경
    @Transactional
    public ResponseEntity<StatusTrue> PwChangeUser(PwChangeDto.Request request) {
        PWCHANGE_VALIDATION(request);

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        userRepository.save(
                FybUser.builder()
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(passwordEncoder.encode(request.getNewPw()))
                        .name(getTokenInfo().getName())
                        .authorities(Collections.singleton(authority))
                        .gender(getTokenInfo().getGender())
                        .height(getTokenInfo().getHeight())
                        .weight(getTokenInfo().getWeight())
                        .age(getTokenInfo().getAge())
                        .userData(getTokenInfo().getUserData())
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );
        return new ResponseEntity<>(PASSWORD_CHANGE_STATUS_TRUE, HttpStatus.OK);
    }

    // 비밀번호 잃어버린경우
    @Transactional
    public ResponseEntity<StatusTrue> PwLostChange(PwChangeDto.lostRequest request) {

        Optional<String> email = Optional.of(request.getEmail());
        TokenInfoResponseDto userInfo = TokenInfoResponseDto.Response(
                Objects.requireNonNull(email
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
        PWLOSTCHANGE_VALIDATION(request, userInfo.getPw());


        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        userRepository.save(
                FybUser.builder()
                        .id(userInfo.getId())
                        .email(userInfo.getEmail())
                        .pw(passwordEncoder.encode(request.getNewPw()))
                        .name(userInfo.getName())
                        .authorities(Collections.singleton(authority))
                        .gender(userInfo.getGender())
                        .height(userInfo.getHeight())
                        .weight(userInfo.getWeight())
                        .age(userInfo.getAge())
                        .userData(userInfo.getUserData())
                        .createAt(userInfo.getCreateAt())
                        .build()
        );
        return new ResponseEntity<>(PASSWORD_CHANGE_STATUS_TRUE, HttpStatus.OK);
    }

    // 회원탈퇴
    @Transactional
    public ResponseEntity<StatusTrue> delete(PwDeleteDto.Request request) {

        if (!passwordEncoder.matches(request.getPw(), getTokenInfo().getPw())) {
            throw new CustomException(USER_DELETE_STATUS_FALSE);
        }
        userRepository.deleteById(getTokenInfo().getId());

        return new ResponseEntity<>(USER_DELETE_STATUS_TRUE, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> model() {
        Map<String, String> response = new HashMap<>();
        response.put("userData", getTokenInfo().getUserData());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> reissue(String rtk) {
        Map<String, String> response = new HashMap<>();
        String username = tokenProvider.getRefreshTokenInfo(rtk);
        String rtkInRedis = redisDao.getValues(username);
        if (Objects.isNull(rtkInRedis) || !rtkInRedis.equals(rtk))
            throw new CustomException(REFRESH_TOKEN_IS_BAD_REQUEST);
        response.put("atk", tokenProvider.reCreateToken(username));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
