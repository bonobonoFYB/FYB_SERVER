package school.bonobono.fyb.domain.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class UserRegisterDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class socialRequest {
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
        private String form;
        private String pelvis;
        private String shoulder;
        private String leg;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String email;
        private String pw;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
        private String form;
        private String pelvis;
        private String shoulder;
        private String leg;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String email;
        private String pw;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
    }
}
