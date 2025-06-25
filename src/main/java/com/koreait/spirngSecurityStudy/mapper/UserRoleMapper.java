package com.koreait.spirngSecurityStudy.mapper;

import com.koreait.spirngSecurityStudy.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    int insert(UserRole userRole);
}

