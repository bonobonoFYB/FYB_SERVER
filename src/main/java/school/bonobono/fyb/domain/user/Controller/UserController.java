package school.bonobono.fyb.domain.user.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.user.Dto.*;
import school.bonobono.fyb.domain.user.Service.UserService;

import javax.validation.Valid;
import java.io.IOException;
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

    // 로그인
    @PostMapping("log")
    public ResponseEntity<StatusTrue> loginUser(@Valid @RequestBody UserLoginDto.Request request){
        return userService.loginUser(request);
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<StatusTrue> registerUser(
            @Valid @RequestBody final UserRegisterDto.Request request
    ) {
        return userService.registerUser(request);
    }

    // 로그인 만료시 atk 재발급
    @GetMapping
    public ResponseEntity<Map<String,String>> reissue(
            @RequestHeader(value = "REFRESH_TOKEN") String rtk
    ) {
        return userService.reissue(rtk);
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
    public ResponseEntity<List<UserReadDto.UserResponse>> getMyUserInfo(
    ) {
        return userService.getMyInfo();
    }

    // 내 정보 수정
    @PatchMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> updateUser(
            @Valid @RequestBody final UserUpdateDto.Request request
    ) {
        return userService.updateUser(request);
    }

    // 비밀번호 변경 ( 로그인 이후 )
    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> pwChangeUser(
            @Valid @RequestBody final PwChangeDto.Request request
    ) {
        return userService.PwChangeUser(request);
    }

    // 비밀번호 변경 ( 로그인 이전 )
    @PutMapping("/password")
    public ResponseEntity<StatusTrue> pwLostChange(
            @Valid @RequestBody final PwChangeDto.lostRequest request
    ) {
        return userService.PwLostChange(request);
    }

    // 로그아웃
    @DeleteMapping("logout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> logoutUser(
            @Valid @RequestBody final PwDeleteDto.Request2 request2
    ) {
        return userService.logoutUser(request2);
    }

    // 회원탈퇴
    @DeleteMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> deleteUser(
            @Valid @RequestBody final PwDeleteDto.Request request
    ) {
        return userService.delete(request);
    }

    // 3d 모델링을 위한 userdata 전송
    @GetMapping("model")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Map<String,String>> postUserData(
    ){
        return userService.model();
    }
}
