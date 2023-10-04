package com.study.gateway.auth;

import cn.hutool.core.convert.Convert;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.study.platform.base.constant.AuthConstant;
import com.study.platform.base.constant.TokenConstant;
import com.study.platform.oauth2.props.AuthUrlWhiteListProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 网关鉴权管理器，用于自定义权限校验
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RedisTemplate redisTemplate;

    private final AuthUrlWhiteListProperties authUrlWhiteListProperties;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        // 匹配url
        PathMatcher pathMatcher = new AntPathMatcher();
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        // 获取请求方法，POST、GET
        HttpMethod method = request.getMethod();
        // 对应跨域的预检请求直接放行
        if (method == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // token为空拒绝访问
        String token = request.getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }

        //如果token被加入到黑名单，就是执行了退出登录操作，那么拒绝访问
        String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
        try {
            JWSObject jwsObject = JWSObject.parse(realToken);
            Payload payload = jwsObject.getPayload();
            JSONObject jsonObject = payload.toJSONObject();
            String jti = jsonObject.getAsString(TokenConstant.JTI);
            String blackListToken = (String) redisTemplate.opsForValue().get(AuthConstant.TOKEN_BLACKLIST + jti);
            if (!org.springframework.util.StringUtils.isEmpty(blackListToken)) {
                return Mono.error(new InvalidTokenException("无效的token！"));
            }
        } catch (ParseException e) {
            log.error("获取token黑名单时发生错误：{}", e);
        }

        // 需要鉴权但是每一个角色都需要的url，统一配置，不需要单个配置
        List<String> authUrls = authUrlWhiteListProperties.getAuthUrls();
        String urls = authUrls.stream().filter(url -> pathMatcher.match(url, path)).findAny().orElse(null);
        // 当配置了功能鉴权url时，直接放行，用户都有的功能，但是必须要登录才能用，例：退出登录功能是每个用户都有的权限，但是这个必须要登录才能够调用
        if (null != urls) {
            return mono.filter(Authentication::isAuthenticated)
                    .map(auth -> new AuthorizationDecision(true))
                    .defaultIfEmpty(new AuthorizationDecision(false));
        }
        String redisRoleKey = AuthConstant.RESOURCE_ROLES_KEY;
        //  缓存取资源权限角色关系列表
        Map<Object, Object> resourceRolesMap = redisTemplate.opsForHash().entries(redisRoleKey);
        Iterator<Object> iterator = resourceRolesMap.keySet().iterator();

        //请求路径匹配到的资源需要的角色权限集合authorities统计
        List<String> authorities = new ArrayList<>();
        while (iterator.hasNext()) {
            String pattern = (String) iterator.next();
            if (pathMatcher.match(pattern, path)) {
                authorities.addAll(Convert.toList(String.class, resourceRolesMap.get(pattern)));
            }
        }
        Mono<AuthorizationDecision> authorizationDecisionMono = mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(roleKey -> {
                    // roleId是请求用户的角色(格式:ROLE_{roleKey})，authorities是请求资源所需要角色的集合
                    log.info("访问路径：{}", path);
                    log.info("用户角色roleKey：{}", roleKey);
                    log.info("资源需要权限authorities：{}", authorities);
                    return authorities.contains(roleKey);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
        return authorizationDecisionMono;
    }
}
