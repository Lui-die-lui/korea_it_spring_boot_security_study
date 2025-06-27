package com.koreait.spirngSecurityStudy.dto;


import com.koreait.spirngSecurityStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyEmailReqDto {
    private String email;

    public User toEntity(Integer userId) {
        return User.builder()
                .userId(userId)
                .email(this.email)
                .build();
    }
}
