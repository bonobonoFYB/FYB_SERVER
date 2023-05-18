package school.bonobono.fyb.domain.shop.Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.shop.Dto.ShopDto;
import school.bonobono.fyb.domain.shop.Entity.Shop;
import school.bonobono.fyb.domain.shop.Repository.ShopRepository;
import school.bonobono.fyb.domain.user.Entity.Authority;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.redis.service.RedisService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShopServiceTest {

    @MockBean
    private RedisService redisService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("등록된 쇼핑몰들을 전부 불러온다.")
    @Test
    void getAllShopAndUserInfo() {
        // given
        savedShop();

        // when
        List<ShopDto.DetailListDto> response = shopService.getAllShop();

        // then
        assertThat(response)
                .extracting("id", "shopName", "shopUrl", "shopImage")
                .containsExactlyInAnyOrder(
                        tuple(1L, "무신사", "www.musinsa.com", "test.png"),
                        tuple(2L, "우신사", "www.wusinsa.com", "test.png"),
                        tuple(3L, "지그재그", "www.zigzag.kr", "test.png")
                );
    }

    @DisplayName("사용자 조회수 기준으로 정렬된 쇼핑몰 목록을 가져온다.")
    @Test
    void getMostViewed() {
        // given
        savedShop();
        given(redisService.getSortedShopId(anyString()))
                .willReturn(List.of(2L, 1L, 3L));

        // when
        List<ShopDto.DetailListDto> response = shopService.getMostViewed();

        // then
        assertThat(response)
                .extracting("id")
                .contains(2L, 1L, 3L);
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

    private void savedShop() {
        Shop musinsa = Shop.builder()
                .id(1L)
                .shopName("무신사")
                .shopUrl("www.musinsa.com")
                .shopImage("test.png")
                .shopData(false)
                .build();

        Shop wusinsa = Shop.builder()
                .id(2L)
                .shopName("우신사")
                .shopUrl("www.wusinsa.com")
                .shopImage("test.png")
                .shopData(false)
                .build();

        Shop zigzag = Shop.builder()
                .id(3L)
                .shopName("지그재그")
                .shopUrl("www.zigzag.kr")
                .shopImage("test.png")
                .shopData(false)
                .build();

        shopRepository.saveAll(List.of(musinsa, wusinsa, zigzag));
    }
}