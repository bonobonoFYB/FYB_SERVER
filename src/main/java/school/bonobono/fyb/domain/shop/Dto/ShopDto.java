package school.bonobono.fyb.domain.shop.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.domain.shop.Entity.Shop;

import javax.validation.constraints.NotNull;

public class ShopDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchDto {
        private String shop;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetailListDto {
        private Long id;
        private String shopName;
        private String shopUrl;
        private String shopImage;
        public static DetailListDto response(Shop shop) {
            return DetailListDto.builder()
                    .id(shop.getId())
                    .shopName(shop.getShopName())
                    .shopUrl(shop.getShopUrl())
                    .shopImage(shop.getShopImage())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SaveDto {
        private Long shopId;
        private String shopName;
        private String redirectURL;

        public static SaveDto response(Shop shop) {
            return SaveDto.builder()
                    .shopId(shop.getId())
                    .shopName(shop.getShopName())
                    .redirectURL(shop.getShopUrl())
                    .build();
        }
    }
}
