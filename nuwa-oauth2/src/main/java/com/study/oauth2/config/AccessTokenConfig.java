package com.study.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * @ClassName: AccessTokenConfig
 * @DESCRIPTION: 令牌相关配置
 * @author: 西门
 * @create: 2023-09-03 22:51
 **/
@Configuration
public class AccessTokenConfig {

    /**
     * 令牌存储策略
     * 对token进行持久化存储在数据库中，数据存储在oauth_access_token和oauth_refresh_token
     *
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        // 使用JwtTokenStore生成JWT令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 使用非对称加密算法对token签名
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * 从classpath下的密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "810905".toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("jwt", "810905".toCharArray());
        return keyPair;
    }

}
