package school.bonobono.fyb.domain.wishlist.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.domain.shop.Dto.ShopDto;
import school.bonobono.fyb.domain.wishlist.Entity.Wishlist;

import javax.validation.constraints.NotNull;

public class WishlistDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeleteDto {
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SaveDto {
        private Long id;
        private String productName;
        private String productNotes;
        private String productUrl;
        private Integer productPrice;

        public static SaveDto response(Wishlist wishlist) {
            return SaveDto.builder()
                    .id(wishlist.getId())
                    .productName(wishlist.getProductName())
                    .productNotes(wishlist.getProductNotes())
                    .productUrl(wishlist.getProductUrl())
                    .productPrice(wishlist.getProductPrice())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetailDto {
        private Long pid;
        private String pname;
        private String notes;
        private String purl;
        private Integer price;

        public static DetailDto response(@NotNull Wishlist wishlist) {
            return DetailDto.builder()
                    .pid(wishlist.getId())
                    .pname(wishlist.getProductName())
                    .notes(wishlist.getProductNotes())
                    .purl(wishlist.getProductUrl())
                    .price(wishlist.getProductPrice())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateDto {
        private Long id;
        private String productName;
        private String productNotes;
        private String productUrl;
        private Integer productPrice;
    }
}
