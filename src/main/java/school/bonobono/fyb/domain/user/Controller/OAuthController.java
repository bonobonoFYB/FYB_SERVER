package school.bonobono.fyb.domain.user.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.domain.user.OAuth.GoogleOAuth;
import school.bonobono.fyb.domain.user.OAuth.KakaoOAuth;
import school.bonobono.fyb.domain.user.Dto.UserRegisterDto;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.user.Service.OAuthService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;
    private final KakaoOAuth kakaoOAuth;
    private final GoogleOAuth googleOAuth;

    @GetMapping("/kakao")
    public void getKakakoAuthUrl(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoOAuth.responseUrl());
    }

    @GetMapping("login/kakao")
    public ResponseEntity<StatusTrue> kakaoLogin(
            @RequestParam(name = "code") String code) throws IOException {
        return oAuthService.kakaoLogin(code);
    }

    // 구글 로그인 창 접근
    @GetMapping("google")
    public void getGoogleAuthUrl(HttpServletResponse response) throws Exception {
        response.sendRedirect(googleOAuth.getOauthRedirectURL());
    }

    // 구글 로그인 이후
    @GetMapping("login/google")
    public ResponseEntity<StatusTrue> callback(
            @RequestParam(name = "code") String code) throws IOException {
        return oAuthService.googlelogin(code);
    }

    // Sosial 로그인 이후 추가 정보 요청
    @PostMapping("/")
    public ResponseEntity<StatusTrue> socialRegister(
            @RequestBody final UserRegisterDto.socialRequest request
    ) {
        return oAuthService.socialRegister(request);
    }
}
