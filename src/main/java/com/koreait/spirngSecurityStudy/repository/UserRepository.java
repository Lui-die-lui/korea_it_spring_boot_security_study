package com.koreait.spirngSecurityStudy.repository;

import com.koreait.spirngSecurityStudy.entity.User;
import com.koreait.spirngSecurityStudy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    public Optional<User> addUser(User user) {
        try {
            userMapper.addUser(user);
        }catch (DuplicateKeyException e) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public Optional<User> getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    public int updateEmail(User user) {
        return userMapper.updateEmail(user);
    }

    public int updatePassword(Integer userId, String password) {
        return userMapper.updatePassword(userId, password);
    }
}
