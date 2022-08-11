package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.UserRegisterDto;
import school.bonobono.fyb.Dto.UserUpdateDto;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.constant.Constable;

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
    public ResponseEntity<Object> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyInfo(request));
    }

    @PutMapping("update")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable updateUser(
            @Valid @RequestBody final UserUpdateDto.Request request, HttpServletRequest headerRequest
    ) {

        return userService.updateUser(request, headerRequest);
    }

    @PostMapping("logout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable logoutUser(HttpServletRequest headerRequest) {
        return userService.logoutUser(headerRequest);
    }


}
