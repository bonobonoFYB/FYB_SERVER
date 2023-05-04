package school.bonobono.fyb.domain.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GoogleDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class OAuthTokenDto {
        private String access_token;
        private Integer expires_in;
        private String scope;
        private String token_type;
        private String id_token;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UserInfoDto {
        private String id;
        private String email;
        private Boolean verified_email;
        private String name;
        private String given_name;
        private String picture;
        private String locale;
    }

}
