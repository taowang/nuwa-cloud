package com.study.oauth2.config;

import cn.hutool.core.util.StrUtil;
import com.study.oauth2.service.NuwaUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurity安全配置类
 */
@Configuration
@EnableWebSecurity
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier(value = "nuwaUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
    };

    private static final String[] INTERFACE_RESOURCE_LOCATIONS = {
            "/oauth/login",
            "/oauth/token",
            "/oauth/public_key",
            "/oauth/genPassWord",
            "/oauth/logout",
            "/form/login",
            "/oauth/check_token"
    };


    /**
     * 放行静态资源
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        if (CLASSPATH_RESOURCE_LOCATIONS == null || CLASSPATH_RESOURCE_LOCATIONS.length == 0) {
            return;
        }
        web.ignoring().antMatchers(CLASSPATH_RESOURCE_LOCATIONS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // todo 允许表单登录
        http
                .authorizeRequests().antMatchers(INTERFACE_RESOURCE_LOCATIONS).permitAll()
                .anyRequest().authenticated();
        http
                .formLogin()
                // 处理登录逻辑的url
                .loginProcessingUrl("/form/login")
                // form表单登录页面
                .loginPage("/oauth/login");
        http.csrf().disable();
    }

    /**
     * AuthenticationManager对象在OAuth2认证服务中要使用，提前放入IOC容器中
     * Oauth的密码模式需要
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //从数据库中查询用户信息
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}