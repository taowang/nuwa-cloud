package com.study.nuwa.cloud.fegin;

import com.study.nuwa.cloud.dto.ApiSystemDTO;
import com.study.platform.result.Result;
import com.study.nuwa.cloud.fegin.fallback.SystemApiFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "nuwa-service-system", contextId = "SystemClient", fallback = SystemApiFeignFallback.class)
public interface ISystemFeign {

    /**
     * OpenFeign测试Get
     *
     * @param id
     * @return
     */
    @GetMapping("/system/api/by/id")
    Result<Object> querySystemById(@RequestParam("id") Long id);

    /**
     * OpenFeign测试Post
     *
     * @param apiSystemDTO
     * @return ApiSystemDTO
     */
    @PostMapping("/system/api/by/dto")
    Result<ApiSystemDTO> querySystemByDto(@RequestBody ApiSystemDTO apiSystemDTO);

    /**
     * OpenFeign测试Ribbon负载均衡功能
     *
     * @return
     */
    @GetMapping("/system/api/ribbon")
    Result<String> testRibbon();
}
