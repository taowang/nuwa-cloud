package com.study.nuwa.cloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 网关层 springdoc-openapi 聚合配置。
 * <p>
 * springfox 时代通过实现 {@code SwaggerResourcesProvider} 提供聚合；springdoc 体系下改为
 * 通过 {@link org.springdoc.core.properties.SwaggerUiConfigParameters} 注入 URL。
 * 这里从网关路由表中读取 Path 断言，拼成下游服务的 OpenAPI 文档 URL。
 * <p>
 * 注意：knife4j 4.x 的网关聚合需要在每个下游服务都暴露 {@code /v3/api-docs}（springdoc 默认），
 * 且网关层把对应路径转发过去。
 *
 * <p>说明：本次升级期间 lombok 在 maven 编译时未生效（详见 UPGRADE.md），把 {@code @Slf4j}
 * 改写为手写 {@code LoggerFactory}。
 */
@Configuration
public class SwaggerResourceConfig {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SwaggerResourceConfig.class);

    /**
     * springdoc-openapi 默认的文档 URL 后缀
     */
    public static final String OPENAPI3_URL_SUFFIX = "/v3/api-docs";

    @Value("${spring.application.name:}")
    private String self;

    private final GatewayProperties gatewayProperties;

    public SwaggerResourceConfig(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @PostConstruct
    public void logAggregatedRoutes() {
        log.info("[springdoc] 网关聚合 OpenAPI 文档，将基于 {} 条路由自动发现下游服务",
                gatewayProperties.getRoutes().size());
    }

    /**
     * 构造下游服务的 OpenAPI 文档 URL 列表（供 knife4j/swgger-ui 拉取）。
     */
    public List<SwaggerUrl> buildSwaggerUrls() {
        List<SwaggerUrl> urls = new ArrayList<>();
        gatewayProperties.getRoutes().stream()
                .filter(r -> r.getPredicates() != null)
                .forEach(route -> route.getPredicates().stream()
                        .filter(p -> "Path".equalsIgnoreCase(p.getName()))
                        .forEach(p -> {
                            String path = p.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0");
                            if (path != null) {
                                String location = path.replace("/**", OPENAPI3_URL_SUFFIX);
                                SwaggerUrl url = new SwaggerUrl();
                                url.setName(route.getId());
                                url.setUrl(location);
                                urls.add(url);
                                log.info("[springdoc] 注册聚合: route={}, url={}", route.getId(), location);
                            }
                        }));
        return urls;
    }
}
