package com.study.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

public class HttpResponseDecorator extends ServerHttpResponseDecorator {

    private String proxyUrl;

    private ServerHttpRequest request;

    /**
     * 构造函数
     *
     * @param delegate
     */
    public HttpResponseDecorator(ServerHttpRequest request, ServerHttpResponse delegate, String proxyUrl) {
        super(delegate);
        this.request = request;
        this.proxyUrl = proxyUrl;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        HttpStatus status = this.getStatusCode();
        if (status.equals(HttpStatus.FOUND)) {
            String domain = proxyUrl + "/nuwa-oauth2";
            String location = getHeaders().getFirst("Location");
            String replaceLocation = location.replaceAll("^((ht|f)tps?):\\/\\/(\\d{1,3}.){3}\\d{1,3}(:\\d+)?", domain);
            if (location.contains("code=")) {
            } else {
                getHeaders().set("Location", replaceLocation);
            }
        }
        this.getStatusCode();
        return super.writeWith(body);
    }
}