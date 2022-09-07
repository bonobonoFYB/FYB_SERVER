package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Config.GoogleOAuth;
import school.bonobono.fyb.Dto.*;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Service.OAuthService;
import school.bonobono.fyb.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.constant.Constable;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class UserController {

    private final OAuthService oauthService;
    private final GoogleOAuth googleoauth;

    private final UserService userService;

    // 구글 로그인 창 접근
    @GetMapping("google")
    public void getGoogleAuthUrl(HttpServletResponse response) throws Exception {
        response.sendRedirect(googleoauth.getOauthRedirectURL());
    }

    // 구글 로그인 이후
    @GetMapping("login/google")
    public ResponseEntity<StatusTrue> callback(
            @RequestParam(name = "code") String code) throws IOException {
        log.info("구글 API 서버 code : " + code);
        return oauthService.googlelogin(code);
    }

    // Sosial 로그인 이후 추가 정보 요청
    @PostMapping("social/register")
    public ResponseEntity<StatusTrue> socialRegister(
            @RequestBody final UserRegisterDto.socialRequest request
    ){
        return oauthService.socialRegister(request);
    }


    // 휴대폰 인증
    @PostMapping("check")
    public Map<Object, Object> certifiedPhoneNumber(
            @Valid @RequestBody final PhoneCheckDto.Request request
    ) throws CoolsmsException {
        return userService.certifiedPhoneNumber(request);
    }

    // 회원가입
    @PostMapping("register")
    public Constable registerUser(
            @Valid @RequestBody final UserRegisterDto.Request request
    ) {
        return userService.registerUser(request);
    }

    // 내 정보 조회
    @GetMapping("info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Object> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyInfo(request));
    }

    // 내 정보 수정
    @PutMapping("update")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable updateUser(
            @Valid @RequestBody final UserUpdateDto.Request request, HttpServletRequest headerRequest
    ) {
        return userService.updateUser(request, headerRequest);
    }

    // 비밀번호 변경 ( 로그인 이후 )
    @PostMapping("pwchange")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable pwChangeUser(
            @Valid @RequestBody final PwChangeDto.Request request, HttpServletRequest headerRequest
    ) {
        return userService.PwChangeUser(request, headerRequest);
    }

    // 비밀번호 변경 ( 로그인 이전 )
    @PostMapping("lost/pwchange")
    public Constable pwLostChange(
            @Valid @RequestBody final PwChangeDto.lostRequest request
    ) {
        return userService.PwLostChange(request);
    }

    // 로그아웃
    @PostMapping("logout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable logoutUser(HttpServletRequest headerRequest) {
        return userService.logoutUser(headerRequest);
    }

    // 회원탈퇴
    @PostMapping("delete")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable deleteUser(
            @Valid @RequestBody final PwDeleteDto.Request request, HttpServletRequest headerRequest
    ) {
        return userService.delete(request, headerRequest);
    }
}
