package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.UserReadDto;
import school.bonobono.fyb.Dto.UserRegisterDto;
import school.bonobono.fyb.Dto.UserUpdateDto;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public StatusTrue registerUser(
            @Valid @RequestBody final UserRegisterDto.Request request
    ) {
        userService.registerUser(request);
        return StatusTrue.REGISTER_STATUS_TRUE;
    }

    @GetMapping("info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserReadDto.UserResponse> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    @PutMapping("update")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public StatusTrue updateUser(
            @Valid @RequestBody final UserUpdateDto.Request request
    ) {
        userService.updateUser(request);
                return StatusTrue.UPDATE_STATUS_TURE;
    }
}
