package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.*;
import school.bonobono.fyb.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.constant.Constable;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

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
