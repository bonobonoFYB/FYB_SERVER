package school.bonobono.fyb.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Dto.UserReadDto;
import school.bonobono.fyb.Dto.UserRegisterDto;
import school.bonobono.fyb.Dto.UserUpdateDto;
import school.bonobono.fyb.Entity.Authority;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Exception.DuplicateMemberException;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public UserRegisterDto.Response registerUser(UserRegisterDto.Request request) {
        if (userRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        return UserRegisterDto.Response.register(
                userRepository.save(
                        FybUser.builder()
                                .email(request.getEmail())
                                .pw(passwordEncoder.encode(request.getPw()))
                                .name(request.getName())
                                .authorities(Collections.singleton(authority))
                                .gender(request.getGender())
                                .height(request.getHeight())
                                .weight(request.getWeight())
                                .age(request.getAge())
                                .build()
                )
        );
    }

    // 내 정보 조회
    @Transactional
    public UserReadDto.UserResponse getMyInfo() {
        // getCurrentUsername 은 해당 프젝에서는 email 임 !
        return UserReadDto.UserResponse.Response(SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail).orElse(null));
    }

    // 내 정보 수정

    @Transactional
    public UserUpdateDto.Response updateUser(UserUpdateDto.Request request) {
        Long userid = getTokenInfo().getId();
        String userpw = getTokenInfo().getPw();
        String useremail = getTokenInfo().getEmail();
        LocalDateTime localDateTime = getTokenInfo().getCreateAt();

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        return UserUpdateDto.Response.update(
                userRepository.save(
                        FybUser.builder()
                                .id(userid)
                                .email(useremail)
                                .pw(userpw)
                                .name(request.getName())
                                .authorities(Collections.singleton(authority))
                                .gender(request.getGender())
                                .height(request.getHeight())
                                .weight(request.getWeight())
                                .age(request.getAge())
                                .createAt(localDateTime)
                                .build()
                )
        );
    }

    // validate 및 단순 메소드화
    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail).orElse(null));
    }
}
