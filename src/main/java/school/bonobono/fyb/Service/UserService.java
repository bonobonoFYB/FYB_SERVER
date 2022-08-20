package school.bonobono.fyb.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.*;
import school.bonobono.fyb.Entity.Authority;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.constant.Constable;
import java.util.*;

import static school.bonobono.fyb.Exception.CustomErrorCode.*;
import static school.bonobono.fyb.Model.Model.*;
import static school.bonobono.fyb.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

    private String randNum = "";
    // Service
    // 회원가입
    @Transactional
    public Constable registerUser(UserRegisterDto.Request request) {

        REGISTER_VALIDATION(request);


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
                        .build()
        );

        return REGISTER_STATUS_TRUE;
    }

    // 내 정보 조회
    @Transactional
    public Object getMyInfo(HttpServletRequest headerRequest) {

        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        // getCurrentUsername 은 해당 프젝에서는 email 임 !
        return UserReadDto.UserResponse.Response(
                Objects.requireNonNull(
                        SecurityUtil.getCurrentUsername()
                                .flatMap(
                                        userRepository
                                                ::findOneWithAuthoritiesByEmail
                                )
                                .orElse(null)
                )
        );
    }

    // 내 정보 수정
    @Transactional
    public Constable updateUser(UserUpdateDto.Request request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);
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
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );

        return UPDATE_STATUS_TURE;
    }

    // 로그아웃
    @Transactional
    public Constable logoutUser(HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        String getToken = headerRequest.getHeader(AUTHORIZATION_HEADER);
        tokenRepository.deleteById(getToken);
        return LOGOUT_STATUS_TRUE;
    }

    // 휴대폰 인증
    public Map<Object, Object> certifiedPhoneNumber(PhoneCheckDto.Request request) throws CoolsmsException {
        // 핸드폰 번호 - 포함 13글자 지정
        PHONE_NUM_LENGTH_CHECK(request);

        Random rand = new Random();

        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            randNum += ran;
        }

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
    public Constable PwChangeUser(PwChangeDto.Request request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);
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
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );
        return PASSWORD_CHANGE_STATUS_TRUE;
    }

    // 비밀번호 잃어버린경우
    public Constable PwLostChange(PwChangeDto.lostRequest request) {

        Optional<String> email = Optional.of(request.getEmail());
        TokenInfoResponseDto userInfo = TokenInfoResponseDto.Response(
                Objects.requireNonNull(email
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
        PWLOSTCHANGE_VALIDATION(request,userInfo.getPw());


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
                        .createAt(userInfo.getCreateAt())
                        .build()
        );
        return PASSWORD_CHANGE_STATUS_TRUE;
    }

    // 회원탈퇴
    public Constable delete(PwDeleteDto.Request request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        if (!passwordEncoder.matches(request.getPw(), getTokenInfo().getPw())) {
            throw new CustomException(USER_DELETE_STATUS_FALSE);
        }
        userRepository.deleteById(getTokenInfo().getId());

        return USER_DELETE_STATUS_TRUE;
    }

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

    private void tokenCredEntialsValidate(HttpServletRequest request) {
        tokenRepository
                .findById(request.getHeader(AUTHORIZATION_HEADER))
                .orElseThrow(
                        () -> new CustomException(JWT_CREDENTIALS_STATUS_FALSE)
                );
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

        if(passwordEncoder.matches(request.getNewPw(),pw)){
            throw new CustomException(PASSWORD_IS_NOT_CHANGE);
        }
    }
}
