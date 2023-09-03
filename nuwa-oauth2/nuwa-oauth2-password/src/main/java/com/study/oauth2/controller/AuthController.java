package com.study.oauth2.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.study.oauth2.domain.Oauth2TokenDto;
import com.study.oauth2.util.JwtUtils;
import com.study.platform.base.constant.AuthConstant;
import com.study.platform.base.constant.NuwaConstant;
import com.study.platform.base.result.Result;
import io.swagger.annotations.ApiOperation;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 自定义Oauth2获取令牌接口
 * Created by macro on 2020/7/17.
 */
@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 授权码模式-跳转接口
     *
     * @param model
     * @return
     */
    @ApiOperation(value = "表单登录跳转页面")
    @GetMapping("/login")
    public ModelAndView loginPage(Model model) {
        //返回跳转页面
        ModelAndView loginModel = new ModelAndView("oauth-login");
        return loginModel;
    }

    @ApiOperation(value = "处理授权异常的跳转页面")
    @GetMapping("/error")
    public ModelAndView error(Model model){
        ModelAndView errorModel = new ModelAndView("oauth-error");
        return errorModel;
    }

    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public Result<Oauth2TokenDto> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();
        return Result.data(oauth2TokenDto);
    }

    @GetMapping("/public_key")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

    //生成密码
    @GetMapping("/genPassWord")
    public Result<String> genPassWord() {
        return Result.success("genPassWord");
    }

    /**
     * 退出登录需要需要登录的一点思考：
     * 1、如果不需要登录，那么在调用接口的时候就需要把token传过来，且系统不校验token有效性，此时如果系统被攻击，不停的大量发送token，最后会把redis充爆
     * 2、如果调用退出接口必须登录，那么系统会调用token校验有效性，refresh_token通过参数传过来加入黑名单
     * 综上：选择调用退出接口需要登录的方式
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result logout(HttpServletRequest request) {

        String token = request.getHeader(AuthConstant.JWT_TOKEN_HEADER);
        String refreshToken = request.getParameter(AuthConstant.REFRESH_TOKEN);
        long currentTimeSeconds = System.currentTimeMillis() / NuwaConstant.Number.THOUSAND;

        // 将token和refresh_token同时加入黑名单
        String[] tokenArray = new String[NuwaConstant.Number.TWO];
        tokenArray[NuwaConstant.Number.ZERO] = token.replace("Bearer ", "");
        tokenArray[NuwaConstant.Number.ONE] = refreshToken;
        for (int i = NuwaConstant.Number.ZERO; i < tokenArray.length; i++) {
            String realToken = tokenArray[i];
            if (StringUtils.isEmpty(realToken)) {
                continue;
            }
            JSONObject jsonObject = JwtUtils.decodeJwt(realToken);
            String jti = jsonObject.getAsString("jti");
            Long exp = Long.parseLong(jsonObject.getAsString("exp"));
            if (exp - currentTimeSeconds > NuwaConstant.Number.ZERO) {
                redisTemplate.opsForValue().set(AuthConstant.TOKEN_BLACKLIST + jti, jti, (exp - currentTimeSeconds), TimeUnit.SECONDS);
            }
        }
        return Result.success();
    }
}