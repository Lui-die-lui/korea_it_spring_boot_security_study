package com.koreait.spirngSecurityStudy.service;

import com.koreait.spirngSecurityStudy.dto.ApiRespDto;
import com.koreait.spirngSecurityStudy.dto.OAuth2SignupReqDto;
import com.koreait.spirngSecurityStudy.entity.User;
import com.koreait.spirngSecurityStudy.entity.UserRole;
import com.koreait.spirngSecurityStudy.repository.OAuth2UserRepository;
import com.koreait.spirngSecurityStudy.repository.UserRepository;
import com.koreait.spirngSecurityStudy.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2AuthService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserRoleRepository userRoleRepository;

        @Autowired
        private OAuth2UserRepository oAuth2UserRepository;

        @Autowired
        private BCryptPasswordEncoder bCryptPasswordEncoder;


    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) { // 새로 가입 로직
        Optional<User> optionalUser = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());
        if (optionalUser.isPresent()) { //객체가 비어있지 않은지(값이 존재하는지) 확인
            return new ApiRespDto<>("failed", "이미 존재하는 이메일 입니다." , null);
        }
        Optional<User> user = userRepository.addUser(oAuth2SignupReqDto.toEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(user.get().getUserId())
                // mapper.xml에 <insert id="addUser" useGeneratedKeys="true" keyProperty="userId"> 사용해서 가능
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);
        oAuth2UserRepository.insertOAuth2USer(oAuth2SignupReqDto.toOAuth2User(user.get().getUserId()));

        return new ApiRespDto<>("success","OAuth2 회원가입 완료", null);


    }
}
