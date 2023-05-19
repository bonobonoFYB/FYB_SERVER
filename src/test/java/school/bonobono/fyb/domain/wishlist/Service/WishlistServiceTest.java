package school.bonobono.fyb.domain.wishlist.Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.domain.wishlist.Dto.WishlistDto;
import school.bonobono.fyb.domain.wishlist.Entity.Wishlist;
import school.bonobono.fyb.domain.wishlist.Repository.WishlistRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WishlistServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private WishlistRepository wishlistRepository;

    @DisplayName("유저가 자신의 장바구니안에 상품을 하나 등록한다.")
    @Test
    void addWishlistInfo() {
        // given
        FybUser user = getUserAndSave();

        WishlistDto.SaveDto request = WishlistDto.SaveDto.builder()
                .productName("1")
                .productNotes("1_설명")
                .productPrice(20000)
                .productUrl("test.com")
                .build();

        // when
        WishlistDto.SaveDto response = wishlistService.addWishlistInfo(request, user);

        // then
        assertThat(response)
                .extracting("productName", "productNotes", "productPrice", "productUrl")
                .contains("1", "1_설명", 20000, "test.com");
    }

    @DisplayName("유저가 자신의 장바구니 안의 상품들을 조회한다.")
    @Test
    void getWishlistInfo() {
        // given
        FybUser user = getUserAndSave();

        Wishlist wishlist1 = Wishlist.builder()
                .productName("1")
                .productNotes("1_설명")
                .productPrice(10000)
                .productUrl("test.com")
                .build();
        user.getWishlists().add(wishlist1);

        Wishlist wishlist2 = Wishlist.builder()
                .productName("2")
                .productNotes("2_설명")
                .productPrice(20000)
                .productUrl("test.com")
                .build();
        user.getWishlists().add(wishlist2);

        Wishlist wishlist3 = Wishlist.builder()
                .productName("3")
                .productNotes("3_설명")
                .productPrice(30000)
                .productUrl("test.com")
                .build();
        user.getWishlists().add(wishlist3);

        wishlistRepository.save(wishlist1);
        wishlistRepository.save(wishlist2);
        wishlistRepository.save(wishlist3);

        // when
        List<WishlistDto.DetailDto> response = wishlistService.getWishlistInfo(user);

        // then
        assertThat(response)
                .hasSize(3)
                .extracting(WishlistDto.DetailDto::getPname, WishlistDto.DetailDto::getPrice)
                .containsExactlyInAnyOrder(
                        tuple("1", 10000),
                        tuple("2", 20000),
                        tuple("3", 30000)
                );
    }

    @DisplayName("유저가 자신의 장바구니 안에 담아둔 상품을 하나 삭제한다.")
    @Test
    void deleteWishlistInfo() {
        // given
        FybUser user = getUserAndSave();

        Wishlist wishlist = wishlistRepository.save(Wishlist.builder()
                .productName("1")
                .productNotes("1_설명")
                .productPrice(10000)
                .productUrl("test.com")
                .user(user)
                .build()
        );

        WishlistDto.DeleteDto request = WishlistDto.DeleteDto.builder()
                .id(wishlist.getId())
                .build();

        // when
        WishlistDto.DetailDto response = wishlistService.deleteWishlistInfo(request);

        // then
        assertThat(response.getPname()).isEqualTo("1");

        Optional<Wishlist> wishlistOptional = wishlistRepository.findById(request.getId());
        assertThat(wishlistOptional.isEmpty()).isTrue();
    }

    @DisplayName("유저가 자신의 장바구니 안에 있는 상품을 하나 수정한다.")
    @Test
    void updateWishlistInfo() {
        // given
        FybUser user = getUserAndSave();

        Wishlist wishlist = wishlistRepository.save(Wishlist.builder()
                .productName("1")
                .productNotes("1_설명")
                .productPrice(10000)
                .productUrl("test.com")
                .user(user)
                .build()
        );

        WishlistDto.UpdateDto request = WishlistDto.UpdateDto.builder()
                .id(wishlist.getId())
                .productName("2")
                .productNotes("2_설명")
                .productPrice(20000)
                .productUrl("update.com")
                .build();

        // when
        wishlistService.updateWishlistInfo(request);

        // then
        Optional<Wishlist> wishlistOptional = wishlistRepository.findById(request.getId());

        assertThat(wishlistOptional.isPresent()).isTrue();
        assertThat(wishlistOptional.get())
                .extracting("productName", "productNotes", "productPrice", "productUrl")
                .contains("2", "2_설명", 20000, "update.com");

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
                .pw("abc123!")
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