package com.study.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.service.system.entity.Role;
import com.study.service.system.entity.UserRole;
import com.study.service.system.mapper.UserRoleMapper;
import com.study.service.system.service.IRoleService;
import com.study.service.system.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RoleServiceImpl
 * @Description: 角色相关操作接口实现类
 * @author gitegg
 * @date 2018年5月18日 下午3:22:21
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    private final IRoleService roleService;

    @Override
    public UserRole selectByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> ew = new LambdaQueryWrapper<>();
        ew.eq(UserRole::getUserId, userId);
        return this.getOne(ew);
    }

    @Override
    public List<Role> queryRolesByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> ew = new LambdaQueryWrapper<>();
        ew.eq(UserRole::getUserId, userId);
        List<UserRole> userRoleList = this.list(ew);
        if (!CollectionUtils.isEmpty(userRoleList)) {
            List<Long> roleIds = new ArrayList<>();
            for (UserRole userRole : userRoleList) {
                roleIds.add(userRole.getRoleId());
            }
            List<Role> roleList = roleService.listByIds(roleIds);
            return roleList;
        }
        return null;
    }
}
