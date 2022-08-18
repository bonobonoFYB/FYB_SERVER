package school.bonobono.fyb.Dto;

import lombok.*;

public class PhoneCheckDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        private String pnum;
    }
}
