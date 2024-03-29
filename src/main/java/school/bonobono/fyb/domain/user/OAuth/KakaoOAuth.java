package school.bonobono.fyb.domain.user.OAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import school.bonobono.fyb.domain.user.Dto.KakaoDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth {

    @Value("${app.kakao.restApiKey}")
    private String restApiKey;
    @Value("${app.kakao.redirectUrl}")
    private String kakaoRedirecUrl;

    public String getKakaoLoginURL() {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + restApiKey +
                "&redirect_uri=" + kakaoRedirecUrl + "&response_type=code";
        return kakaoLoginUrl;
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headersAccess = new HttpHeaders();
        headersAccess.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", restApiKey);
        params.add("redirect_uri", kakaoRedirecUrl);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, headersAccess);

        return restTemplate.postForEntity("https://kauth.kakao.com/oauth/token",
                kakaoRequest, String.class);
    }

    public KakaoDto.OAuthTokenDto getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoDto.OAuthTokenDto kakaoOAuthTokenDto = null;
        kakaoOAuthTokenDto = objectMapper.readValue(response.getBody(), KakaoDto.OAuthTokenDto.class);
        return kakaoOAuthTokenDto;
    }

    public ResponseEntity<String> requestUserInfo(KakaoDto.OAuthTokenDto oAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        return restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class);
    }

    public KakaoDto.UserInfoDto getUserInfo(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), KakaoDto.UserInfoDto.class);
    }

}
