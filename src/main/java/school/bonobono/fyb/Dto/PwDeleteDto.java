package school.bonobono.fyb.Dto;

import lombok.*;

public class PwDeleteDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        String pw;
    }
}
