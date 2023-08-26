package com.study.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ResponseGlobalFilter implements GlobalFilter, Ordered {

    private String crossOriginPath = "http://localhost:80";


    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.contains("/oauth/authorize") ) {
            //构建响应包装类
            HttpResponseDecorator responseDecorator = new HttpResponseDecorator(exchange.getRequest(), exchange.getResponse(), crossOriginPath);
            return chain
                    .filter(exchange.mutate().response(responseDecorator).build());
        }
        return chain.filter(exchange);
    }
}
