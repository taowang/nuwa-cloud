package com.study.service.system.component;

import com.study.service.system.service.IDataPermissionRoleService;
import com.study.service.system.service.IRoleResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 容器启动完成加载资源权限数据到缓存
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
public class InitSystemCacheRunner implements CommandLineRunner {

    private final IRoleResourceService roleResourceService;

    private final IDataPermissionRoleService dataPermissionRoleService;

    @Override
    public void run(String... args) {

        log.info("InitResourceRolesCacheRunner running");

        // 初始化系统权限和角色的关系缓存
        roleResourceService.initResourceRoles();

        // 初始化数据权限缓存
        //dataPermissionRoleService.initDataRolePermissions();

    }
}

