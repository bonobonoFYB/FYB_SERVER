package school.bonobono.fyb.domain.user.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.domain.user.Entity.FybUser;

public class UserDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginDto {
        private Long id;
        private String email;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String pw;
        private String atk;
        private String rtk;

        public static LoginDto response(FybUser user, String atk, String rtk) {
            return LoginDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .atk(atk)
                    .rtk(rtk)
                    .build();
        }
    }
}
