package com.koreait.spirngSecurityStudy.security.filter;

import com.koreait.spirngSecurityStudy.entity.User;
import com.koreait.spirngSecurityStudy.repository.UserRepository;
import com.koreait.spirngSecurityStudy.security.jwt.JwtUtil;
import com.koreait.spirngSecurityStudy.security.model.PrincipalUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component // bean등록 해야 해당 클래스 Autowird 사용가능
public class JwtAuthenticationFilter implements Filter {
    //Java 에서 javax.servlet.Filter는 **원래 서블릿 컨테이너(Tomcat 등)**가 관리하는 것이고,
    //Spring 에서는 필터를 자동으로 Bean 으로 등록하지 않음.


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override // ALT + Ins / ctrl + I - 메서드 구현
    // Override는 생성자와 구성은 같게, 내용만 바꿔줌(메서드 재정의)
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //필터 = 전처리 / 후처리 - 응답
//        System.out.println("전처리");



        // 해당 메소드가 아니면 그냥 다음 필터로 넘김
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        List<String> methods = List.of("POST", "PUT", "GET", "PATCH", "DELETE");
        if (! methods.contains(request.getMethod())) { // 위 리스트가 아니면
            filterChain.doFilter(servletRequest, servletResponse); // 다음 필터로 넘겨라
            return;
        }


        String authorization = request.getHeader("Authorization");
        System.out.println("Bearer 토큰 : " + authorization); // Bearer 토큰 : Bearer 12345 반환
        if (jwtUtil.isBearer(authorization)) {
            String accessToken = jwtUtil.removeBearer(authorization);
            try {
                Claims claims = jwtUtil.getClaims(accessToken);
                // 토큰에서 Claims를 추출
                // 이때 서명검증도 같이 진행
                // 서명 위조나 만료 시 예외 발생
                String id = claims.getId();

                //인증 구조 - UserDetailsService
                Integer userId = Integer.parseInt(id);
                Optional<User> optionalUser = userRepository.getUserByUserId(userId);
                optionalUser.ifPresentOrElse((user) ->{
                    // DB에서 조회된 User 객체를 Spring Security 인증 객체(PrincipalUser) 로
                    // UserDetails
                    PrincipalUser principalUser =PrincipalUser.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .userRoles(user.getUserRoles())
                            .build();
                    // UsernamePasswordAuthenticationToken 직접 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principalUser,"", principalUser.getAuthorities()); // 비번부분은 인증이 완료되어서 비워줌
                    // spring security의 인증 컨텍스트에 인증 객체 저장 -> 이후 요청은 인증된 사용자로 간주
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("인증 완료");
                    System.out.println(authentication.getName());
                }, () -> {
                    throw new AuthenticationCredentialsNotFoundException("인증 실패");
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        // 인증 실패든 성공이든 필터링을 중단하지 않고 다음 필터로 넘어감


        filterChain.doFilter(servletRequest,servletResponse); // 이거 전 - 전처리 / 후 - 후처리
        // doFilter 가 있으면 다음으로 감
        System.out.println("후처리");
        // 작동하면 java 시스템에 찍힘
    }
}
