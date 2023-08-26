package com.study.service.system.controller;

import com.study.platform.base.result.Result;
import com.study.service.system.dto.UserDTO;
import com.study.service.system.holder.LoginUserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取登录用户信息接口
 * Created by macro on 2020/6/19.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "UserController|用户相关的前端控制器")
@RefreshScope
public class UserController {

    @Autowired
    private LoginUserHolder loginUserHolder;

    @ApiOperation("查询用户")
    @GetMapping("/currentUser")
    public UserDTO currentUser() {
        return loginUserHolder.getCurrentUser();
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
        return Result.success();
    }

}
