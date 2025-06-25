package com.koreait.spirngSecurityStudy.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String username;
    @JsonIgnore // 아래 값을 json으로 반환할때 빼고 보여줌
    private String password;

    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
