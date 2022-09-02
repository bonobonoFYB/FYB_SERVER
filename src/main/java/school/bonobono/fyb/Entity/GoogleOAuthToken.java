package school.bonobono.fyb.Entity;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class GoogleOAuthToken {
    private String access_token;
    private Integer expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
