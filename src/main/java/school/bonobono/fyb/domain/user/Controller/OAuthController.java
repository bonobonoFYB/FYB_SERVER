package school.bonobono.fyb.domain.user.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.domain.user.Dto.UserDto;
import school.bonobono.fyb.domain.user.Dto.UserRegisterDto;
import school.bonobono.fyb.domain.user.OAuth.GoogleOAuth;
import school.bonobono.fyb.domain.user.OAuth.KakaoOAuth;
import school.bonobono.fyb.domain.user.Service.OAuthService;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.CustomResponseEntity;
import school.bonobono.fyb.global.Model.Result;
import school.bonobono.fyb.global.Model.StatusTrue;

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
    public void redirectToKakaoLogin(HttpServletResponse response) {
        try {
            response.sendRedirect(kakaoOAuth.getKakaoLoginURL());
        } catch (IOException e) {
            throw new CustomException(Result.FAIL);
        }
    }

    @GetMapping("login/kakao")
    public CustomResponseEntity<UserDto.LoginDto> kakaoLogin(
            @RequestParam(name = "code") String code) throws JsonProcessingException {
        return CustomResponseEntity.success(oAuthService.kakaoLogin(code));
    }

    // 구글 로그인 창 접근
    @GetMapping("google")
    public void redirectToGoogleLogin (HttpServletResponse response) throws Exception {
        response.sendRedirect(googleOAuth.getGoogleLoginURL());
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
