package com.study.oauth2.listener;

import com.study.oauth2.service.NuwaUserDetails;
import com.study.platform.base.constant.AuthConstant;
import com.study.platform.base.constant.NuwaConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 当登录失败时的调用，当密码错误过多时，则锁定账户
 *
 * @author GitEgg
 * @date 2021-03-12 17:57:05
 **/
@Slf4j
//@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private static Logger LOGGER = LoggerFactory.getLogger(LoginFailureListener.class);

    private final UserDetailsService userDetailsService;

    private final RedisTemplate redisTemplate;

    @Value("${system.maxTryTimes}")
    private int maxTryTimes;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        if (event.getException().getClass().equals(UsernameNotFoundException.class)) {
            return;
        }
        String userName = event.getAuthentication().getName();
        NuwaUserDetails user = (NuwaUserDetails) userDetailsService.loadUserByUsername(userName);

        if (null != user) {
            Object lockTimes = redisTemplate.boundValueOps(AuthConstant.LOCK_ACCOUNT_PREFIX + user.getId()).get();
            LOGGER.warn("用户{}登陆失败，密码错误第{}次，超过{}次则锁定账户", userName, lockTimes == null ? 1 : (int) lockTimes + 1, maxTryTimes);
            if (null == lockTimes || (int) lockTimes <= maxTryTimes) {
                redisTemplate.boundValueOps(AuthConstant.LOCK_ACCOUNT_PREFIX + user.getId()).increment(NuwaConstant.Number.ONE);
                redisTemplate.expire(AuthConstant.LOCK_ACCOUNT_PREFIX + user.getId(), 60 * 60 * 2, TimeUnit.SECONDS);
            }
        }
    }
}
