package com.koreait.spirngSecurityStudy.repository;

import com.koreait.spirngSecurityStudy.entity.OAuth2User;
import com.koreait.spirngSecurityStudy.mapper.OAuth2UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OAuth2UserRepository {

    @Autowired
    private OAuth2UserMapper oAuth2UserMapper;

    public OAuth2User getOAth2UserByProviderAndProviderUserId(String provider, String providerUserId) {
       return oAuth2UserMapper.getOAuth2UserByProviderAndProviderUserId(provider,providerUserId);
    }

    public int insertOAuth2USer(OAuth2User oAuth2User) {
        return oAuth2UserMapper.insertOAuth2User(oAuth2User);
    }
}
