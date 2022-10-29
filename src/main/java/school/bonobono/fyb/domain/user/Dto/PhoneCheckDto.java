package school.bonobono.fyb.domain.user.Dto;

import lombok.*;

public class PhoneCheckDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        private String pnum;
    }
}
