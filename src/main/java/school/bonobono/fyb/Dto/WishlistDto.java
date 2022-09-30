package school.bonobono.fyb.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.Entity.Wishlist;

import javax.validation.constraints.NotNull;

public class WishlistDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class deleteRequest {
        private Long pid;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String pname;
        private String notes;
        private String purl;
        private Integer price;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long pid;
        private String pname;
        private String notes;
        private String purl;
        private Integer price;

        public static WishlistDto.Response response(@NotNull Wishlist wishlist) {
            return Response.builder()
                    .pid(wishlist.getPid())
                    .pname(wishlist.getPname())
                    .notes(wishlist.getNotes())
                    .purl(wishlist.getPurl())
                    .price(wishlist.getPrice())
                    .build();
        }
    }
}
