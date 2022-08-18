package school.bonobono.fyb.Dto;

import lombok.*;

public class PwChangeDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        private String email;
        private String pw;
        private String newPw;
    }
}
