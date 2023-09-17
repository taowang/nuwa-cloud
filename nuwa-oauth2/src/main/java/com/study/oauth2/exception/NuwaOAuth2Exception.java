package com.study.oauth2.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;


/**
 * 自定义一个异常，继承OAuth2Exception，并添加序列化
 */
@JsonSerialize(using = NuwaOAuthExceptionJackson2Serializer.class)
@JsonDeserialize(using = NuwaOAuth2ExceptionJackson2Deserializer.class)
public class NuwaOAuth2Exception extends OAuth2Exception {

    public NuwaOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

    public NuwaOAuth2Exception(String msg) {
        super(msg);
    }
}
