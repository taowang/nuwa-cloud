package com.study.nuwa.cloud.handler;

import com.study.nuwa.cloud.config.SwaggerResourceConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关层 API 文档聚合端点（兼容老 knife4j 客户端调用习惯）。
 * <p>
 * knife4j 3.x 调用 {@code /swagger-resources}；springdoc-openapi + knife4j 4.x 的标准端点是
 * {@code /v3/api-docs/swagger-config}，由 springdoc 自动暴露。本类保留旧端点以便老集成能继续访问，
 * 实际内容由 {@link SwaggerResourceConfig} 提供。
 * <p>
 * 真正的 springdoc 文档访问入口：
 * <ul>
 *   <li>聚合 UI：{@code /doc.html}</li>
 *   <li>单服务文档：{@code /<service-path>/v3/api-docs}</li>
 * </ul>
 *
 * <p>说明：原实现使用 {@code @RequiredArgsConstructor} 注入 {@link SwaggerResourceConfig}。本次升级
 * 期间发现 lombok 在 nuwa 项目的 maven 编译时未生效（详见 UPGRADE.md 第 6 节），为保证网关在 SB3 下可编译，
 * 这里改用 {@code @Autowired} 字段注入；{@code @Slf4j} 改成手写 logger。后续 lombok 修复后可还原。
 */
@RestController
public class SwaggerHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SwaggerHandler.class);

    private SwaggerResourceConfig swaggerResourceConfig;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSwaggerResourceConfig(SwaggerResourceConfig swaggerResourceConfig) {
        this.swaggerResourceConfig = swaggerResourceConfig;
    }

    /**
     * 兼容老客户端：返回聚合服务列表。
     */
    @GetMapping("/swagger-resources")
    public Mono<ResponseEntity<Object>> swaggerResources() {
        Map<String, Object> body = new HashMap<>();
        if (swaggerResourceConfig == null) {
            log.warn("[springdoc] SwaggerResourceConfig 尚未就绪，请稍后再试");
            return Mono.just(new ResponseEntity<>(body, HttpStatus.OK));
        }
        swaggerResourceConfig.buildSwaggerUrls().forEach(u -> {
            Map<String, String> entry = new HashMap<>();
            entry.put("name", u.getName());
            entry.put("url", u.getUrl());
            entry.put("swaggerVersion", "3.0.3");
            entry.put("location", u.getUrl());
            body.put(u.getName(), entry);
        });
        log.debug("[springdoc] 兼容端点 /swagger-resources 已废弃，请改用 /v3/api-docs/swagger-config");
        return Mono.just(new ResponseEntity<>(body, HttpStatus.OK));
    }
}
