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
    public static class readResponse{
        private Long uid;
        private String pname;
        private String pnotes;
        private String pkind;

        public static MyClosetDto.readResponse Response(@NotNull MyCloset myCloset){
            return readResponse.builder()
                    .pname(myCloset.getPname())
                    .pkind(myCloset.getPkind())
                    .uid(myCloset.getUid())
                    .pnotes(myCloset.getPnotes())
                    .build();
        }
    }
}
