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
    public static class Response {
        private String shop;
        private String surl;
        private String simg;

        public static ShopDto.Response dataResponse(@NotNull ShopData shop) {

            return Response.builder()
                    .shop(shop.getShop())
                    .surl(shop.getSurl())
                    .simg(shop.getSimg())
                    .build();
        }
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
}
