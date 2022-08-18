package school.bonobono.fyb.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.*;
import school.bonobono.fyb.Entity.Authority;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Exception.CustomErrorCode;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.constant.Constable;
import java.util.*;

import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    public static final CustomErrorCode JWT_CREDENTIALS_STATUS_FALSE = CustomErrorCode.JWT_CREDENTIALS_STATUS_FALSE;
    public static final CustomErrorCode PASSWORD_CHANGE_STATUS_FALSE = CustomErrorCode.PASSWORD_CHANGE_STATUS_FALSE;
    public static final CustomErrorCode USER_DELETE_STATUS_FALSE = CustomErrorCode.USER_DELETE_STATUS_FALSE;
    public static final CustomErrorCode NOT_FOUND_USER = CustomErrorCode.NOT_FOUND_USER;
    public static final CustomErrorCode DUPLICATE_USER = CustomErrorCode.DUPLICATE_USER;
    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // validate 및 단순 메소드화
    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    private void tokenCredEntialsValidate(HttpServletRequest request) {
        tokenRepository
                .findById(request.getHeader(AUTHORIZATION_HEADER))
                .orElseThrow(
                        () -> new CustomException(JWT_CREDENTIALS_STATUS_FALSE)
                );
    }

    // Service

    // 회원가입
    @Transactional
    public FybUser registerUser(UserRegisterDto.Request request) {

        if(userRepository.existsByEmail(request.getEmail()))
            throw new CustomException(DUPLICATE_USER);

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
        tokenCredEntialsValidate(headerRequest);

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
        tokenCredEntialsValidate(headerRequest);

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        userRepository.save(
                FybUser.builder()
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(getTokenInfo().getPw())
                        .name(request.getName())
                        .authorities(Collections.singleton(authority))
                        .gender(request.getGender())
                        .height(request.getHeight())
                        .weight(request.getWeight())
                        .age(request.getAge())
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );

        return StatusTrue.UPDATE_STATUS_TURE;
    }

    @Transactional
    public Constable logoutUser(HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        String getToken = headerRequest.getHeader(AUTHORIZATION_HEADER);
        tokenRepository.deleteById(getToken);
        return StatusTrue.LOGOUT_STATUS_TRUE;
    }

    // 휴대폰 인증
    public Map<Object, Object> certifiedPhoneNumber(PhoneCheckDto.Request request, String randNum) {
        String api_key = "NCSBDTMXRMDGUIFD";
        String api_secret = "S917YGKP2H2IFYE0P9ONJBTFA2EDCV3J";
        Message coolsms = new Message(api_key, api_secret);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", request.getPnum());
        params.put("from", "010-4345-4377");
        params.put("type", "SMS");
        params.put("text", "FYB 휴대폰인증 : 인증번호는" + "[" + randNum + "]" + "입니다.");
        params.put("app_version", "test app 1.2");

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }

        Map<Object, Object> send = new HashMap<>();
        send.put("randNum", randNum);
        return send;
    }

    public Constable PwChangeUser(PwChangeDto.Request request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_USER)
                );

        if (!passwordEncoder.matches(request.getPw(), getTokenInfo().getPw())) {
            throw new CustomException(PASSWORD_CHANGE_STATUS_FALSE);
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        userRepository.save(
                FybUser.builder()
                        .id(getTokenInfo().getId())
                        .email(getTokenInfo().getEmail())
                        .pw(passwordEncoder.encode(request.getNewPw()))
                        .name(getTokenInfo().getName())
                        .authorities(Collections.singleton(authority))
                        .gender(getTokenInfo().getGender())
                        .height(getTokenInfo().getHeight())
                        .weight(getTokenInfo().getWeight())
                        .age(getTokenInfo().getAge())
                        .createAt(getTokenInfo().getCreateAt())
                        .build()
        );
        return StatusTrue.PASSWORD_CHANGE_STATUS_TRUE;
    }

    public Constable PwLostChange(PwChangeDto.lostRequest request) {

        userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_USER)
                );

        Optional<String> email = Optional.of(request.getEmail());

        TokenInfoResponseDto userInfo = TokenInfoResponseDto.Response(
                Objects.requireNonNull(email
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        userRepository.save(
                FybUser.builder()
                        .id(userInfo.getId())
                        .email(userInfo.getEmail())
                        .pw(passwordEncoder.encode(request.getNewPw()))
                        .name(userInfo.getName())
                        .authorities(Collections.singleton(authority))
                        .gender(userInfo.getGender())
                        .height(userInfo.getHeight())
                        .weight(userInfo.getWeight())
                        .age(userInfo.getAge())
                        .createAt(userInfo.getCreateAt())
                        .build()
        );
        return StatusTrue.PASSWORD_CHANGE_STATUS_TRUE;
    }

    public Constable delete(PwDeleteDto.Request request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        if (!passwordEncoder.matches(request.getPw(), getTokenInfo().getPw())) {
            throw new CustomException(USER_DELETE_STATUS_FALSE);
        }
        userRepository.deleteById(getTokenInfo().getId());

        return StatusTrue.USER_DELETE_STATUS_TRUE;
    }
}
