package com.study.oauth2.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;


@JsonSerialize(using = NuwaOAuth2ExceptionSerializer.class)
public class NuwaOAuth2Exception extends OAuth2Exception {

    public NuwaOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

    public NuwaOAuth2Exception(String msg) {
        super(msg);
    }
}
