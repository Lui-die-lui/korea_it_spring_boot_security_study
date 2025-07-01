package com.koreait.spirngSecurityStudy.dto;

import com.koreait.spirngSecurityStudy.entity.OAuth2User;
import com.koreait.spirngSecurityStudy.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class OAuth2SignupReqDto {
    private String email;
    private String username;
    private String password;
    private String provider;
    private String providerUserId;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) { // 유저테이블에 추가
        return  User.builder()
                .username(this.username)
                .password(bCryptPasswordEncoder.encode(this.password))
                .email(this.email)
                .build();
    }

    public OAuth2User toOAuth2User(int userId) {
        return OAuth2User.builder()
                .userId(userId)
                .provider(this.provider)
                .providerUserId(this.providerUserId)
                .build();
    }
}
