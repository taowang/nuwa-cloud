package com.study.nuwa.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.nuwa.cloud.entity.Role;
import com.study.nuwa.cloud.entity.RoleResource;
import com.study.nuwa.cloud.entity.UserRole;
import com.study.nuwa.cloud.mapper.RoleMapper;
import com.study.nuwa.cloud.service.IRoleResourceService;
import com.study.nuwa.cloud.service.IUserRoleService;
import com.study.platform.exception.BusinessException;
import com.study.platform.util.BeanCopierUtils;
import com.study.nuwa.cloud.dto.CreateRoleDTO;
import com.study.nuwa.cloud.dto.UpdateRoleDTO;
import com.study.nuwa.cloud.service.IRoleService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: RoleServiceImpl
 * @Description: 角色相关操作接口实现类
 * 这里使用Lombok setter注入方式，使用构造器注入IUserRoleService和IRoleService有循环依赖问题
 * @author gitegg
 * @date 2019年5月18日 下午3:22:21
 */
@Slf4j
@Service
@Setter(onMethod_ = {@Autowired})
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private RoleMapper roleMapper;

    private IUserRoleService userRoleService;

    @Lazy
    private IRoleResourceService roleResourceService;

    @Override
    public Page<Role> selectRoleList(Page<Role> page, Role role) {
        Page<Role> roleInfoList = roleMapper.selectRoleList(page, role);
        return roleInfoList;
    }

    @Override
    public boolean createRole(CreateRoleDTO role) {
        LambdaQueryWrapper<Role> ew = new LambdaQueryWrapper<>();
        ew.eq(Role::getRoleName, role.getRoleName()).or().eq(Role::getRoleKey, role.getRoleKey()).or()
                .eq(Role::getRoleName, role.getRoleKey()).or().eq(Role::getRoleKey, role.getRoleName());
        List<Role> roleList = list(ew);
        if (!CollectionUtils.isEmpty(roleList)) {
            throw new BusinessException("角色名称或角色标识已经存在");
        }
        Role roleEntity = BeanCopierUtils.copyByClass(role, Role.class);
        roleEntity.setUpdateTime(LocalDateTime.now());
        roleEntity.setCreateTime(LocalDateTime.now());
        roleEntity.setCreator(1L);
        roleEntity.setOperator(1L);
        boolean result = save(roleEntity);
        return result;
    }

    @Override
    public boolean updateRole(UpdateRoleDTO role) {
        LambdaQueryWrapper<Role> ew = new LambdaQueryWrapper<>();
        ew.ne(Role::getId, role.getId()).and(e -> ew.eq(Role::getRoleName, role.getRoleName()).or().eq(Role::getRoleKey, role.getRoleKey()).or()
                .eq(Role::getRoleName, role.getRoleKey()).or().eq(Role::getRoleKey, role.getRoleName()));
        List<Role> roleList = list(ew);
        if (!CollectionUtils.isEmpty(roleList)) {
            throw new BusinessException("角色名称或角色标识已经存在");
        }

        Role roleEntity = BeanCopierUtils.copyByClass(role, Role.class);
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("id", roleEntity.getId());
        boolean result = update(roleEntity, wrapper);
        return result;
    }

    @Override
    public boolean deleteRole(Long roleId) {
        Role role = this.getById(roleId);
        boolean result = removeById(roleId);
        if (result) {
            // 删除用户和角色的关联关系
            LambdaQueryWrapper<UserRole> wr = new LambdaQueryWrapper<>();
            wr.eq(UserRole::getRoleId, roleId);
            userRoleService.removeById(wr);
            // 删除角色和权限的关联关系
            LambdaQueryWrapper<RoleResource> rr = new LambdaQueryWrapper<>();
            rr.eq(RoleResource::getRoleId, roleId);
            roleResourceService.remove(rr);
            //重新初始化角色和权限的对应关系
            roleResourceService.removeBatchRoles(Arrays.asList(role));
        }
        return result;
    }

    @Override
    public boolean batchDeleteRole(List<Long> roleIds) {
        List<Role> roles = this.listByIds(roleIds);
        boolean result = removeByIds(roleIds);
        if (result) {
            // 删除用户和角色的关联关系
            LambdaQueryWrapper<UserRole> wrs = new LambdaQueryWrapper<>();
            wrs.in(UserRole::getRoleId, roleIds);
            userRoleService.remove(wrs);
            // 删除角色和权限的关联关系
            LambdaQueryWrapper<RoleResource> rrs = new LambdaQueryWrapper<>();
            rrs.in(RoleResource::getRoleId, roleIds);
            roleResourceService.remove(rrs);
            //重新初始化角色和权限的对应关系
            roleResourceService.initResourceRoles();
            roleResourceService.removeBatchRoles(roles);
        }
        return result;
    }
}
