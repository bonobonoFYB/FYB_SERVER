package school.bonobono.fyb.domain.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class KakaoDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class UrlInfoDto {
        String redirectURL;
        public static UrlInfoDto response(String redirectURL) {
            return UrlInfoDto.builder()
                    .redirectURL(redirectURL)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class OAuthTokenDto {
        private String access_token;
        private String refresh_token;
        private Integer refresh_token_expires_in;
        private Integer expires_in;
        private String scope;
        private String token_type;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UserInfoDto {
        private Long id;
        private String connected_at;
        private Properties properties;
        private KakaoAccount kakao_account;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        public static class Properties {
            private String nickname;
            private String profile_image;
            private String thumbnail_image;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        public static class KakaoAccount {
            private String email;
            private Boolean profile_nickname_needs_agreement;
            private Boolean profile_image_needs_agreement;
            private profile profile;
            private Boolean name_needs_agreement;
            private Boolean email_needs_agreement;
            private Boolean has_email;
            private Boolean is_email_valid;
            private Boolean is_email_verified;

            @AllArgsConstructor
            @NoArgsConstructor
            @Getter
            public static class profile {
                private String nickname;
                private String thumbnail_image_url;
                private String profile_image_url;
                private Boolean is_default_image;
            }
        }
    }
}
