package school.bonobono.fyb.domain.user.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.bonobono.fyb.domain.user.Dto.*;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.OAuth.GoogleOAuth;
import school.bonobono.fyb.domain.user.OAuth.KakaoOAuth;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Config.Jwt.SecurityUtil;
import school.bonobono.fyb.global.Config.Jwt.TokenProvider;
import school.bonobono.fyb.global.Config.Redis.RedisDao;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.Result;
import school.bonobono.fyb.global.Model.StatusTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static school.bonobono.fyb.global.Model.StatusTrue.*;

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
    // validate 및 단순 메소드

    private static void socialRegisterValidate(UserRegisterDto.socialRequest request) {
        if (request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(Result.REGISTER_INFO_NULL);
    }

    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    private KakaoDto.UserInfoDto getKakaoUserInfoDto(String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
        KakaoDto.OAuthTokenDto oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
        KakaoDto.UserInfoDto kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
        return kakaoUser;
    }

    private GoogleDto.UserInfoDto getGoogleUserInfoDto(String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = googleOAuth.requestAccessToken(code);
        GoogleDto.OAuthTokenDto oAuthToken = googleOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = googleOAuth.requestUserInfo(oAuthToken);
        GoogleDto.UserInfoDto googleUser = googleOAuth.getUserInfo(userInfoResponse);
        return googleUser;
    }

    // Service
    // 구글 로그인 서비스
    @Transactional
    public UserDto.LoginDto googlelogin(String code) throws IOException {
        GoogleDto.UserInfoDto googleUser = getGoogleUserInfoDto(code);
        String email = googleUser.getEmail();
        String name = googleUser.getName();

        // 회원가입
        if (userRepository.existsByEmail(email) == false) {
            userRepository.save(
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
        }

        String atk = issueAccessToken(email, "google");
        String rtk = tokenProvider.createRefreshToken(email);
        redisDao.setValues(email, rtk, Duration.ofDays(14));
        FybUser user = getUser(email);

        return UserDto.LoginDto.response(user, atk, rtk);
    }

    // 카카오 로그인 서비스
    @Transactional
    public UserDto.LoginDto kakaoLogin(String code) throws JsonProcessingException {
        KakaoDto.UserInfoDto kakaoUser = getKakaoUserInfoDto(code);
        String email = kakaoUser.getKakao_account().getEmail();
        String name = kakaoUser.getProperties().getNickname();
        String profileImagePath = kakaoUser.getProperties().getProfile_image();

        // 회원가입
        if (userRepository.existsByEmail(email) == false) {
            userRepository.save(
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
        }

        String atk = issueAccessToken(email, "kakao");
        String rtk = tokenProvider.createRefreshToken(email);
        redisDao.setValues(email, rtk, Duration.ofDays(14));
        FybUser user = getUser(email);

        return UserDto.LoginDto.response(user, atk, rtk);
    }


    // 추가 정보 요청 서비스
    @Transactional
    public ResponseEntity<StatusTrue> socialRegister(UserRegisterDto.socialRequest request) {

        socialRegisterValidate(request);

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
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(getTokenInfo().getPw())
                        .name(getTokenInfo().getName())
                        .authorities(getUserAuthority())
                        .gender(request.getGender())
                        .height(request.getHeight())
                        .weight(request.getWeight())
                        .age(request.getAge())
                        .userData(request.getGender() + bmiGrade + userForm)
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );
        return new ResponseEntity<>(SOCIAL_ADD_INFO_STAUTS_TRUE, HttpStatus.OK);
    }

    private FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(
                email).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER)
        );
    }
    private String issueAccessToken(String email, String google) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, google);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String atk = tokenProvider.createToken(authentication);
        return atk;
    }

    private Set<Authority> getUserAuthority() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }
}
