package school.bonobono.fyb.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.Entity.FybUser;

import javax.validation.constraints.NotNull;

public class UserReadDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserResponse {
        private String email;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;

        public static UserReadDto.UserResponse Response(@NotNull FybUser user) {
            return UserReadDto.UserResponse.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .gender(user.getGender())
                    .height(user.getHeight())
                    .weight(user.getWeight())
                    .age(user.getAge())
                    .build();
        }
    }
}
