package school.bonobono.fyb.domain.user.Entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FybUserTest {

    @DisplayName("유저 객체의 프로필 이미지를 수정한다.")
    @Test
    void uploadProfileImage() {
        // given
        FybUser user = getUser();

        // when
        user.uploadProfileImage("update_test.png");

        // then
        assertThat(user.getProfileImagePath()).isEqualTo("update_test.png");
    }

    @DisplayName("유저 객체의 이름, 성별, 키, 몸무게, 나이 정보를 수정한다.")
    @Test
    void updateUserInfo() {
        // given
        FybUser user = getUser();

        // when
        user.updateUserInfo("수정된 계정", 'W', 175, 64, 24);

        // then
        assertThat(user)
                .extracting("name", "gender", "height", "weight", "age")
                .contains("수정된 계정", 'W', 175, 64, 24);
    }

    @DisplayName("유저 객체의 비밀번호를 수정한다.")
    @Test
    void updatePassword() {
        // given
        FybUser user = getUser();

        // when
        user.updatePassword("abc124!");

        // then
        assertThat(user.getPw()).isEqualTo("abc124!");
    }

    // method
    private FybUser getUser() {
        return FybUser.builder()
                .email("test@test.com")
                .pw("abc123!")
                .name("테스트 계정")
                .gender('M')
                .height(180)
                .weight(70)
                .age(23)
                .userData("ABC")
                .build();
    }
}