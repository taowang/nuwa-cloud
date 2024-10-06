package com.study.nuwa.cloud.fegin.fallback;

import com.study.nuwa.cloud.dto.ApiSystemDTO;
import com.study.nuwa.cloud.dto.UserAddDTO;
import com.study.nuwa.cloud.fegin.ISystemFeign;
import com.study.nuwa.cloud.fegin.IUserFeign;
import com.study.platform.enums.ResultCode;
import com.study.platform.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SystemApiFeignFallback implements ISystemFeign, IUserFeign {
    @Override
    public Result<Object> querySystemById(Long id) {
        return null;
    }

    @Override
    public Result<ApiSystemDTO> querySystemByDto(ApiSystemDTO apiSystemDTO) {
        return null;
    }

    @Override
    public Result<String> testRibbon() {
        return null;
    }

    @Override
    public Result<Object> queryById(Long id) {
        return null;
    }

    @Override
    public Result<Object> queryByIds(List<Long> ids) {
        return null;
    }

    @Override
    public Result<Object> queryByOrganizationId(Long id) {
        return null;
    }

    @Override
    public Result<Object> queryByOrganizationIds(List<Long> ids) {
        return null;
    }

    @Override
    public Result<Object> queryUserByPhone(String phoneNumber) {
        return null;
    }

    @Override
    public Result<Object> queryUserByAccount(String account) {
        log.error("System Api Server Fallback..........");
        return Result.error(ResultCode.SERVER_FALLBACK.getCode(), ResultCode.SERVER_FALLBACK.getMessage());
    }

    @Override
    public Result<Object> queryUserByOpenId(String openid) {
        return null;
    }

    @Override
    public Result<Object> userAdd(UserAddDTO userAddDTO) {
        return null;
    }
}
