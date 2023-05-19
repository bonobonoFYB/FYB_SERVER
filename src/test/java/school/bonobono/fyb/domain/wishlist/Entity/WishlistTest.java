package school.bonobono.fyb.domain.wishlist.Entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WishlistTest {

    @DisplayName("장바구니 객체의 이름, 내용, 가격, URL 을 수정한다.")
    @Test
    void updateWishlist() {
        // given
        Wishlist wishlist = Wishlist.builder()
                .productName("1")
                .productNotes("1_설명")
                .productPrice(10000)
                .productUrl("test.com")
                .build();

        // when
        wishlist.updateWishlist("2", "2_설명", 20000, "update.com");

        // then
        assertThat(wishlist)
                .extracting("productName", "productNotes", "productPrice", "productUrl")
                .contains("2", "2_설명", 20000, "update.com");
    }
}