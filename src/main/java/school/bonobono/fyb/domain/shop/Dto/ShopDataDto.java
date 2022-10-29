package school.bonobono.fyb.domain.shop.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ShopDataDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long sid;
        private Character gender;
        private Integer age;
        // 미구현
        private Integer height;
        private Integer weight;
    }
}
