package school.bonobono.fyb.Service;

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
import school.bonobono.fyb.Config.GoogleOAuth;
import school.bonobono.fyb.Config.KakaoOAuth;
import school.bonobono.fyb.Dto.*;
import school.bonobono.fyb.Entity.Authority;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Entity.userToken;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Jwt.TokenProvider;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static school.bonobono.fyb.Exception.CustomErrorCode.REGISTER_INFO_NULL;
import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;
import static school.bonobono.fyb.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoOAuth kakaoOAuth;
    private final GoogleOAuth googleOAuth;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    UsernamePasswordAuthenticationToken authenticationToken = null;

    // validate 및 단순 메소드
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
        String jwt = tokenProvider.createToken(authentication);

        // 토큰 유효성 검증을 위한 데이터 저장 (로그아웃을 위한 장치)
        tokenRepository.save(userToken.builder()
                .token("Bearer " + jwt)
                .build());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + jwt);

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
    // 구글 로그인 서비스
    @Transactional
    public ResponseEntity<StatusTrue> googlelogin(String code) throws IOException {
        GoogleUserInfoDto googleUser = getGoogleUserInfoDto(code);
        String email = googleUser.getEmail();
        String name = googleUser.getName();
        // 데이터베이스에 이메일이 존재하는 경우 로그인
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
        // 데이터베이스에 이메일이 존재하지 않는 경우 회원가입 후 로그인
        return Login(email);
    }


    // 카카오 로그인 서비스
    @Transactional
    public ResponseEntity<StatusTrue> kakaoLogin(String code) throws IOException {
        KakaoUserInfoDto kakaoUser = getKakaoUserInfoDto(code);
        String email = kakaoUser.getKakao_account().getEmail();
        String name = kakaoUser.getProperties().getNickname();
        String profileImagePath = kakaoUser.getProperties().getProfile_image();

        // 회원가입
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


    // 추가 정보 요청 서비스
    @Transactional
    public ResponseEntity<StatusTrue> socialRegister(UserRegisterDto.socialRequest request) {

        socialRegisterValidate(request);

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
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );
        return new ResponseEntity<>(SOCIAL_ADD_INFO_STAUTS_TRUE, HttpStatus.OK);
    }
}
