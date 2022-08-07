package school.bonobono.fyb.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import school.bonobono.fyb.Jwt.JwtAccessDeniedHandler;
import school.bonobono.fyb.Jwt.JwtAuthenticationEntryPoint;
import school.bonobono.fyb.Jwt.JwtSecurityConfig;
import school.bonobono.fyb.Jwt.TokenProvider;

@EnableWebSecurity // 기본적인 웹 보안을 활성화 시킬거라는 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true) //PreAuthorize 어노테이션을 메소드 단위로 사용하기 위해 선언
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 생성한 jwt 설정들 의존성 주입
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 패스워드는 Bcrypt 사용
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable() // token을 사용하는 방식이기 때문에 csrf를 disable

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling() // Exception을 핸들링 할때 직접 만든 Exception 클래스들을 설정
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다는 의미
                .antMatchers("/login").permitAll() // 인증없이 접근을 허용하겠다는 의미
                .antMatchers("/register").permitAll()
                .antMatchers("/authenticate").permitAll()

                .anyRequest().authenticated() // 나머지 요청들은 전부 인증 받아야함을 명시

                .and()
                .apply(new JwtSecurityConfig(tokenProvider)); // JwtFilter를 JwtSecurityConfig 에 등록했기에 필터에 적용
    }
}