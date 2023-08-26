package com.study.service.base.controller;

import com.study.platform.base.result.Result;
import com.study.service.system.api.dto.ApiSystemDTO;
import com.study.service.system.api.feign.ISystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "base")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = "nuwa-base")
@RefreshScope
public class BaseController {

    private final ISystemFeign systemFeign;

    @GetMapping(value = "api/by/id")
    @ApiOperation(value = "Fegin Get调用测试接口")
    public Result<Object> feginById(@RequestParam("id") Long id) {
        return Result.data(systemFeign.querySystemById(id));
    }

    @PostMapping(value = "api/by/dto")
    @ApiOperation(value = "Fegin Post调用测试接口")
    public Result<Object> feginByDto(@Valid @RequestBody ApiSystemDTO systemDTO) {
        return Result.data(systemFeign.querySystemByDto(systemDTO));
    }

    @PostMapping(value = "api/ribbon")
    @ApiOperation(value = "Ribbon调用测试接口")
    public Result<Object> testRibbon() {
        return Result.data(systemFeign.testRibbon());
    }

}
