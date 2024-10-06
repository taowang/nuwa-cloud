package com.study.nuwa.cloud.filter;

import cn.hutool.core.util.StrUtil;
import com.nimbusds.jose.JWSObject;
import com.study.platform.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 全局过滤器
 * 将登录用户的JWT转化成用户信息的全局过滤器
 */
@Slf4j
@Component
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String tenantId = exchange.getRequest().getHeaders().getFirst(AuthConstant.TENANT_ID);
        //2、 检查token是否存在
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if (StrUtil.isEmpty(tenantId) && StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }

        Map<String, String> addHeaders = new HashMap<>();
        if (!StrUtil.isEmpty(token) && token.startsWith(AuthConstant.JWT_TOKEN_PREFIX)) {
            try {
                //从token中解析用户信息并设置到Header中去
                String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
                JWSObject jwsObject = JWSObject.parse(realToken);
                String userStr = jwsObject.getPayload().toString();
                log.info("AuthGlobalFilter.filter() User:{}", userStr);
                addHeaders.put(AuthConstant.HEADER_USER, URLEncoder.encode(userStr, "UTF-8"));

            } catch (ParseException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
            addHeaders.forEach((k, v) -> {
                httpHeader.set(k, v);
            });
        };

        ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders).build();
        exchange = exchange.mutate().request(request).build();
        String path = request.getPath().pathWithinApplication().value();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        log.info("请求路径:{},远程IP地址:{}", path, remoteAddress);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
