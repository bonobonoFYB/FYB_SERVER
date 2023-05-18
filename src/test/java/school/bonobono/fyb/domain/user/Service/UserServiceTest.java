package school.bonobono.fyb.domain.user.Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.user.Dto.UserDto;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.aws.service.S3Service;
import school.bonobono.fyb.global.config.Jwt.TokenProvider;
import school.bonobono.fyb.global.config.Redis.RedisDao;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {
    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @MockBean
    private SmsService smsService;

    @MockBean
    private S3Service s3Service;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

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
    void phoneVerification() {
        // given
        UserDto.PhoneVerificationDto request = UserDto.PhoneVerificationDto.builder()
                .phoneNumber("010-4345-4377")
                .build();

        given(smsService.sendMessage(anyString(), anyString()))
                .willReturn(true);

        // when
        UserDto.PhoneVerificationDto response = userService.certifiedPhoneNumber(request, "123456");

        // then
        assertThat(response.getNumber()).isEqualTo("123456");
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

    @DisplayName("사용자에게 파일을 요청받아 서버에 업로드한다.")
    @Test
    void uploadImage() {
        // given
        FybUser user = getUserAndSave();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Spring Framework".getBytes()
        );

        given(s3Service.uploadProfileImage(multipartFile, user.getEmail()))
                .willReturn("s3/profile/test.png");

        // when
        UserDto.DetailDto response = userService.updateImage(multipartFile, user);

        // then
        assertThat(response.getProfileImagePath()).isEqualTo("s3/profile/test.png");
    }

    @DisplayName("유저가 자신의 정보를 조회한다.")
    @Test
    void getMyUserInfo() {
        // given
        FybUser user = getUserAndSave();

        // when
        UserDto.DetailDto response = userService.getMyInfo(user);

        // then
        assertThat(response)
                .extracting("email", "name", "gender", "age")
                .contains("test@test.com", "테스트 계정", 'M', 23);
    }

    @DisplayName("유저가 자신의 정보를 수정한다.")
    @Test
    void updateUser() {
        // given
        FybUser user = getUserAndSave();

        UserDto.UpdateDto request = UserDto.UpdateDto.builder()
                .name("업데이트 테스트 계정")
                .gender('W')
                .height(175)
                .weight(64)
                .age(24)
                .build();
        // when
        userService.updateUser(request, user);

        // then
        Optional<FybUser> userOptional = userRepository.findByEmail(user.getEmail());

        assertThat(userOptional.isPresent()).isTrue();
        assertThat(userOptional.get())
                .extracting("name", "gender", "height", "weight", "age")
                .contains("업데이트 테스트 계정", 'W', 175, 64, 24);
    }

    @DisplayName("유저가 로그인을 한 상태에서 비밀번호를 변경한다.")
    @Test
    void changePasswordWhileLoggedIn() {
        // given
        FybUser user = getUserAndSave();
        String oldPassword = user.getPw();
        UserDto.PasswordResetDto request = UserDto.PasswordResetDto.builder()
                .email("test@test.com")
                .password("abc123!")
                .newPassword("abc124!")
                .build();

        // when
        userService.changePasswordWhileLoggedIn(request, user);

        // then
        Optional<FybUser> userOptional = userRepository.findByEmail(user.getEmail());
        assertThat(userOptional.isPresent()).isTrue();
        assertThat(oldPassword).isNotEqualTo(userOptional.get().getPw());
    }

    @DisplayName("유저가 비밀번호를 잃어버렸을때 패스워드를 변경한다.")
    @Test
    void resetLostPassword() {
        // given
        FybUser user = getUserAndSave();
        String oldPassword = user.getPw();

        UserDto.LostPasswordResetDto request = UserDto.LostPasswordResetDto.builder()
                .email("test@test.com")
                .newPassword("abc124!")
                .build();

        // when
        userService.resetLostPassword(request);

        // then
        Optional<FybUser> userOptional = userRepository.findByEmail(user.getEmail());
        assertThat(userOptional.isPresent()).isTrue();
        assertThat(oldPassword).isNotEqualTo(userOptional.get().getPw());
    }

    @DisplayName("유저가 로그아웃을 하면 액세스 토큰이 블랙리스트로 redis 에 저장된다.")
    @Test
    void logoutUser() {
        // given
        FybUser user = getUserAndSave();
        Authentication authentication = saveSecurityContextHolderAndGetAuthentication();

        String testAccessToken = tokenProvider.createToken(user, authentication);
        redisDao.setValues(user.getEmail(), "testRefreshToken");

        // when
        userService.logoutUser(testAccessToken, user);

        // then
        assertThat(redisDao.getValues(user.getEmail())).isNull();
        assertThat(redisDao.getValues(testAccessToken)).isEqualTo("logout");
    }

    @DisplayName("유저가 회원탈퇴를 한다.")
    @Test
    void deleteUser() {
        // given
        FybUser user = getUserAndSave();
        UserDto.WithdrawalDto request = UserDto.WithdrawalDto.builder()
                .password("abc123!")
                .build();

        // when
        UserDto.DetailDto response = userService.deleteUser(request, user);

        // then
        assertThat(response.getEmail()).isEqualTo("test@test.com");

        Optional<FybUser> userOptional = userRepository.findByEmail(user.getEmail());
        assertThat(userOptional.isEmpty()).isTrue();
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

    private Authentication saveSecurityContextHolderAndGetAuthentication() {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("test@test.com", "abc123!");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}