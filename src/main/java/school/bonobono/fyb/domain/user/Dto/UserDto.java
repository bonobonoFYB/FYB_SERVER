package school.bonobono.fyb.domain.user.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.bonobono.fyb.domain.user.Entity.FybUser;

import javax.validation.constraints.NotNull;

public class UserDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginDto {
        private Long id;
        private String email;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String password;
        private String accessToken;
        private String refreshToken;

        public static LoginDto response(FybUser user, String atk, String rtk) {
            return LoginDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .accessToken(atk)
                    .refreshToken(rtk)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class RegisterDto {
        private String email;
        private String password;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
        private String form;
        private String pelvis;
        private String shoulder;
        private String leg;
        private String atk;
        private String rtk;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SocialRegisterDto {
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
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Builder
    public static class DetailDto{
        private String email;
        private String name;
        private String profileImagePath;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;

        public static DetailDto response(@NotNull FybUser user) {
            return DetailDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .profileImagePath(user.getProfileImagePath())
                    .gender(user.getGender())
                    .height(user.getHeight())
                    .weight(user.getWeight())
                    .age(user.getAge())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class LostPasswordResetDto{
        private String email;
        private String newPassword;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class PasswordResetDto{
        private String email;
        private String password;
        private String newPassword;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class WithdrawalDto {
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccessTokenRefreshDto {
        String accessToken;
        public static AccessTokenRefreshDto response(String accessToken) {
            return AccessTokenRefreshDto.builder()
                    .accessToken(accessToken)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class PhoneVerificationDto {
        String phoneNumber;
        String number;
        public static PhoneVerificationDto response(String number) {
            return PhoneVerificationDto.builder()
                    .number(number)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDataDto {
        String userData;
        public static UserDataDto response(String userData) {
            return UserDataDto.builder()
                    .userData(userData)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateDto {
        private String email;
        private String password;
        private String name;
        private Character gender;
        private Integer height;
        private Integer weight;
        private Integer age;
    }
}
