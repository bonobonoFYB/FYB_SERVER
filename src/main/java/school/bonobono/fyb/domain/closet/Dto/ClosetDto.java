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
    public static class deleteRequest{
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class readResponse{
        private Long id;
        private String pname;
        private String pnotes;
        private String pkind;
        private String closetImagePath;

        public static ClosetDto.readResponse Response(@NotNull Closet myCloset){
            return readResponse.builder()
                    .id(myCloset.getId())
                    .pname(myCloset.getPname())
                    .pkind(myCloset.getPkind())
                    .pnotes(myCloset.getPnotes())
                    .closetImagePath(myCloset.getClosetImagePath())
                    .build();
        }
    }
}
