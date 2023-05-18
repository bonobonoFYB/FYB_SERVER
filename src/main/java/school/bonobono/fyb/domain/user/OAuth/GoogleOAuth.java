package school.bonobono.fyb.domain.user.OAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import school.bonobono.fyb.domain.user.Dto.GoogleDto;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleOAuth {

    private final RestTemplate restTemplate;
    @Value("${app.google.clientid}")
    private String googleClientId;
    @Value("${app.google.redirectUrl}")
    private String googleRedirecUrl;
    @Value("${app.google.clientSecret}")
    private String googleClientSecret;

    public String getGoogleLoginURL() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirecUrl
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirecUrl);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                params, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new CustomException(Result.FAIL);
        }

        return responseEntity;
    }

    public GoogleDto.OAuthTokenDto getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        return new ObjectMapper().readValue(response.getBody(), GoogleDto.OAuthTokenDto.class);
    }

    public ResponseEntity<String> requestUserInfo(GoogleDto.OAuthTokenDto oAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        return restTemplate.exchange("https://www.googleapis.com/oauth2/v1/userinfo", HttpMethod.GET, request, String.class);
    }

    public GoogleDto.UserInfoDto getUserInfo(ResponseEntity<String> response) throws JsonProcessingException {
        return new ObjectMapper().readValue(response.getBody(), GoogleDto.UserInfoDto.class);
    }

}
