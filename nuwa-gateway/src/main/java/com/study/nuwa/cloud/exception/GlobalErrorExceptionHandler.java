package com.study.nuwa.cloud.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.platform.enums.ResultCode;
import com.study.platform.result.Result;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用于网关的全局异常处理。
 *
 * @Order(-1)：优先级一定要比ResponseStatusExceptionHandler低
 *
 * <p>说明：本次升级期间 lombok 在 nuwa 项目的 maven 编译时未生效（详见 UPGRADE.md），
 * {@code @Slf4j} / {@code @RequiredArgsConstructor} 改为手写 logger 和注入。
 */
@Order(-1)
@Component
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalErrorExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public GlobalErrorExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        Result resultMsg = Result.error(ResultCode.UNAUTHORIZED);


        // JOSN格式返回
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            // Spring 6：getStatus() 已改为 getStatusCode()，返回 HttpStatusCode
            response.setStatusCode(((ResponseStatusException) ex).getStatusCode());
        }

        //处理TOKEN失效的异常
        if (ex instanceof InvalidBearerTokenException) {
            resultMsg = Result.error(ResultCode.UNAUTHORIZED.getCode(), StringUtils.isEmpty(ex.getMessage())
                    ? ResultCode.UNAUTHORIZED.getMessage() : ex.getMessage());
        }

        Result finalResultMsg = resultMsg;
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                //todo 返回响应结果，根据业务需求，自己定制
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(finalResultMsg));
            } catch (Exception e) {
                log.error("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
