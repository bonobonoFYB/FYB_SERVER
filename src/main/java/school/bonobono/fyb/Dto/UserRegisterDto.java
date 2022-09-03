package school.bonobono.fyb.Dto;

import lombok.*;
import school.bonobono.fyb.Entity.FybUser;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;


public class UserRegisterDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class socialRequest{
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        private String email;
        private String pw;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String email;
        private String pw;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;

    }
}
