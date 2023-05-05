package school.bonobono.fyb.domain.closet.Dto;

import lombok.*;
import school.bonobono.fyb.domain.closet.Entity.Closet;
import school.bonobono.fyb.domain.wishlist.Entity.Wishlist;

import javax.validation.constraints.NotNull;

public class ClosetDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SaveDto{
        private String productName;
        private String productNotes;
        private String productKind;
        public static SaveDto response(Closet closet) {
            return SaveDto.builder()
                    .productName(closet.getProductName())
                    .productNotes(closet.getProductNotes())
                    .productKind(closet.getProductKind())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeleteDto{
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetailDto{
        private Long id;
        private String productName;
        private String productNotes;
        private String productKind;
        private String closetImagePath;

        public static DetailDto response(@NotNull Closet closet){
            return DetailDto.builder()
                    .id(closet.getId())
                    .productName(closet.getProductName())
                    .productNotes(closet.getProductNotes())
                    .productKind(closet.getProductKind())
                    .closetImagePath(closet.getClosetImagePath())
                    .build();
        }
    }
}
