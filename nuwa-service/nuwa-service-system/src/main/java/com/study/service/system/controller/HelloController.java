package com.study.service.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
}
