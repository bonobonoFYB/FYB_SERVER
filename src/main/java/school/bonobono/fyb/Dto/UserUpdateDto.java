package school.bonobono.fyb.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import school.bonobono.fyb.Entity.FybUser;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class UserUpdateDto {

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
