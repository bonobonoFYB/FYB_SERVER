package school.bonobono.fyb.domain.shop.Repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.shop.Entity.Shop;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    @DisplayName("Id 값을 넣은 순서대로 정렬되어 출력한다.")
    @Test
    void findByIdsInSpecifiedOrder() {
        // given
        savedShop();

        // when
        List<Shop> sortedShop = shopRepository.findByIdIn(List.of(2L, 1L, 3L));

        // then
        assertThat(sortedShop.size()).isEqualTo(3);
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