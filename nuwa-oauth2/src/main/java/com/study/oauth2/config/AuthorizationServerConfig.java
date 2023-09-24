package com.study.oauth2.config;

import com.study.oauth2.filter.NuwaClientCredentialsTokenEndpointFilter;
import com.study.oauth2.exception.NuwaOAuth2ExceptionTranslator;
import com.study.oauth2.exception.NuwaOAuthServerAuthenticationEntryPoint;
import com.study.oauth2.service.NuwaClientDetailsServiceImpl;
import com.study.platform.base.constant.AuthConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

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

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final TokenStore tokenStore;

    private final NuwaOAuthServerAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 客户端配置
     * 配置客户端详情，并不是所有的客户端都有权限向认证中心申请令牌，因此一些必要的配置是要认证中心分配给你的，比如 客户端唯一Id 、 秘钥 、 权限 。
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
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints
                //设置异常WebResponseExceptionTranslator，用于处理用户名，密码错误、授权类型不正确的异常、自定义异常返回消息
                .exceptionTranslator(new NuwaOAuth2ExceptionTranslator())
                // 授权码模式所需要的authorizationCodeServices
                .authorizationCodeServices(authorizationCodeServices())
                // 密码模式所需要的authenticationManager
                .authenticationManager(authenticationManager)
                // 令牌管理服务，无论哪种模式都需要
                .tokenServices(tokenServices())
                //授权页面的url替换
//                .pathMapping("/oauth/confirm_access","自定义的url")
                //异常页面的url替换
//                .pathMapping("/oauth/error","自定义的url")
                .reuseRefreshTokens(false)
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
        NuwaClientCredentialsTokenEndpointFilter endpointFilter = new NuwaClientCredentialsTokenEndpointFilter(security);
        endpointFilter.afterPropertiesSet();
        endpointFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
        security.addTokenEndpointAuthenticationFilter(endpointFilter);
        security
                // 开启/oauth/token_key 验证端口-无权限
                .tokenKeyAccess("permitAll()")
                // 开启/oauth/check_token 验证端口-需权限
                .checkTokenAccess("permitAll()");
        // 允许表单认证
        // 如果配置，且url中有client_id和client_secret的，则走 ClientCredentialsTokenEndpointFilter
        // 如果没有配置，但是url中没有client_id和client_secret的，走basic认证保护
        //.allowFormAuthenticationForClients();
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
     * 令牌管理服务的配置
     * 用来创建、获取、刷新令牌
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

    /**
     * 授权码模式的service，使用授权码模式authorization_code必须注入
     * 使用授权码模式必须配置一个授权码服务，用来颁布和删除授权码，当然授权码也支持多种存储方式存储（比如内存、数据库
     *
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // 授权码存储在数据库中
        return new JdbcAuthorizationCodeServices(dataSource);
    }
}
