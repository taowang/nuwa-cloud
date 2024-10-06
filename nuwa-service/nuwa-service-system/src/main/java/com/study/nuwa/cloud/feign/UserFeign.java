package com.study.nuwa.cloud.feign;

import com.study.nuwa.cloud.service.IUserService;
import com.study.platform.result.Result;
import com.study.nuwa.cloud.entity.User;
import com.study.nuwa.cloud.entity.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserFeign
 * @Description: UserFeign前端控制器
 * @author gitegg
 * @date 2019年5月18日 下午4:03:58
 */
@RestController
@RequestMapping(value = "/feign/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "UserFeign|提供微服务调用接口")
@RefreshScope
public class UserFeign {

    private final IUserService userService;

    @GetMapping(value = "/query/by/account")
    @ApiOperation(value = "通过账号查询用户信息", notes = "通过账号查询用户信息")
    public Result<UserInfo> queryByAccount(String account) {
        User user = new User();
        user.setAccount(account);
        UserInfo userInfo = userService.queryUserInfo(user);
        return Result.data(userInfo);
    }
}
