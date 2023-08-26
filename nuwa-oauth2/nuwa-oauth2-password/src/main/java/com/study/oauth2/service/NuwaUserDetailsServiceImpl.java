package com.study.oauth2.service;

import cn.hutool.core.bean.BeanUtil;
import com.study.platform.base.constant.AuthConstant;
import com.study.platform.base.constant.NuwaConstant;
import com.study.platform.base.domain.NuwaUser;
import com.study.platform.base.enums.ResultCode;
import com.study.platform.base.result.Result;
import com.study.service.system.api.feign.IUserFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 实现SpringSecurity获取用户信息接口
 *  从数据库中根据用户名查询用户的详细信息，包括权限
 *  数据库设计：角色、用户、权限、角色<->权限、用户<->角色，总共五张表，遵循RBAC设计
 * @author nuwa
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NuwaUserDetailsServiceImpl implements UserDetailsService {

    // 密码最大尝试次数
    @Value("${system.maxTryTimes:5}")
    private int maxTryTimes;

    // 不需要验证码登录的最大尝试次数
    @Value("${system.maxNonCaptchaTimes:3}")
    private int maxNonCaptchaTimes;

    private final IUserFeign userFeign;
    private final RedisTemplate redisTemplate;

    @Override
    public NuwaUserDetails loadUserByUsername(String username) {

        // 远程调用返回数据
        Result<Object> result = userFeign.queryUserByAccount(username);
        if (result == null || !result.isSuccess()) {
            throw new UsernameNotFoundException(result.getMsg());
        }
        // 判断返回信息
        NuwaUser nuwaUser = new NuwaUser();
        BeanUtil.copyProperties(result.getData(), nuwaUser, false);
        // 用户名或密码错误
        if (nuwaUser == null || nuwaUser.getId() == null) {
            throw new UsernameNotFoundException(ResultCode.INVALID_USERNAME.getMessage());
        }
        // 没有角色
        if (CollectionUtils.isEmpty(nuwaUser.getRoleIdList())) {
            throw new UserDeniedAuthorizationException(ResultCode.INVALID_ROLE.getMessage());
        }

        // 从Redis获取账号密码错误次数
        Object lockTimes = redisTemplate.boundValueOps(AuthConstant.LOCK_ACCOUNT_PREFIX + nuwaUser.getId()).get();
        /**
         // 判断账号密码输入错误几次，如果输入错误多次，则锁定账号
         // 输入错误大于配置的次数，必须选择captcha或sms_captcha
         if (null != lockTimes && (int) lockTimes > maxNonCaptchaTimes) {
         throw new NuwaOAuth2Exception(ResultCode.INVALID_PASSWORD_CAPTCHA.msg);
         }
         */

        // 判断账号是否被锁定（账户过期，凭证过期等可在此处扩展）
        if (null != lockTimes && (int) lockTimes >= maxTryTimes) {
            throw new LockedException(ResultCode.PASSWORD_TRY_MAX_ERROR.getMessage());
        }

        // 判断账号是否被禁用
        String userStatus = nuwaUser.getStatus();
        if (String.valueOf(NuwaConstant.DISABLE).equals(userStatus)) {
            throw new DisabledException(ResultCode.DISABLED_ACCOUNT.getMessage());
        }
        /**
         * enabled 账户是否被禁用  !String.valueOf(GitEggConstant.DISABLE).equals(gitEggUser.getStatus())
         * AccountNonExpired 账户是否过期  此框架暂时不提供账户过期功能，可根据业务需求在此处扩展
         * AccountNonLocked  账户是否被锁  密码尝试次数过多，则锁定账户
         * CredentialsNonExpired 凭证是否过期
         */
        return new NuwaUserDetails(
                nuwaUser.getId(),
                nuwaUser.getTenantId(),
                nuwaUser.getOauthId(),
                nuwaUser.getNickname(),
                nuwaUser.getRealName(),
                nuwaUser.getOrganizationId(),
                nuwaUser.getOrganizationName(),
                nuwaUser.getOrganizationIds(),
                nuwaUser.getOrganizationNames(),
                nuwaUser.getRoleId(),
                nuwaUser.getRoleIds(),
                nuwaUser.getRoleName(),
                nuwaUser.getRoleNames(),
                nuwaUser.getRoleIdList(),
                nuwaUser.getRoleKeyList(),
                nuwaUser.getResourceKeyList(),
                nuwaUser.getDataPermission(),
                nuwaUser.getAvatar(),
                nuwaUser.getAccount(),
                nuwaUser.getPassword(),
                !String.valueOf(NuwaConstant.DISABLE).equals(nuwaUser.getStatus()),
                true,
                true,
                true,
                this.createAuthorityList(nuwaUser.getRoleKeyList(), nuwaUser.getResourceUrlList()));
    }

    /**
     * 设置SpringSecurity需要的role和resource
     *
     * @param roles
     * @param resources
     * @return
     */
    private final List<GrantedAuthority> createAuthorityList(final Collection<String> roles, final Collection<String> resources) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        //不将resource权限加入token，这样会导致请求头很大
//        for (final String resource : resources) {
//            authorities.add(new SimpleGrantedAuthority(resource));
//        }
        return authorities;
    }
}