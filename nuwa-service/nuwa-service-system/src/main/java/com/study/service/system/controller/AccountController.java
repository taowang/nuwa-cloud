package com.study.service.system.controller;

import com.study.platform.base.annotation.auth.CurrentUser;
import com.study.platform.base.domain.NuwaUser;
import com.study.platform.base.result.Result;
import com.study.platform.base.util.BeanCopierUtils;
import com.study.service.system.dto.CreateUserDTO;
import com.study.service.system.dto.RegisterUserDTO;
import com.study.service.system.entity.User;
import com.study.service.system.entity.UserInfo;
import com.study.service.system.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * @ClassName: AccountController
 * @DESCRIPTION:
 * @author: 西门
 * @create: 2023-04-15 16:27
 **/
@RestController
@RequestMapping(value = "/account")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "AccountController|账号相关的前端控制器")
@Slf4j
@RefreshScope
public class AccountController {

    private final IUserService userService;

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册用户")
    public Result<?> registerUser(@Valid @RequestBody RegisterUserDTO user) {
        CreateUserDTO createUser = BeanCopierUtils.copyByClass(user, CreateUserDTO.class);
        CreateUserDTO userDTO = userService.createUser(createUser);
        return Result.data(userDTO.getId());
    }

    /**
     * 注册用户时，校验用户是否存在
     *
     * @param user
     * @return
     */
    @PostMapping("/register/check")
    @ApiOperation(value = "注册用户")
    public Result<?> registerUserCheck(@RequestBody RegisterUserDTO user) {
        User userEntity = BeanCopierUtils.copyByClass(user, User.class);
        boolean exist = userService.checkUserExist(userEntity);
        return Result.data(!exist);
    }

    /**
     * 获取登录后的用户信息
     */
    @GetMapping("/user/info")
    @ApiOperation(value = "登录后获取用户个人信息")
    public Result<UserInfo> userInfo(@ApiIgnore @CurrentUser NuwaUser currentUser) {
        User user = new User();
        user.setId(currentUser.getId());
        UserInfo userInfo = userService.queryUserInfo(user);
        return Result.data(userInfo);
    }
}
