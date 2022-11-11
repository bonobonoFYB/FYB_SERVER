package school.bonobono.fyb.domain.shop.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.domain.shop.Entity.Shop;
import school.bonobono.fyb.domain.shop.Entity.ShopData;

import javax.validation.constraints.NotNull;

public class ShopDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
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

        public static ShopDto.Response response(@NotNull Shop shop) {

            return Response.builder()
                    .shop(shop.getShop())
                    .surl(shop.getSurl())
                    .simg(shop.getSimg())
                    .build();
        }

        public static ShopDto.Response dataResponse(@NotNull ShopData shop) {

            return Response.builder()
                    .shop(shop.getShop())
                    .surl(shop.getSurl())
                    .simg(shop.getSimg())
                    .build();
        }
    }
}
