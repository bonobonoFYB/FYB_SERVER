package school.bonobono.fyb.Dto;

import lombok.*;

public class PwDeleteDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        String pw;
    }
}
