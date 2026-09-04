package com.study.nuwa.cloud.controller;

import com.study.nuwa.cloud.dto.CreateUserDTO;
import com.study.nuwa.cloud.entity.User;
import com.study.nuwa.cloud.entity.UserInfo;
import com.study.nuwa.cloud.service.IUserService;
import com.study.platform.annotation.auth.CurrentUser;
import com.study.platform.domain.NuwaUser;
import com.study.platform.result.Result;
import com.study.platform.util.BeanCopierUtils;
import com.study.nuwa.cloud.dto.RegisterUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * @ClassName: AccountController
 * @DESCRIPTION:
 * @author: 西门
 * @create: 2023-04-15 16:27
 **/
@RestController
@RequestMapping(value = "/account")
@RequiredArgsConstructor()
@Tag(name = "AccountController", description = "账号相关的前端控制器")
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
    @Operation(summary = "注册用户")
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
    @Operation(summary = "注册用户时校验用户是否存在")
    public Result<?> registerUserCheck(@RequestBody RegisterUserDTO user) {
        User userEntity = BeanCopierUtils.copyByClass(user, User.class);
        boolean exist = userService.checkUserExist(userEntity);
        return Result.data(!exist);
    }

    /**
     * 获取登录后的用户信息
     */
    @GetMapping("/user/info")
    @Operation(summary = "登录后获取用户个人信息")
    public Result<UserInfo> userInfo(@Parameter(hidden = true) @CurrentUser NuwaUser currentUser) {
        User user = new User();
        user.setId(currentUser.getId());
        UserInfo userInfo = userService.queryUserInfo(user);
        return Result.data(userInfo);
    }
}
