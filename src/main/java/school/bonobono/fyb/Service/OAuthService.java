package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.bonobono.fyb.Config.GoogleOAuth;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Dto.UserRegisterDto;
import school.bonobono.fyb.Entity.Authority;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Entity.GoogleOAuthToken;
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
    private final GoogleOAuth googleOAuth;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // validate 및 단순 메소드
    Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

    // Service
    private static void socialRegisterValidate(UserRegisterDto.socialRequest request) {
        if (request.getWeight() == null || request.getHeight() == null)
            throw new CustomException(REGISTER_INFO_NULL);
    }

    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    public ResponseEntity<StatusTrue> googlelogin(String code) throws IOException {
        // 구글로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
        ResponseEntity<String> accessTokenResponse = googleOAuth.requestAccessToken(code);
        // 응답 객체가 JSON형식으로 되어 있으므로, 이를 deserialization해서 자바 객체에 담을 것이다.
        GoogleOAuthToken oAuthToken = googleOAuth.getAccessToken(accessTokenResponse);
        // accessToken을 담은 후 accessToken 통신
        ResponseEntity<String> userInfoResponse = googleOAuth.requestUserInfo(oAuthToken);

        JSONParser jsonParser = new JSONParser();
        String email;
        String name;

        // json parse
        try {
            JSONObject jsonObj = (JSONObject) jsonParser.parse(userInfoResponse.getBody());
            email = (String) jsonObj.get("email");
            name = (String) jsonObj.get("name");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 데이터베이스에 이메일이 존재하는 경우 로그인
        if (userRepository.existsByEmail(email)) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, "google");

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

        // 데이터베이스에 이메일이 존재하지 않는 경우 회원가입
        else {
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

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, "google");

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication);

            // 토큰 유효성 검증을 위한 데이터 저장 (로그아웃을 위한 장치)
            tokenRepository.save(userToken.builder()
                    .token("Bearer " + jwt)
                    .build());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + jwt);

            return new ResponseEntity<>(GOOGLE_REGISTER_STATUS_TRUE, httpHeaders, HttpStatus.OK);
        }
    }

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
        return new ResponseEntity<>(SOCIAL_REGISTER_STATUS_TRUE, HttpStatus.OK);
    }
}
