package school.bonobono.fyb.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import school.bonobono.fyb.Entity.FybUser;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenInfoResponseDto {
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
        private LocalDateTime updateAt;

    public static TokenInfoResponseDto Response (@NotNull FybUser user){
        return TokenInfoResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .pw(user.getPw())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .age(user.getAge())
                .id(user.getId())
                .createAt(user.getCreateAt())
                .build();
    }
}
