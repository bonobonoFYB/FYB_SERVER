package school.bonobono.fyb.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.Entity.Shop;

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

        public static ShopDto.Response response(@NotNull Shop shop) {

            return Response.builder()
                    .shop(shop.getShop())
                    .surl(shop.getSurl())
                    .build();
        }
    }
}
