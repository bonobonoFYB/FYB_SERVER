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
import school.bonobono.fyb.domain.user.OAuth.GoogleOAuth;
import school.bonobono.fyb.domain.user.OAuth.KakaoOAuth;
import school.bonobono.fyb.global.Config.Redis.RedisDao;
import school.bonobono.fyb.domain.user.Dto.*;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Config.Jwt.TokenProvider;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Config.Jwt.SecurityUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

import static school.bonobono.fyb.global.Exception.CustomErrorCode.REGISTER_INFO_NULL;
import static school.bonobono.fyb.global.Model.Model.AUTHORIZATION_HEADER;
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
    UsernamePasswordAuthenticationToken authenticationToken = null;
    private final RedisDao redisDao;

    // validate ??? ?????? ?????????
    Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

    private static void socialRegisterValidate(UserRegisterDto.socialRequest request) {
        if (request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(REGISTER_INFO_NULL);
    }

    private ResponseEntity<StatusTrue> Login(String email) {
        if (email.contains("gmail")) {
            authenticationToken = new UsernamePasswordAuthenticationToken(email, "google");
        }
        if (email.contains("daum")) {
            authenticationToken = new UsernamePasswordAuthenticationToken(email, "kakao");
        }

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String atk = tokenProvider.createToken(authentication);
        String rtk = tokenProvider.createRefreshToken(email);

        redisDao.setValues(email, rtk, Duration.ofDays(14));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + atk);

        return new ResponseEntity<>(LOGIN_STATUS_TRUE, httpHeaders, HttpStatus.OK);
    }

    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    private KakaoUserInfoDto getKakaoUserInfoDto(String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
        KakaoOAuthTokenDto oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
        KakaoUserInfoDto kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
        return kakaoUser;
    }

    private GoogleUserInfoDto getGoogleUserInfoDto(String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = googleOAuth.requestAccessToken(code);
        GoogleOAuthTokenDto oAuthToken = googleOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = googleOAuth.requestUserInfo(oAuthToken);
        GoogleUserInfoDto googleUser = googleOAuth.getUserInfo(userInfoResponse);
        return googleUser;
    }

    // Service
    // ?????? ????????? ?????????
    @Transactional
    public ResponseEntity<StatusTrue> googlelogin(String code) throws IOException {
        GoogleUserInfoDto googleUser = getGoogleUserInfoDto(code);
        String email = googleUser.getEmail();
        String name = googleUser.getName();
        // ????????????????????? ???????????? ???????????? ?????? ?????????
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(
                    FybUser.builder()
                            .email(email)
                            .pw(passwordEncoder.encode("google"))
                            .name(name)
                            .authorities(Collections.singleton(authority))
                            .gender(null)
                            .height(null)
                            .weight(null)
                            .age(null)
                            .build()
            );
            Login(email);
            return new ResponseEntity<>(SOCIAL_REGISTER_STATUS_TRUE, HttpStatus.OK);
        }
        // ????????????????????? ???????????? ???????????? ?????? ?????? ???????????? ??? ?????????
        return Login(email);
    }


    // ????????? ????????? ?????????
    @Transactional
    public ResponseEntity<StatusTrue> kakaoLogin(String code) throws IOException {
        KakaoUserInfoDto kakaoUser = getKakaoUserInfoDto(code);
        String email = kakaoUser.getKakao_account().getEmail();
        String name = kakaoUser.getProperties().getNickname();
        String profileImagePath = kakaoUser.getProperties().getProfile_image();

        // ????????????
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(
                    FybUser.builder()
                            .email(email)
                            .pw(passwordEncoder.encode("kakao"))
                            .name(name)
                            .authorities(Collections.singleton(authority))
                            .profileImagePath(profileImagePath)
                            .gender(null)
                            .height(null)
                            .weight(null)
                            .age(null)
                            .build()
            );
            Login(email);
            return new ResponseEntity<>(SOCIAL_REGISTER_STATUS_TRUE, HttpStatus.OK);
        }
        return Login(email);
    }


    // ?????? ?????? ?????? ?????????
    @Transactional
    public ResponseEntity<StatusTrue> socialRegister(UserRegisterDto.socialRequest request) {

        socialRegisterValidate(request);

        String bmiGrade;
        String userForm = request.getForm() + request.getPelvis() + request.getShoulder() + request.getLeg();

        double BMI = ((double)request.getWeight() / (double)request.getHeight() / (double)request.getHeight()) * 10000;
        if(BMI <= 18.5){
            bmiGrade = "A";
        }
        else if(BMI <= 22.9){
            bmiGrade = "B";
        }
        else if(BMI <= 24.9){
            bmiGrade = "C";
        }
        else if(BMI <= 29.9){
            bmiGrade = "D";
        }
        else {
            bmiGrade = "E";
        }

        userRepository.save(
                FybUser.builder()
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(getTokenInfo().getPw())
                        .name(getTokenInfo().getName())
                        .authorities(Collections.singleton(authority))
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
}
