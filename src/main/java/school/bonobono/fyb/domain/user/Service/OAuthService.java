package school.bonobono.fyb.domain.user.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.user.Dto.*;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.OAuth.GoogleOAuth;
import school.bonobono.fyb.domain.user.OAuth.KakaoOAuth;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.config.Jwt.TokenProvider;
import school.bonobono.fyb.global.config.Redis.RedisDao;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoOAuth kakaoOAuth;
    private final GoogleOAuth googleOAuth;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisDao redisDao;

    // Service
    // 구글 로그인 서비스
    @Transactional
    public UserDto.LoginDto googleLogin(String code) throws IOException {
        GoogleDto.UserInfoDto googleUser = getGoogleUserInfoDto(code);
        String email = googleUser.getEmail();
        String name = googleUser.getName();
        Optional<FybUser> userOptional = userRepository.findByEmail(googleUser.getEmail());

        // 회원가입
        if (userOptional.isPresent() == false) {
            FybUser user = userRepository.save(
                    FybUser.builder()
                            .email(email)
                            .pw(passwordEncoder.encode("google"))
                            .name(name)
                            .authorities(getUserAuthority())
                            .profileImagePath(null)
                            .gender(null)
                            .height(null)
                            .weight(null)
                            .age(null)
                            .build()
            );
            userOptional = Optional.of(user);
        }

        FybUser user = userOptional.get();
        String atk = issueAccessToken(user, email, "google");
        String rtk = tokenProvider.createRefreshToken(email);
        redisDao.setValues(email, rtk, Duration.ofDays(14));

        return UserDto.LoginDto.response(user, atk, rtk);
    }

    // 카카오 로그인 서비스
    @Transactional
    public UserDto.LoginDto kakaoLogin(String code) throws JsonProcessingException {
        KakaoDto.UserInfoDto kakaoUser = getKakaoUserInfoDto(code);
        String email = kakaoUser.getKakao_account().getEmail();
        String name = kakaoUser.getProperties().getNickname();
        String profileImagePath = kakaoUser.getProperties().getProfile_image();
        Optional<FybUser> userOptional = userRepository.findByEmail(email);
        // 회원가입
        if (userOptional.isPresent() == false) {
            FybUser user = userRepository.save(
                    FybUser.builder()
                            .email(email)
                            .pw(passwordEncoder.encode("kakao"))
                            .name(name)
                            .authorities(getUserAuthority())
                            .profileImagePath(profileImagePath)
                            .gender(null)
                            .height(null)
                            .weight(null)
                            .age(null)
                            .build()
            );
            userOptional = Optional.of(user);
        }

        FybUser user = userOptional.get();
        String atk = issueAccessToken(user, email, "kakao");
        String rtk = tokenProvider.createRefreshToken(email);
        redisDao.setValues(email, rtk, Duration.ofDays(14));

        return UserDto.LoginDto.response(user, atk, rtk);
    }

    @Transactional
    public UserDto.DetailDto socialRegister(UserDto.SocialRegisterDto request, UserDetails userDetails) {
        socialRegisterValidate(request);
        double BMI = calculateBMI(request);
        String bodyInformation = request.getForm() + request.getPelvis() + request.getShoulder() + request.getLeg();
        String userData = request.getGender() + getBmiGrade(BMI) + bodyInformation;

        FybUser user = getUser(userDetails.getUsername());
        user.saveUserBodyInformation(
                request.getGender(), request.getHeight(), request.getWeight(),
                request.getAge(), userData);

        return UserDto.DetailDto.response(user);
    }

    // validate 및 단순 메소드
    private static void socialRegisterValidate(UserDto.SocialRegisterDto request) {
        if (request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(Result.REGISTER_INFO_NULL);
    }

    private FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(
                email).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER)
        );
    }

    private String issueAccessToken(FybUser user, String email, String google) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, google);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createToken(user, authentication);
    }

    private Set<Authority> getUserAuthority() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    private KakaoDto.UserInfoDto getKakaoUserInfoDto(String code) throws JsonProcessingException {
        ResponseEntity<String> AccessTokenEntity = kakaoOAuth.requestAccessToken(code);
        KakaoDto.OAuthTokenDto AccessTokenDto = kakaoOAuth.getAccessToken(AccessTokenEntity);
        ResponseEntity<String> UserInfoEntity = kakaoOAuth.requestUserInfo(AccessTokenDto);
        return kakaoOAuth.getUserInfo(UserInfoEntity);
    }

    private GoogleDto.UserInfoDto getGoogleUserInfoDto(String code) throws JsonProcessingException {
        ResponseEntity<String> AccessTokenEntity = googleOAuth.requestAccessToken(code);
        GoogleDto.OAuthTokenDto AccessTokenDto = googleOAuth.getAccessToken(AccessTokenEntity);
        ResponseEntity<String> UserInfoEntity = googleOAuth.requestUserInfo(AccessTokenDto);
        return googleOAuth.getUserInfo(UserInfoEntity);
    }

    private static double calculateBMI(UserDto.SocialRegisterDto request) {
        double BMI = ((double) request.getWeight() / (double) request.getHeight() / (double) request.getHeight()) * 10000;
        return BMI;
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
}
