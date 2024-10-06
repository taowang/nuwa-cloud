package com.study.nuwa.cloud.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.study.nuwa.cloud.domain.Oauth2TokenDto;
import com.study.nuwa.cloud.exception.NuwaOAuth2ExceptionTranslator;
import com.study.nuwa.cloud.util.JwtUtils;
import com.study.platform.constant.AuthConstant;
import com.study.platform.constant.NuwaConstant;
import com.study.platform.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

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
@Controller
@RequestMapping("/oauth")
@Slf4j
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CheckTokenEndpoint checkTokenEndpoint;

    //自定义异常翻译器，针对用户名、密码异常，授权类型不支持的异常进行处理
    @Autowired
    private NuwaOAuth2ExceptionTranslator translate;


    /**
     * 授权码模式-跳转接口
     *
     * @param model
     * @return
     */
    @ApiOperation(value = "表单登录跳转页面")
    @GetMapping("/login")
    public String loginPage(Model model) {
        //返回跳转页面
        return "oauth-login";
    }

    @ApiOperation(value = "处理授权异常的跳转页面")
    @GetMapping("/error")
    public String error(Model model) {
        return "oauth-error";
    }

    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    public Result<Oauth2TokenDto> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken() == null ? "" : oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();
        return Result.data(oauth2TokenDto);
    }

    /**
     * 重写/oauth/check_token这个默认接口，用于校验令牌，返回的数据格式统一
     */
    @PostMapping(value = "/check_token")
    @ResponseBody
    public Result<Map<String, ?>> checkToken(@RequestParam("token") String token) {
        Map<String, ?> map = checkTokenEndpoint.checkToken(token);
        return Result.data(map);
    }

    @GetMapping("/public_key")
    @ResponseBody
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

    //生成密码
    @GetMapping("/genPassWord")
    @ResponseBody
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
    @ResponseBody
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