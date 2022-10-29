package school.bonobono.fyb.domain.user.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class UserUpdateDto {

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
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        @JsonIgnore
        private Long id;
        private String pw;
        private String email;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
        private LocalDateTime createAt;
    }
}
