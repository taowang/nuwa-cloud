package com.study.oauth2.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurity安全配置类
 * Created by macro on 2020/6/19.
 */
@Configuration
@EnableWebSecurity
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "/resources/**",
            "/static/**",
            "/css/**",
            "/ajax/**",
            "/fonts/**",
            "/img/**",
            "/js/**",
            "/login/**",
            "/webjars/**",
            "/error/**",
            "/favicon.ico",
            "/oauth/check_token"
    };

    /**
     * 放行静态资源
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(CLASSPATH_RESOURCE_LOCATIONS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .formLogin()
                // 自定义处理登录逻辑的地址login
                .loginProcessingUrl("/login")
                // 自定义登录页面
                .loginPage("/base-login.html")
                .permitAll()
                .and()
                .requestMatchers().antMatchers("/rsa/publicKey", "/oauth/authorize", "/login/**", "/logout/**", "/view/**")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .antMatchers(
                        "/xxx/**").permitAll()//一些需要放开的URL
                .anyRequest().authenticated()
                .and().headers().frameOptions().disable();

        //http.csrf().disable();
        //http
        //        .requestMatchers().antMatchers("/oauth/**","/login/**","/logout/**")
        //        .and()
        //        .authorizeRequests()
        //        .antMatchers("/oauth/**").authenticated()
        //        .and()
        //        .formLogin().permitAll(); //新增login form支持用户登录及授权
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}