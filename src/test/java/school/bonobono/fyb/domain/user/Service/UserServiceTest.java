package school.bonobono.fyb.domain.user.Service;

import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.user.Dto.UserDto;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Config.Jwt.TokenProvider;
import school.bonobono.fyb.global.Config.Redis.RedisDao;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {
    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RedisDao redisDao;

    @DisplayName("유저가 회원가입을 하고 토큰을 발급받는다.")
    @Test
    void userRegistration() {
        // given
        UserDto.RegisterDto request = UserDto.RegisterDto.builder()
                .email("test@test.com")
                .password("abc123!")
                .name("테스트 계정")
                .gender('M')
                .height(180)
                .weight(70)
                .age(23)
                .form("A")
                .pelvis("B")
                .shoulder("A")
                .leg("D")
                .build();

        // when
        UserDto.LoginDto response = userService.registerUser(request);

        // then
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

        Optional<FybUser> userOptional = userRepository.findByEmail("test@test.com");
        assertThat(userOptional.isPresent()).isTrue();

        FybUser user = userOptional.get();
        assertThat(user.getUserData()).isNotNull();
    }

    @DisplayName("유저가 로그인을 하고 토큰을 발급받는다.")
    @Test
    void userLogin() {
        // given
        FybUser user = getUserAndSave();

        UserDto.LoginDto request = UserDto.LoginDto.builder()
                .email("test@test.com")
                .password("abc123!")
                .build();

        // when
        UserDto.LoginDto response = userService.loginUser(request);

        // then
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@test.com");
    }

    @DisplayName("유저가 자신의 휴대폰으로 인증번호를 받고 해당 번호를 인증한다.")
    @Test
    void phoneVerification() throws CoolsmsException {
        // given
        UserDto.PhoneVerificationDto request = UserDto.PhoneVerificationDto.builder()
                .phoneNumber("010-1234-5678")
                .build();
        // when
        UserDto.PhoneVerificationDto response = userService.certifiedPhoneNumber(request, "123456");

        // then
        assertThat(response.getNumber()).isEqualTo("123456");
    }

    @DisplayName("비밀번호를 잃어버린 경우 새로운 비밀번호를 발급한다.")
    @Test
    void generateNewPassword() {
        // given
        FybUser user = getUserAndSave();
        String oldPassword = user.getPw();

        UserDto.LostPasswordResetDto request = UserDto.LostPasswordResetDto.builder()
                .email("test@test.com")
                .newPassword("abc124!")
                .build();

        // when
        UserDto.LostPasswordResetDto response = userService.PwLostChange(request);

        // then
        FybUser assertUser = userRepository.findByEmail("test@test.com").get();
        assertThat(assertUser.getPw()).isNotEqualTo(oldPassword);
    }

    @DisplayName("AccessToken 이 만료되었을경우 RefreshToken 을 재발급 받는다.")
    @Test
    void refreshAccessToken() {
        // given
        FybUser user = getUserAndSave();

        String refreshToken = tokenProvider.createRefreshToken(user.getEmail());
        redisDao.setValues(user.getEmail(), refreshToken, Duration.ofDays(14));

        // when
        UserDto.AccessTokenRefreshDto response = userService.reissue(refreshToken);

        // then
        assertThat(response.getAccessToken()).isNotNull();
    }

    @DisplayName("내 정보 조회")
    @Test
    void myInfoReadDetail() {
        // given
        FybUser userAndSave = getUserAndSave();

        // when
        UserDto.DetailDto response = userService.getMyInfo(userAndSave);

        // then
        assertThat(response.getEmail()).isEqualTo("test@test.com");
    }

    // method
    private Set<Authority> getUserAuthority() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    private FybUser getUserAndSave() {
        FybUser user = FybUser.builder()
                .email("test@test.com")
                .pw(passwordEncoder.encode("abc123!"))
                .name("테스트 계정")
                .gender('M')
                .height(180)
                .weight(70)
                .age(23)
                .authorities(getUserAuthority())
                .userData("ABC")
                .build();
        return userRepository.save(user);
    }
}