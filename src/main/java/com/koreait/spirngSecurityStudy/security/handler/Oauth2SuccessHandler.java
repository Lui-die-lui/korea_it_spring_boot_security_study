package com.koreait.spirngSecurityStudy.security.handler;

import com.koreait.spirngSecurityStudy.entity.OAuth2User;
import com.koreait.spirngSecurityStudy.entity.User;
import com.koreait.spirngSecurityStudy.repository.OAuth2UserRepository;
import com.koreait.spirngSecurityStudy.repository.UserRepository;
import com.koreait.spirngSecurityStudy.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException { // 전부 완료됐을때 어떻게 처리할지

        // OAuth2User 정보 가져오기
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("id");
        String email = defaultOAuth2User.getAttribute("email");

        // provider, providerUserId 이미 연동된 사용자 정보가 있는지 DB 조회
        OAuth2User oAuth2User = oAuth2UserRepository.getOAth2UserByProviderAndProviderUserId(provider, providerUserId);

        // OAuth2 로그인을 통해 회원가입이 되어있지 않거나 아직 연동되지 않은 상태
        if(oAuth2User == null) {
            // 프론트로 provider와 providerUserId, email 전달
            response.sendRedirect("http://localhost:3000/auth/oauth2?provider=" + provider
                    + "&providerUserId" + providerUserId + "&email=" +email);
        }

        // 연동된 사용자가 있다면? -> userId를 통해 회원 정보 조회
        Optional<User> optionalUser = userRepository.getUserByUserId(oAuth2User.getUserId());

        // OAuth2 로그인을 통해 회원가입이나 연동을 진행한 경우
        String accessToken = null;
        if (optionalUser.isPresent()) {
            accessToken = jwtUtil.generateAccessToken(Integer.toString(optionalUser.get().getUserId()));
        }

        // 최종적으로 accessToken을 쿼리 파라미터로 프론트에 전달 - cpontroller 도착하기 전에 처리 완료
        response.sendRedirect("http://localhost:3000/auth/oauth2/signin?accessToken=" + accessToken);

    }
}
