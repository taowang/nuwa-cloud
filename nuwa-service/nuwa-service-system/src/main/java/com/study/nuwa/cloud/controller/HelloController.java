package com.study.nuwa.cloud.controller;

import com.study.platform.result.Result;
import com.study.nuwa.platform.aspect.AvoidRepeatableCommit;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 * Created by macro on 2020/6/19.
 */
@Tag(name = "default", description = "测试接口")
@RequestMapping(value = "/system")
@RestController
public class HelloController {

    @Operation(summary = "mgr")
    @GetMapping("/mgr")
    public String hello() {
        return "Hello World.";
    }

    @AvoidRepeatableCommit
    @PostMapping("/duplicateSubmit")
    @Operation(summary = "防止重复提交")
    public Result<String> duplicateSubmit() {
        return Result.data("Success");
    }
}
