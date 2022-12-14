package school.bonobono.fyb.domain.user.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.domain.user.Entity.FybUser;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenInfoResponseDto {
    @JsonIgnore
    private Long id;
    private String pw;
    private String email;
    private String name;
    private String profileImagePath;
    private Character gender;
    private Integer height;
    private Integer weight;
    private Integer age;
    private String userData;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static TokenInfoResponseDto Response(@NotNull FybUser user) {
        return TokenInfoResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .pw(user.getPw())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .age(user.getAge())
                .id(user.getId())
                .userData(user.getUserData())
                .profileImagePath(user.getProfileImagePath())
                .createAt(user.getCreateAt())
                .build();
    }
}
