package school.bonobono.fyb.domain.user.Service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.user.Dto.UserDto;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Config.Jwt.TokenProvider;
import school.bonobono.fyb.global.Config.Redis.RedisDao;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.Result;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

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
    private final SmsService smsService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // Service
    @Transactional
    public UserDto.LoginDto loginUser(UserDto.LoginDto request) {
        LOGIN_VALIDATION(request);
        String atk = tokenProvider.createToken(getAuthentication(request.getEmail(), request.getPassword()));
        String rtk = tokenProvider.createRefreshToken(request.getEmail());
        redisDao.setValues(request.getEmail(), rtk, Duration.ofDays(14));
        FybUser user = getUser(request.getEmail());

        return UserDto.LoginDto.response(user, atk, rtk);
    }

    @Transactional
    public UserDto.LoginDto registerUser(UserDto.RegisterDto request) {
        REGISTER_VALIDATION(request);

        double BMI = calculateBMI(request);
        FybUser user = userRepository.save(
                FybUser.builder()
                        .email(request.getEmail())
                        .pw(passwordEncoder.encode(request.getPassword()))
                        .name(request.getName())
                        .authorities(getUserAuthority())
                        .gender(request.getGender())
                        .height(request.getHeight())
                        .weight(request.getWeight())
                        .age(request.getAge())
                        .userData(request.getGender() + getBmiGrade(BMI) + getBodyInformation(request))
                        .build()
        );

        String atk = tokenProvider.createToken(getAuthentication(request.getEmail(), request.getPassword()));
        String rtk = tokenProvider.createRefreshToken(request.getEmail());
        redisDao.setValues(request.getEmail(), rtk, Duration.ofDays(14));

        return UserDto.LoginDto.response(user, atk, rtk);
    }


    // 내 정보 조회

    @Transactional
    public UserDto.DetailDto updateImage(MultipartFile multipartFile, UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());
        String myProfileImagePath = uploadProfileImage(multipartFile, user.getEmail());
        user.uploadProfileImage(myProfileImagePath);
        return UserDto.DetailDto.response(user);
    }
    // 내 정보 수정

    @Transactional
    public UserDto.DetailDto getMyInfo(UserDetails userDetails) {
        return UserDto.DetailDto.response(getUser(userDetails.getUsername()));
    }
    // 로그아웃

    @Transactional
    public UserDto.DetailDto updateUser(UserDto.UpdateDto request, UserDetails userDetails) {
        UPDATE_VALIDATION(request);
        FybUser user = getUser(userDetails.getUsername());
        user.updateUserInfo(
                request.getName(), request.getGender(), request.getHeight(),
                request.getWeight(), request.getAge()
        );
        return UserDto.DetailDto.response(user);
    }
    // 휴대폰 인증

    @Transactional
    public UserDto.DetailDto logoutUser(String auth, UserDetails userDetails) {
        String atk = auth.substring(7);
        String email = userDetails.getUsername();

        if (redisDao.getValues(email) != null) {
            redisDao.deleteValues(email);
        }

        redisDao.setValues(atk, "logout", Duration.ofMillis(
                tokenProvider.getExpiration(atk)
        ));

        return new UserDto.DetailDto();
    }

    @Transactional
    public UserDto.PhoneVerificationDto certifiedPhoneNumber(UserDto.PhoneVerificationDto request, String randNum) throws CoolsmsException {
        // 핸드폰 번호 - 포함 13글자 지정
        PHONE_NUM_LENGTH_CHECK(request);
        smsService.sendMessage(request.getPhoneNumber(),randNum);
        return UserDto.PhoneVerificationDto.response(randNum);
    }

    @Transactional
    public UserDto.PasswordResetDto PwChangeUser(UserDto.PasswordResetDto request, UserDetails userDetails) {
        PWCHANGE_VALIDATION(request);
        FybUser user = getUser(userDetails.getUsername());
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        return new UserDto.PasswordResetDto();
    }


    // 비밀번호 잃어버린경우
    @Transactional
    public UserDto.LostPasswordResetDto PwLostChange(UserDto.LostPasswordResetDto request) {
        FybUser user = getUser(request.getEmail());
        PWLOSTCHANGE_VALIDATION(request, user.getPw());
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        return new UserDto.LostPasswordResetDto();
    }

    // 회원탈퇴
    @Transactional
    public UserDto.DetailDto delete(UserDto.WithdrawalDto request, UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());

        if (passwordEncoder.matches(request.getPassword(), user.getPw()) == false) {
            throw new CustomException(Result.USER_DELETE_STATUS_FALSE);
        }

        userRepository.delete(user);
        return UserDto.DetailDto.response(user);
    }
    @Transactional
    public UserDto.UserDataDto model(UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());
        return UserDto.UserDataDto.response(user.getUserData());
    }


    // validate 및 단순 메소드화
    @Transactional
    public UserDto.AccessTokenRefreshDto reissue(String refreshToken) {
        String email = tokenProvider.getRefreshTokenInfo(refreshToken);
        String refreshTokenInRedis = redisDao.getValues(email);

        if (Objects.isNull(refreshTokenInRedis) || !refreshTokenInRedis.equals(refreshToken))
            throw new CustomException(Result.REFRESH_TOKEN_IS_BAD_REQUEST);

        return UserDto.AccessTokenRefreshDto.response(tokenProvider.reCreateToken(email));
    }

    private void LOGIN_VALIDATION(UserDto.LoginDto request) {
        if (request.getPassword().equals("google"))
            throw new CustomException(Result.NOT_SOCIAL_LOGIN);

        if (!request.getEmail().contains("@"))
            throw new CustomException(Result.NOT_EMAIL_FORM);

        userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(Result.LOGIN_FALSE)
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                userRepository.findByEmail(request.getEmail())
                        .get()
                        .getPw()
        )
        ) {
            throw new CustomException(Result.LOGIN_FALSE);
        }
    }
    private static String getBmiGrade(double BMI) {
        String bmiGrade;
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
        return bmiGrade;
    }

    private static double calculateBMI(UserDto.RegisterDto request) {
        return ((double) request.getWeight() / (double) request.getHeight() / (double) request.getHeight()) * 10000;
    }

    private Set<Authority> getUserAuthority() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    private void PHONE_NUM_LENGTH_CHECK(UserDto.PhoneVerificationDto request) {
        if (!(request.getPhoneNumber().length() == 13)) {
            throw new CustomException(Result.PHONE_NUM_ERROR);
        }
    }

    private void REGISTER_VALIDATION(UserDto.RegisterDto request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getName() == null
                || request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(Result.REGISTER_INFO_NULL);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new CustomException(Result.DUPLICATE_USER);

        if (!request.getEmail().contains("@"))
            throw new CustomException(Result.NOT_EMAIL_FORM);

        if (!(request.getPassword().length() > 5))
            throw new CustomException(Result.PASSWORD_SIZE_ERROR);

        if (!(request.getPassword().contains("!") || request.getPassword().contains("@") || request.getPassword().contains("#")
                || request.getPassword().contains("$") || request.getPassword().contains("%") || request.getPassword().contains("^")
                || request.getPassword().contains("&") || request.getPassword().contains("*") || request.getPassword().contains("(")
                || request.getPassword().contains(")"))
        ) {
            throw new CustomException(Result.NOT_CONTAINS_EXCLAMATIONMARK);
        }
    }

    private void UPDATE_VALIDATION(UserDto.UpdateDto request) {
        if (request.getName() == null || request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(Result.UPDATE_INFO_NULL);
    }

    private void PWCHANGE_VALIDATION(UserDto.PasswordResetDto request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPassword(), getUser(request.getEmail()).getPw())) {
            throw new CustomException(Result.PASSWORD_CHANGE_STATUS_FALSE);
        }
    }

    private void PWLOSTCHANGE_VALIDATION(UserDto.LostPasswordResetDto request, String pw) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_USER)
                );
        if (passwordEncoder.matches(request.getNewPassword(), pw)) {
            throw new CustomException(Result.PASSWORD_IS_NOT_CHANGE);
        }
    }

    public Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private String uploadProfileImage(MultipartFile multipartFile, String email) {
        String profile_image_name = "profile/" + email;
        ObjectMetadata objMeta = new ObjectMetadata();
        try {
            objMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3Client.putObject(bucket, profile_image_name, multipartFile.getInputStream(), objMeta);
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new CustomException(Result.FAIL);
        }
        return amazonS3Client.getUrl(bucket, profile_image_name).toString();
    }

    private FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER)
        );
    }

    private static String getBodyInformation(UserDto.RegisterDto request) {
        return request.getForm() + request.getPelvis() + request.getShoulder() + request.getLeg();
    }
}
