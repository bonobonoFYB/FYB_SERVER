package school.bonobono.fyb.Dto;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;


public class UserLoginDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotNull
        private String email;
        @NotNull
        private String pw;
    }
}
