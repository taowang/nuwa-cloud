package com.study.service.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.service.system.dto.UpdateRoleResourceDTO;
import com.study.service.system.entity.Resource;
import com.study.service.system.entity.Role;
import com.study.service.system.entity.RoleResource;

import java.util.List;

/**
 * @ClassName: IRoleService
 * @Description: 角色相关操作接口
 * @author gitegg
 * @date
 */
public interface IRoleResourceService extends IService<RoleResource> {

    /**
     * 初始化系统角色权限
     */
    void initResourceRoles();

    /**
     * 批量删除资源权限的角色缓存
     * @param roles
     */
    void removeBatchRoles(List<Role> roles);

    /**
     * 新增或删除角色权限
     * @param roleResourceList
     * @param addFlag
     */
    void genRoleResources(List<RoleResource> roleResourceList, boolean addFlag);
}
