package com.study.oauth2.config;

import com.study.oauth2.exception.NuwaOAuth2ExceptionTranslator;
import com.study.oauth2.service.NuwaClientDetailsServiceImpl;
import com.study.oauth2.service.NuwaUserDetails;
import com.study.oauth2.token.NuwaTokenServices;
import com.study.platform.base.constant.AuthConstant;
import com.study.platform.base.constant.TokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.sql.DataSource;
import java.util.*;

/**
 * @ClassName: AuthorizationServerConfig OAuth2.0授权服务的配置类
 * @DESCRIPTION: 作为OAuth2.0需要满足的要求
 * 1. 继承 AuthorizationServerConfigurerAdapter
 * 2. 标注 @EnableAuthorizationServer 注解，这个注解标注是一个认证中心
 * @author: 西门
 * @create: 2023-04-02 14:02
 **/
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 数据源
     */
    private final DataSource dataSource;
    /**
     * 认证管理器
     */
    private final AuthenticationManager authenticationManager;
    /**
     * 自定义用户身份认证
     */
    private final UserDetailsService userDetailsService;
    private final NuwaOAuth2ExceptionTranslator nuwaOAuth2ExceptionTranslator;
    private final RedisTemplate redisTemplate;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final TokenStore tokenStore;

    /**
     * 客户端详情配置，比如秘钥，唯一id
     * 配置 client信息可存在内存和数据库中
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetailsService());
    }

    /**
     * 令牌访问端点的配置
     * 配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
     *
     * @param endpoints
     * @throws Exception
     */
    //@Override
    //public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    //    TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
    //    List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
    //    tokenEnhancers.add(tokenEnhancer());
    //    // 配置JwtAccessToken转换器
    //    tokenEnhancers.add(jwtAccessTokenConverter);
    //    tokenEnhancerChain.setTokenEnhancers(tokenEnhancers); // 配置JWT的内容增强器
    //    endpoints
    //            // 密码模式所需要的authenticationManager
    //            .authenticationManager(authenticationManager)
    //            .accessTokenConverter(jwtAccessTokenConverter)
    //            .tokenEnhancer(tokenEnhancerChain)
    //            // 配置加载用户信息的服务
    //            .userDetailsService(userDetailsService)
    //            // 令牌管理服务，无论哪种模式都需要
    //            .tokenServices(tokenServices())
    //            // 授权码模式所需要的authorizationCodeServices
    //            .authorizationCodeServices(authorizationCodeServices())
    //            .reuseRefreshTokens(false)
    //            //自定义异常返回消息
    //            .exceptionTranslator(nuwaOAuth2ExceptionTranslator)
    //            // 只允许post提交访问令牌，url: /oauth/token
    //            .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    //
    //}

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 授权码模式所需要的authorizationCodeServices
                .authorizationCodeServices(authorizationCodeServices())
                // 密码模式所需要的authenticationManager
                .authenticationManager(authenticationManager)
                // 令牌管理服务，无论那种模式都需要
                .tokenServices(tokenServices())
                // 只允许post提交访问令牌，url: /oauth/token
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 令牌端点安全约束配置，比如/oauth/token 对哪些开放
     * 配置授权服务器的安全性，令牌端点的安全约束
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 开启/oauth/token_key 验证端口-无权限
                .tokenKeyAccess("permitAll()")
                // 开启/oauth/check_token 验证端口-需权限
                .checkTokenAccess("permitAll()")
                // 允许表单认证
                // 如果配置，且url中有client_id和client_secret的，则走 ClientCredentialsTokenEndpointFilter
                // 如果没有配置，但是url中没有client_id和client_secret的，走basic认证保护
                .allowFormAuthenticationForClients();
    }

    /**
     * 将client信息存储在数据库中,存储client信息
     *
     * @return
     */
    @Bean
    public ClientDetailsService jdbcClientDetailsService() {
        NuwaClientDetailsServiceImpl jdbcClientDetailsService = new NuwaClientDetailsServiceImpl(dataSource);
        jdbcClientDetailsService.setFindClientDetailsSql(AuthConstant.FIND_CLIENT_DETAILS_SQL);
        jdbcClientDetailsService.setSelectClientDetailsSql(AuthConstant.SELECT_CLIENT_DETAILS_SQL);
        return jdbcClientDetailsService;
    }

    /**
     *
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> map = new HashMap<>(2);
            NuwaUserDetails user = (NuwaUserDetails) authentication.getUserAuthentication().getPrincipal();
            map.put(TokenConstant.TENANT_ID, user.getTenantId());
            map.put(TokenConstant.OAUTH_ID, user.getOauthId());
            map.put(TokenConstant.USER_ID, user.getId());
            map.put(TokenConstant.ORGANIZATION_ID, user.getOrganizationId());
            map.put(TokenConstant.ORGANIZATION_NAME, user.getOrganizationName());
            map.put(TokenConstant.ORGANIZATION_IDS, user.getOrganizationIds());
            map.put(TokenConstant.ORGANIZATION_NAMES, user.getOrganizationNames());
            map.put(TokenConstant.ROLE_ID, user.getRoleId());
            map.put(TokenConstant.ROLE_NAME, user.getRoleName());
            map.put(TokenConstant.ROLE_IDS, user.getRoleIds());
            map.put(TokenConstant.ROLE_NAMES, user.getRoleNames());
            map.put(TokenConstant.ACCOUNT, user.getAccount());
            map.put(TokenConstant.REAL_NAME, user.getRealName());
            map.put(TokenConstant.NICK_NAME, user.getNickname());
            map.put(TokenConstant.ROLE_ID_LIST, user.getRoleIdList());
            map.put(TokenConstant.ROLE_KEY_LIST, user.getRoleKeyList());
            map.put(TokenConstant.ORGANIZATION_ID_LIST, user.getOrganizationIdList());
            map.put(TokenConstant.AVATAR, user.getAvatar());
            map.put(TokenConstant.DATA_PERMISSION_TYPE_LIST, user.getDataPermissionTypeList());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }

    /**
     * 令牌管理服务配置
     *
     * @param endpoints
     * @return
     */
    //@Primary
    //@Bean
    //public DefaultTokenServices createDefaultTokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
    //    NuwaTokenServices tokenServices = new NuwaTokenServices(redisTemplate);
    //    // 令牌服务
    //    tokenServices.setTokenStore(tokenStore);
    //    // 支持刷新token
    //    tokenServices.setSupportRefreshToken(true);
    //    // 是否重复使用RefreshToken
    //    tokenServices.setReuseRefreshToken(false);
    //    // 客户端配置策略
    //    tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
    //    // access_token的过期时间
    //    tokenServices.setAccessTokenValiditySeconds(60 * 60 * 2);
    //    // refresh_token的过期时间
    //    tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
    //    tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
    //    addUserDetailsService(tokenServices, this.userDetailsService);
    //    return tokenServices;
    //}

    /**
     * 令牌管理服务
     *
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        // 客户端配置策略
        services.setClientDetailsService(jdbcClientDetailsService());
        // 支持令牌刷新
        services.setSupportRefreshToken(true);
        // 令牌服务
        services.setTokenStore(tokenStore);
        // access_token的过期时间
        services.setAccessTokenValiditySeconds(60 * 60 * 2);
        // refresh_token的过期时间
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);

        // 设置令牌增强, 使用jwtAccessTokenConverter进行转换
        services.setTokenEnhancer(jwtAccessTokenConverter);
        return services;
    }

    private void addUserDetailsService(DefaultTokenServices tokenServices, UserDetailsService userDetailsService) {
        if (userDetailsService != null) {
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(
                    userDetailsService));
            tokenServices
                    .setAuthenticationManager(new ProviderManager(Arrays.asList(provider)));
        }
    }

    /**
     * 授权码模式的service，使用授权码模式authorization_code必须注入
     *
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // 授权码存储在数据库中
        return new JdbcAuthorizationCodeServices(dataSource);
    }
}
