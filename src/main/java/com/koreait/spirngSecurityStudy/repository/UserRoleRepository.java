package com.koreait.spirngSecurityStudy.repository;

import com.koreait.spirngSecurityStudy.entity.UserRole;
import com.koreait.spirngSecurityStudy.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public Optional<UserRole> addUserRole(UserRole userRole) {
        return userRoleMapper.insert(userRole) < 1 ?
                Optional.empty() : Optional.of(userRole); // 넣지않음 (0) 값 : 넣어진 값 (1 이상)
    }
}
