package school.bonobono.fyb.Dto;

import lombok.*;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Entity.Shop;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class ShopDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        private String shop;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String shop;
        private String surl;

        public static ShopDto.Response response(@NotNull Shop shop){

            return Response.builder()
                    .shop(shop.getShop())
                    .surl(shop.getSurl())
                    .build();
        }
    }
}