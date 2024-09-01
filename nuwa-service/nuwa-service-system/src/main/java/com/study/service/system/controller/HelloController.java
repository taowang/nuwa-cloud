package com.study.service.system.controller;

import com.study.platform.base.result.Result;
import com.study.platform.cloud.aspect.AvoidRepeatableCommit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 * Created by macro on 2020/6/19.
 */
@Api(description = "测试接口", tags = "UserController")
@RequestMapping(value = "/system")
@RestController
public class HelloController {

    @ApiOperation("mgr")
    @GetMapping("/mgr")
    public String hello() {
        return "Hello World.";
    }

    @AvoidRepeatableCommit
    @PostMapping("/duplicateSubmit")
    @ApiOperation(value = "防止重复提交")
    public Result<String> duplicateSubmit() {
        return Result.data("Success");
    }
}
