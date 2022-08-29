package school.bonobono.fyb.Dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import school.bonobono.fyb.Entity.MyCloset;

import javax.validation.constraints.NotNull;

public class MyClosetDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class addRequest{
        private String pname;
        private String pnotes;
        private String pkind;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class readResponse{
        private Long id;
        private String pname;
        private String pnotes;
        private String pkind;

        public static MyClosetDto.readResponse Response(@NotNull MyCloset myCloset){
            return readResponse.builder()
                    .id(myCloset.getId())
                    .pname(myCloset.getPname())
                    .pkind(myCloset.getPkind())
                    .pnotes(myCloset.getPnotes())
                    .build();
        }
    }
}
