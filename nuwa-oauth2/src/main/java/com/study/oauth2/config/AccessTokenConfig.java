package com.study.oauth2.config;

import com.study.oauth2.service.NuwaUserDetails;
import com.study.platform.base.constant.TokenConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.LinkedHashMap;

/**
 * @ClassName: AccessTokenConfig
 * @DESCRIPTION: 令牌相关配置
 * @author: 西门
 * @create: 2023-09-03 22:51
 **/
@Configuration
public class AccessTokenConfig {

    @Autowired
    @Qualifier(value = "nuwaUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    /**
     * 令牌存储策略
     * <p>
     * 对token进行持久化存储在数据库中，数据存储在oauth_access_token和oauth_refresh_token
     * JwtTokenStore
     * RedisTokenStore：将令牌存储到Redis中，此种方式相对于内存方式来说性能更好
     * JdbcTokenStore：将令牌存储到数据库中，需要新建从对应的表
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
     * JwtAccessTokenConverter: 令牌增强类，用户jwt令牌和OAuth身份进行转换
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenEnhancer();
        // 设置秘钥
        converter.setKeyPair(keyPair());
        //创建默认的DefaultUserAuthenticationConverter
        DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter();
        //注入UserDetailService
        userAuthenticationConverter.setUserDetailsService(userDetailsService);
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        tokenConverter.setUserTokenConverter(userAuthenticationConverter);
        converter.setAccessTokenConverter(tokenConverter);
        //设置令牌转换器，将用户信息存储到令牌中
//        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
//        accessTokenConverter.setUserTokenConverter(new JwtEnhanceUserAuthenticationConverter());
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

    /**
     * JWT令牌增强，继承JwtAccessTokenConverter
     * 将业务所需的额外信息放入令牌中，这样下游微服务就能解析令牌获取
     * 逻辑分为四步：
     * 1.从 OAuth2Authentication 中获取认证成功的用户信息 SecurityUser
     * 2.新建一个 LinkedHashMap 存放额外的信息
     * 3.将额外信息添加到 additionalInformation 中
     * 4.调用父类的 enhance() 方法
     */
    public static class JwtAccessTokenEnhancer extends JwtAccessTokenConverter {
        /**
         * 重写enhance方法，在其中扩展
         */
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            Object principal = authentication.getUserAuthentication().getPrincipal();
            if (principal instanceof NuwaUserDetails) {
                //获取userDetailService中查询到用户信息
                NuwaUserDetails user = (NuwaUserDetails) authentication.getUserAuthentication().getPrincipal();
                //将额外的信息放入到LinkedHashMap中
                LinkedHashMap<String, Object> extendInformation = new LinkedHashMap<>();
                //设置用户的userId
                extendInformation.put(TokenConstant.TENANT_ID, user.getTenantId());
                extendInformation.put(TokenConstant.OAUTH_ID, user.getOauthId());
                extendInformation.put(TokenConstant.USER_ID, user.getId());
                extendInformation.put(TokenConstant.ORGANIZATION_ID, user.getOrganizationId());
                extendInformation.put(TokenConstant.ORGANIZATION_NAME, user.getOrganizationName());
                extendInformation.put(TokenConstant.ORGANIZATION_IDS, user.getOrganizationIds());
                extendInformation.put(TokenConstant.ORGANIZATION_NAMES, user.getOrganizationNames());
                extendInformation.put(TokenConstant.ROLE_ID, user.getRoleId());
                extendInformation.put(TokenConstant.ROLE_NAME, user.getRoleName());
                extendInformation.put(TokenConstant.ROLE_IDS, user.getRoleIds());
                extendInformation.put(TokenConstant.ROLE_NAMES, user.getRoleNames());
                extendInformation.put(TokenConstant.ACCOUNT, user.getAccount());
                extendInformation.put(TokenConstant.REAL_NAME, user.getRealName());
                extendInformation.put(TokenConstant.NICK_NAME, user.getNickname());
                extendInformation.put(TokenConstant.ROLE_ID_LIST, user.getRoleIdList());
                extendInformation.put(TokenConstant.ROLE_KEY_LIST, user.getRoleKeyList());
                extendInformation.put(TokenConstant.ORGANIZATION_ID_LIST, user.getOrganizationIdList());
                extendInformation.put(TokenConstant.AVATAR, user.getAvatar());
                extendInformation.put(TokenConstant.DATA_PERMISSION_TYPE_LIST, user.getDataPermissionTypeList());
                //添加到additionalInformation
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(extendInformation);
            }
            return super.enhance(accessToken, authentication);
        }
    }

}
