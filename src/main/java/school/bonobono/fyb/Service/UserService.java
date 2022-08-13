package school.bonobono.fyb.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Dto.UserReadDto;
import school.bonobono.fyb.Dto.UserRegisterDto;
import school.bonobono.fyb.Dto.UserUpdateDto;
import school.bonobono.fyb.Entity.Authority;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Exception.DuplicateMemberException;
import school.bonobono.fyb.Model.StatusFalse;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.constant.Constable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public FybUser registerUser(UserRegisterDto.Request request) {
        if (userRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        return userRepository.save(
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
        );
    }

    // 내 정보 조회
    @Transactional
    public Object getMyInfo(HttpServletRequest headerRequest) {

        // 데이터 저장된 토큰 검증을 위한 Validation
        if (!tokenCredEntialsValidate(headerRequest))
            return StatusFalse.JWT_CREDENTIALS_STATUS_FALSE;

        // getCurrentUsername 은 해당 프젝에서는 email 임 !
        return UserReadDto.UserResponse.Response(
                Objects.requireNonNull(
                        SecurityUtil.getCurrentUsername()
                                .flatMap(
                                        userRepository
                                                ::findOneWithAuthoritiesByEmail
                                )
                                .orElse(null)
                )
        );
    }

    // 내 정보 수정

    @Transactional
    public Constable updateUser(UserUpdateDto.Request request, HttpServletRequest headerRequest) {

        // 데이터 저장된 토큰 검증을 위한 Validation
        if (!tokenCredEntialsValidate(headerRequest))
            return StatusFalse.JWT_CREDENTIALS_STATUS_FALSE;

        Long userid = getTokenInfo().getId();
        String userpw = getTokenInfo().getPw();
        String useremail = getTokenInfo().getEmail();
        LocalDateTime localDateTime = getTokenInfo().getCreateAt();

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

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
        );

        return StatusTrue.UPDATE_STATUS_TURE;
    }

    @Transactional
    public Constable logoutUser(HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        if (!tokenCredEntialsValidate(headerRequest))
            return StatusFalse.JWT_CREDENTIALS_STATUS_FALSE;

        String getToken = headerRequest.getHeader(AUTHORIZATION_HEADER);
        tokenRepository.deleteById(getToken);
        return StatusTrue.LOGOUT_STATUS_TRUE;
    }


    // validate 및 단순 메소드화
    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    private Boolean tokenCredEntialsValidate(HttpServletRequest request) {
        String getToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!tokenRepository.existsById(getToken)) {
            return false;
        }
        return true;
    }
}
