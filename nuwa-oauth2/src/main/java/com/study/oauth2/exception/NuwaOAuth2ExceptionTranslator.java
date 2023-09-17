package com.study.oauth2.exception;

import com.study.platform.base.enums.ResultCode;
import com.study.platform.base.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * 自定义异常翻译器，针对用户名、密码异常、授权类型不支持的异常进行处理
 */
@Component
public class NuwaOAuth2ExceptionTranslator implements WebResponseExceptionTranslator {

    /**
     * 业务处理方法，重写这个方法返回客户端信息
     *
     * @param exception
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity<Result> translate(Exception exception) throws Exception {
        ResponseEntity response = doTranslatehandler(exception);
        return new ResponseEntity(response, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity doTranslatehandler(Exception exception) {
        if (exception instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
            return ResponseEntity
                    .status(oAuth2Exception.getHttpErrorCode())
                    .body(new NuwaOAuth2Exception(oAuth2Exception.getMessage()));
        } else if (exception instanceof AuthenticationException) {
            AuthenticationException authenticationException = (AuthenticationException) exception;
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new NuwaOAuth2Exception(authenticationException.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new NuwaOAuth2Exception(exception.getMessage()));

    }
}
