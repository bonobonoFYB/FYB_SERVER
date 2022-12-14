package school.bonobono.fyb.domain.closet.Dto;

import lombok.*;
import school.bonobono.fyb.domain.closet.Entity.MyCloset;

import javax.validation.constraints.NotNull;

public class MyClosetDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class addRequest{
        private String pname;
        private String pnotes;
        private String pkind;
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

        public static MyClosetDto.readResponse Response(@NotNull MyCloset myCloset){
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
