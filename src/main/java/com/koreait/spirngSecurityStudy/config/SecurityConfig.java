package com.koreait.spirngSecurityStudy.config;

import com.koreait.spirngSecurityStudy.security.filter.JwtAuthenticationFilter;
import com.koreait.spirngSecurityStudy.security.handler.Oauth2SuccessHandler;
import com.koreait.spirngSecurityStudy.service.OAuth2PrincipalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private Oauth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private OAuth2PrincipalUserService oAuth2PrincipalUserService;


    // 비밀번호를 안전하게 암호화(해싱) 하고, 검증하는 역할
    // 단방향 해시, 복호화가 불가능 - 다시 원래대로 바꾸는게 불가능
    @Bean // ioc 컨테이너에 저장해라
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // corsConfigurationSource() 설정은 spring security에서
    // CORS(Cross-Origin Resource Sharing)를 처리하기 위한 설정
    // CORS
    // 브라우저가 보안상 다른 도메인의 리소스 요청을 제한하는 정책
    // 기본적으로 브라우저는 같은 출처(Same-Origin)만 허용한다.

    @Bean // bean 있어야 객체생성 가능
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 요청을 보내는 쪽의 도메인(사이트 주소)을 허용하겠다 - 모두 허용
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);
        // 요청을 보내는 쪽에서 Request, Response Header 정보에 대한 제약을 허용
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        // 요청을 보내는 쪽의 메소드 (GET, POST, PUT, DELETE, OPTION 등) 허용
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        // 요청 URL (/user/get)에 대한 CORS 설정 적용을 위해 객체 생성
        UrlBasedCorsConfigurationSource soruce = new UrlBasedCorsConfigurationSource();
        // 모든 URL(/**)에 대해 위에서 설정한  CORS 정책을 적용
        soruce.registerCorsConfiguration("/**",corsConfiguration);
        return soruce;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(csrf->csrf.disable()); // 사용하지 않겠다
        // CSRF란
        // 사용자가 의도하지 않은 요청을 공격자가 유도해서 서버에 전달하도록 하는 공격
        // JWT 방식 또는 무상태(Stateless) 인증이기 대문에
        // 세션이 없고, 쿠키도 안 쓰고, 토큰 기반이기 때문에 CSRF 공격 자체가 성립되지 않는다.

        // 서버 사이드 렌더링 로그인 방식 비활성화
        http.formLogin(formLogin -> formLogin.disable());
        // HTTP 프로토콜 기본 로그인 방식 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());
        // 서버 사이드 렌더링 로그아웃 비활성화
        http.logout(logout -> logout.disable());
        http.sessionManagement(Session -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 세션 안쓰겠다 설정

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // 뒤 필터에 앞 필터를 적용시킴 but 로그인 안쓰기땜에 지금은 사용 안함


        // 특정 요청 URL에 대한 권한 설정
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/test",
                    "/auth/signup",
                    "/auth/signin",
                    "/oauth2/**",
                    "/login/oauth2/**").permitAll(); // 이 링크는 전부 허가 해주겠다(통과시켜주는 용도)
            // http://localhost:8080/oauth2/authorization/google - 계정 선택 화면뜸
            auth.anyRequest().authenticated();
        });

        // 요청이 들어오면 Spring Security의 filterChain을 탄다
        // 여기서 여러 필터 중 하나가 OAuth2 요청을 감지
        // 감지되면 해당 provider의 로그인 페이지로 리디렉션 함
        http.oauth2Login(oauth2 -> oauth2
                // OAuth2 로그인 요청이 성공하고 사용자 정보를 가져오는 과정 설정
                .userInfoEndpoint(userInfo ->
                        // 사용자 정보 요청이 완료가 되면 이 커스텀 서비스로 OAuth2User를 처리하겠다고 설정
                        userInfo.userService(oAuth2PrincipalUserService))
                        // OAuth2 인증이 최종적으로 성공한 후 (사용자 정보 파싱이 끝난 후) 실행할 핸들러 설정
                        .successHandler(oAuth2SuccessHandler)
        );

        return http.build();
    }
}
