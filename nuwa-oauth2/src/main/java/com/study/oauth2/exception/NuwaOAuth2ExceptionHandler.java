package com.study.oauth2.exception;

import com.study.platform.base.enums.ResultCode;
import com.study.platform.base.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 自定义Oauth异常拦截处理器
 */
@Slf4j
@RestControllerAdvice
public class NuwaOAuth2ExceptionHandler {

    @ExceptionHandler(InvalidScopeException.class)
    public Result handleInvalidScopeException(InvalidScopeException e) {
        return Result.error(ResultCode.INVALID_SCOPE);
    }

    @ExceptionHandler(UnsupportedGrantTypeException.class)
    public Result handleUnsupportedGrantTypeException(UnsupportedGrantTypeException e) {
        return Result.error(ResultCode.UNSUPPORTED_GRANT_TYPE);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public Result handleInvalidTokenException(InvalidTokenException e) {
        return Result.error(ResultCode.UNAUTHORIZED);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public Result handleUsernameNotFoundException(UsernameNotFoundException e) {
        return Result.error(ResultCode.INVALID_USERNAME_PASSWORD);
    }

    @ExceptionHandler({InvalidGrantException.class})
    public Result handleInvalidGrantException(InvalidGrantException e) {
        return Result.error(ResultCode.INVALID_USERNAME_PASSWORD);
    }

    @ExceptionHandler({NuwaOAuth2Exception.class})
    public Result handleInvalidGrantException(NuwaOAuth2Exception e) {
        return Result.error(ResultCode.SYSTEM_BUSY);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public Result handleInvalidGrantException(InternalAuthenticationServiceException e) {
        Result result = Result.error(ResultCode.INVALID_USERNAME_PASSWORD);
        if (null != e) {
            String errorMsg = e.getMessage();
            if (ResultCode.INVALID_PASSWORD_CAPTCHA.getMessage().equals(errorMsg)) {
                //必须使用验证码
                result = Result.error(ResultCode.INVALID_PASSWORD_CAPTCHA);
            } else if (ResultCode.PASSWORD_TRY_MAX_ERROR.getMessage().equals(errorMsg)) {
                //账号被锁定
                result = Result.error(ResultCode.PASSWORD_TRY_MAX_ERROR);
            } else if (ResultCode.DISABLED_ACCOUNT.getMessage().equals(errorMsg)) {
                //账号被禁用
                result = Result.error(ResultCode.DISABLED_ACCOUNT);
            }
        }
        return result;
    }
}

