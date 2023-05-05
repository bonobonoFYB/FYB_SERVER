package school.bonobono.fyb.domain.user.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.bonobono.fyb.domain.user.Dto.UserDto;
import school.bonobono.fyb.domain.user.Service.UserService;
import school.bonobono.fyb.global.Model.CustomResponseEntity;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class UserController {

    private final UserService userService;

    // 로그인
    @PostMapping("log")
    public CustomResponseEntity<UserDto.LoginDto> loginUser(@Valid @RequestBody UserDto.LoginDto request) {
        return CustomResponseEntity.success(userService.loginUser(request));
    }

    // 회원가입
    @PostMapping
    public CustomResponseEntity<UserDto.LoginDto> registerUser(
            @Valid @RequestBody final UserDto.RegisterDto request
    ) {
        return CustomResponseEntity.success(userService.registerUser(request));
    }

    // 휴대폰 인증
    @PostMapping("check")
    public CustomResponseEntity<UserDto.PhoneVerificationDto> certifiedPhoneNumber(
            @Valid @RequestBody final UserDto.PhoneVerificationDto request
    ) throws CoolsmsException {
        return CustomResponseEntity.success(userService.certifiedPhoneNumber(request));
    }

    // 로그인 만료시 atk 재발급
    @GetMapping
    public CustomResponseEntity<UserDto.AccessTokenRefreshDto> reissue(
            @RequestHeader(value = "REFRESH_TOKEN") String refreshToken
    ) {
        return CustomResponseEntity.success(userService.reissue(refreshToken));
    }

    // 프로필 이미지 설정
    @PutMapping("image")
    public CustomResponseEntity<UserDto.DetailDto> updateImage(
            @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.updateImage(multipartFile, userDetails));
    }

    // 내 정보 조회
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<UserDto.DetailDto> getMyUserInfo(
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.getMyInfo(userDetails));
    }

    // 내 정보 수정
    @PatchMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<UserDto.DetailDto> updateUser(
            @Valid @RequestBody final UserDto.UpdateDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.updateUser(request, userDetails));
    }

    // 비밀번호 변경 ( 로그인 이후 )
    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<UserDto.PasswordResetDto> pwChangeUser(
            @Valid @RequestBody final UserDto.PasswordResetDto request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.PwChangeUser(request, userDetails));
    }

    // 비밀번호 변경 ( 로그인 이전 )
    @PutMapping("/password")
    public CustomResponseEntity<UserDto.LostPasswordResetDto> pwLostChange(
            @Valid @RequestBody final UserDto.LostPasswordResetDto request
    ) {
        return CustomResponseEntity.success(userService.PwLostChange(request));
    }

    // 로그아웃
    @DeleteMapping("logout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<UserDto.DetailDto> logoutUser(
            @RequestHeader(value = "Authorization") final String auth,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.logoutUser(auth, userDetails));
    }

    // 회원탈퇴
    @DeleteMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<UserDto.DetailDto> deleteUser(
            @Valid @RequestBody final UserDto.WithdrawalDto request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.delete(request,userDetails));
    }

    // 3d 모델링을 위한 userdata 전송
    @GetMapping("model")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<UserDto.UserDataDto> postUserData(
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return CustomResponseEntity.success(userService.model(userDetails));
    }
}
