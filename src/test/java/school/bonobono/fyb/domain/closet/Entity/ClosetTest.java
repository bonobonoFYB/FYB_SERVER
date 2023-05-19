package school.bonobono.fyb.domain.closet.Entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClosetTest {

    @DisplayName("옷장 객체 안에 이미지 값을 수정한다.")
    @Test
    void updateImagePath() {
        // given
        Closet closet = Closet.builder()
                .productName("1")
                .productNotes("1_설명")
                .productKind("상의")
                .closetImagePath("test.png")
                .build();

        // when
        closet.updateImagePath("update.png");

        // then
        assertThat(closet.getClosetImagePath()).isEqualTo("update.png");
    }

    @DisplayName("옷장 객체 안의 이름, 내용, 종류를 수정한다.")
    @Test
    void updateCloset() {
        // given
        Closet closet = Closet.builder()
                .productName("1")
                .productNotes("1_설명")
                .productKind("상의")
                .closetImagePath("test.png")
                .build();

        // when
        closet.updateCloset("2", "2_설명", "하의");

        // then
        assertThat(closet)
                .extracting("productName", "productNotes", "productKind")
                .contains("2","2_설명","하의");
    }
}