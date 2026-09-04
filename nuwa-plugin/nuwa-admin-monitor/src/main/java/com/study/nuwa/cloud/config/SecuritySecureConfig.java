package com.study.nuwa.cloud.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.ui.config.AdminServerUiProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

/**
 * Spring Boot Admin 3.x 安全配置（适配 Spring Security 6）。
 * <p>
 * 原 spring-boot-admin 2.x 提供的 {@code WebSecurityConfigurerAdapter} 写法在
 * Spring Security 6 中已移除，本类改用 {@link SecurityFilterChain} bean + {@link UserDetailsService} bean 模式。
 */
@Configuration(proxyBeanMethods = false)
public class SecuritySecureConfig {

    private final AdminServerUiProperties adminUi;
    private final AdminServerProperties adminServer;
    private final SecurityProperties security;

    public SecuritySecureConfig(AdminServerUiProperties adminUi,
                                AdminServerProperties adminServer,
                                SecurityProperties security) {
        this.adminUi = adminUi;
        this.adminServer = adminServer;
        this.security = security;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String publicUrl = this.adminUi.getPublicUrl() != null
                ? this.adminUi.getPublicUrl()
                : this.adminServer.getContextPath();

        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(publicUrl + "/");

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(this.adminServer.path("/assets/**")).permitAll()
                        .requestMatchers(this.adminServer.path("/actuator/info")).permitAll()
                        .requestMatchers(this.adminServer.path("/actuator/health")).permitAll()
                        .requestMatchers(this.adminServer.path("/login")).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage(publicUrl + "/login")
                        .loginProcessingUrl(this.adminServer.path("/login"))
                        .successHandler(successHandler))
                .logout(logout -> logout.logoutUrl(this.adminServer.path("/logout")))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(this.adminServer.path("/instances"), HttpMethod.POST.toString()),
                                new AntPathRequestMatcher(this.adminServer.path("/instances/*"), HttpMethod.DELETE.toString()),
                                new AntPathRequestMatcher(this.adminServer.path("/actuator/**"))))
                .rememberMe(rememberMe -> rememberMe
                        .key(UUID.randomUUID().toString())
                        .tokenValiditySeconds(1209600));
        return http.build();
    }

    @Bean
    public PasswordEncoder adminPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder adminPasswordEncoder) {
        SecurityProperties.User user = this.security.getUser();
        UserDetails details = User.withUsername(user.getName())
                .password(adminPasswordEncoder.encode(user.getPassword()))
                .roles(user.getRoles().toArray(new String[0]))
                .build();
        return new InMemoryUserDetailsManager(details);
    }
}
