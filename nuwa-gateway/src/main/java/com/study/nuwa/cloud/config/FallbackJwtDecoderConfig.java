package com.study.nuwa.cloud.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

/**
 * Fallback ReactiveJwtDecoder Bean。
 * <p>
 * 触发条件：容器中没有任何 ReactiveJwtDecoder（即 spring-security-oauth2-resource-server 自动配置没有匹配）。
 * <p>
 * 使用场景：本地/容器 dev 环境没有 oauth2 认证服务时，gateway 仍能启动；真正的 token 校验交给
 * {@link com.study.nuwa.cloud.auth.AuthorizationManager}（Nimbus JWT 自解析），这里只让 Spring Security
 * OAuth2 ResourceServer 拿到一个 bean 通过启动校验。请求带非法 token 时由 AuthorizationManager 拒。
 *
 * @author WorkBuddy
 */
@Configuration
public class FallbackJwtDecoderConfig {

    @Bean
    @ConditionalOnMissingBean(ReactiveJwtDecoder.class)
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        // 不做任何解析；上游 AuthorizationManager 自己解析 JWT。这里返回 empty Mono 让过滤器链跳过。
        return token -> Mono.error(new UnsupportedOperationException(
                "JWT decoder not bound: please configure spring.security.oauth2.resourceserver.jwt.jwk-set-uri "
                        + "or supply your own ReactiveJwtDecoder bean."));
    }
}