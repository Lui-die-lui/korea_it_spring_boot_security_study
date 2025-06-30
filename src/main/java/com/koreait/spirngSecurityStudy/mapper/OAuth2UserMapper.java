package com.koreait.spirngSecurityStudy.mapper;

import com.koreait.spirngSecurityStudy.entity.OAuth2User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2UserMapper {
    OAuth2User getOAth2UserByProviderAndProviderUserId (String provider, String providerUserId);
}
