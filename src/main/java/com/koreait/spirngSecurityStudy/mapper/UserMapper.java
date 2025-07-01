package com.koreait.spirngSecurityStudy.mapper;

import com.koreait.spirngSecurityStudy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    int addUser(User user);
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    int updateEmail(User user);
    int updatePassword(Integer userId, String password);
}
