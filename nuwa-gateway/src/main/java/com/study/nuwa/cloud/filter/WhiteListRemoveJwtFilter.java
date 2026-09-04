package com.study.nuwa.cloud.filter;

import com.study.platform.constant.AuthConstant;
import com.study.nuwa.platform.props.AuthUrlWhiteListProperties;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * 白名单路径访问时需要移除JWT请求头。
 *
 * <p>说明：本次升级期间 lombok 在 nuwa 项目的 maven 编译时未生效（详见 UPGRADE.md），
 * 这里把 {@code @Slf4j} / {@code @AllArgsConstructor} 改为手写 logger 和显式构造器。
 */
@Component
public class WhiteListRemoveJwtFilter implements WebFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WhiteListRemoveJwtFilter.class);

    private final AuthUrlWhiteListProperties authUrlWhiteListProperties;

    public WhiteListRemoveJwtFilter(AuthUrlWhiteListProperties authUrlWhiteListProperties) {
        this.authUrlWhiteListProperties = authUrlWhiteListProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String path = request.getURI().getPath();
        log.info("url=[{}], path=[{}]", uri, path);
        PathMatcher pathMatcher = new AntPathMatcher();
        //白名单路径移除JWT请求头
        List<String> ignoreUrls = authUrlWhiteListProperties.getWhiteUrls();
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                request = exchange.getRequest().mutate().header(AuthConstant.JWT_TOKEN_HEADER, "").build();
                exchange = exchange.mutate().request(request).build();
                return chain.filter(exchange);
            }
        }
        return chain.filter(exchange);
    }
}
