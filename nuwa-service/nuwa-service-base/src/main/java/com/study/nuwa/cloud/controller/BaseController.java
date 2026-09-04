package com.study.nuwa.cloud.controller;

import com.study.nuwa.cloud.dto.ApiSystemDTO;
import com.study.nuwa.cloud.fegin.ISystemFeign;
import com.study.nuwa.platform.aspect.AvoidRepeatableCommit;
import com.study.platform.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "base")
@RequiredArgsConstructor()
@Tag(name = "nuwa-base")
@RefreshScope
public class BaseController {

    @Value("${server.port}")
    private Integer serverPort;

    private final ISystemFeign systemFeign;

    @GetMapping(value = "api/by/id")
    @Operation(summary = "Fegin Get调用测试接口")
    public Result<Object> feginById(@RequestParam("id") Long id) {
        return Result.data(systemFeign.querySystemById(id));
    }

    @PostMapping(value = "api/by/dto")
    @Operation(summary = "Fegin Post调用测试接口")
    public Result<Object> feginByDto(@Valid @RequestBody ApiSystemDTO systemDTO) {
        return Result.data(systemFeign.querySystemByDto(systemDTO));
    }

    @PostMapping(value = "api/ribbon")
    @Operation(summary = "Ribbon调用测试接口")
    public Result<Object> testRibbon() {
        return Result.data("现在访问的服务端口是:" + serverPort);
        //return Result.data(systemFeign.testRibbon());
    }

    @AvoidRepeatableCommit
    @Operation(summary = "添加文章")
    @PostMapping("api/add")
    public Result<Void> add() {
        System.err.println("添加文章" + "=======================Tony");
        return Result.success();
    }

}
