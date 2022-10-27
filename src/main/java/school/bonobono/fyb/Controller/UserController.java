package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.Dto.*;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.constant.Constable;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
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
    @PostMapping
    public ResponseEntity<StatusTrue> registerUser(
            @Valid @RequestBody final UserRegisterDto.Request request
    ) {
        return userService.registerUser(request);
    }

    // 프로필 이미지 설정
    @PutMapping("image")
    public ResponseEntity<StatusTrue> updateImage(
            @RequestParam("file") MultipartFile multipartFile
    ) throws IOException {
        return userService.updateImage(multipartFile);
    }

    // 내 정보 조회
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<UserReadDto.UserResponse>> getMyUserInfo(HttpServletRequest request) {
        return userService.getMyInfo(request);
    }

    // 내 정보 수정
    @PatchMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable updateUser(
            @Valid @RequestBody final UserUpdateDto.Request request, HttpServletRequest headerRequest
    ) {
        return userService.updateUser(request, headerRequest);
    }

    // 비밀번호 변경 ( 로그인 이후 )
    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable pwChangeUser(
            @Valid @RequestBody final PwChangeDto.Request request, HttpServletRequest headerRequest
    ) {
        return userService.PwChangeUser(request, headerRequest);
    }

    // 비밀번호 변경 ( 로그인 이전 )
    @PutMapping("/password")
    public Constable pwLostChange(
            @Valid @RequestBody final PwChangeDto.lostRequest request
    ) {
        return userService.PwLostChange(request);
    }

    // 로그아웃
    @DeleteMapping("logout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable logoutUser(HttpServletRequest headerRequest) {
        return userService.logoutUser(headerRequest);
    }

    // 회원탈퇴
    @DeleteMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable deleteUser(
            @Valid @RequestBody final PwDeleteDto.Request request, HttpServletRequest headerRequest
    ) {
        return userService.delete(request, headerRequest);
    }

    // 3d 모델링을 위한 userdata 전송
    @GetMapping("model")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Map<String,String>> postUserData(HttpServletRequest headerRequest){
        return userService.model(headerRequest);
    }
}
