package school.bonobono.fyb.global.config.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import school.bonobono.fyb.global.exception.CustomErrorResponse;
import school.bonobono.fyb.global.model.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        //Content-type : application/json;charset=utf-8
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        CustomErrorResponse error = new CustomErrorResponse();
        error.setStatus(Result.JWT_TIMEOUT.getCode());
        error.setStatusMessage("만료된 JWT 토큰입니다.");

        // {"username":"loop-study", "age":20}
        String result = objectMapper.writeValueAsString(error);
        response.getWriter().write(result);
    }
}
