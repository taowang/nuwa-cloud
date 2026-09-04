package com.study.nuwa.cloud.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

public class RequestContextUtils {
    /**
     * 获取HttpServletRequest
     */
    public static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes)(Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
    }
}
