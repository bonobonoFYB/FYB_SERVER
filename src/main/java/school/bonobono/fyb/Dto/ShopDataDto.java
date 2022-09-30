package school.bonobono.fyb.Dto;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class ShopDataDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        private Long sid;
        private Character gender;
        private Integer age;
        private String surl;
        // 미구현
        private Integer height;
        private Integer weight;
    }
}
