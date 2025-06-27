package com.koreait.spirngSecurityStudy.service;

import com.koreait.spirngSecurityStudy.dto.*;
import com.koreait.spirngSecurityStudy.entity.User;
import com.koreait.spirngSecurityStudy.entity.UserRole;
import com.koreait.spirngSecurityStudy.repository.UserRepository;
import com.koreait.spirngSecurityStudy.repository.UserRoleRepository;
import com.koreait.spirngSecurityStudy.security.jwt.JwtUtil;
import com.koreait.spirngSecurityStudy.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public ApiRespDto<?> addUser(SignUpReqDto signUpReqDto) {
        Optional<User> optionalUser = userRepository.addUser(signUpReqDto.toEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3) // ?
                .build();
        userRoleRepository.addUserRole(userRole);
        return new ApiRespDto<>("success","회원가입 성공", userRole);
    }

    public ApiRespDto<?> signin(SigninReqDto signinReqDto) {
        Optional<User> optionalUser = userRepository.getUserByUsername(signinReqDto.getUsername());
        if (optionalUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요.", null);
            // 아이디,비밀번호가 틀렸다고 입력되면 보안상 문제 생김
        }
        User user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
             // (!일치 하지 않으면)입력된 패스워드와 암호화된 유저의 패스워드를 들고옴
        return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요.", null);
        }
        System.out.println("로그인 성공");
        String token = jwtUtil.generateAccessToken(user.getUserId().toString()); // Integer값을 String으로 반환
        return new ApiRespDto<>("success","로그인 성공",token);
    }

    public ApiRespDto<?> mdifyEmail(Integer userId, ModifyEmailReqDto modifyEmailReqDto) {
        User user = modifyEmailReqDto.toEntity(userId);
        int result = userRepository.updateEmail(user);
        return new ApiRespDto<>("success", "이메일 수정 성공", result);
    }

    public ApiRespDto<?> modifyPassword(ModifyPasswordReqDto modifyPasswordReqDto, PrincipalUser principalUser) {
        if (! bCryptPasswordEncoder.matches(modifyPasswordReqDto.getOldPassword(),principalUser.getPassword())) {
            return new ApiRespDto<>("failed","사용자 정보를 확인하세요.",null);
        }
        String password = bCryptPasswordEncoder.encode(modifyPasswordReqDto.getNewPassword());
        int result = userRepository.updatePassword(principalUser.getUserId(), password);
        return new ApiRespDto<>("success","비밀번호 수정 성공",result);
    }


}
