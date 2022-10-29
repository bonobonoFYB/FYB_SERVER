package school.bonobono.fyb.domain.user.Dto;

import lombok.*;

public class PwChangeDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        private String email;
        private String pw;
        private String newPw;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class lostRequest{
        private String email;
        private String newPw;
    }
}
