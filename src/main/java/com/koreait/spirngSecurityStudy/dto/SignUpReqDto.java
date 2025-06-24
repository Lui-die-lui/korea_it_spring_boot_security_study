package com.koreait.spirngSecurityStudy.dto;

import com.koreait.spirngSecurityStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class SignUpReqDto {
    private String username;
    private String password;
    private String email;


    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .username(this.username)
                .password(bCryptPasswordEncoder.encode(this.password)) // 암호문 형태로 바꿔줌
                .email(this.email)
                .build();
    }
}
