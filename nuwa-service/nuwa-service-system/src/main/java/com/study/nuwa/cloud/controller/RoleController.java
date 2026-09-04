package com.study.nuwa.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.platform.result.PageResult;
import com.study.platform.result.Result;
import com.study.nuwa.cloud.dto.CreateRoleDTO;
import com.study.nuwa.cloud.dto.UpdateRoleDTO;
import com.study.nuwa.cloud.entity.Role;
import com.study.nuwa.cloud.entity.RoleResource;
import com.study.nuwa.cloud.service.IRoleResourceService;
import com.study.nuwa.cloud.service.IRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: RoleController
 * @Description: Role前端控制器
 * @author gitegg
 * @date 2018年5月18日 下午4:06:17
 */
@RestController
@RequestMapping(value = "role")
@RequiredArgsConstructor()
@Tag(name = "RoleController|角色相关的前端控制器")
@RefreshScope
public class RoleController {

    private final IRoleService roleService;

    private final IRoleResourceService roleResourceService;

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询角色列表")
    public PageResult<Role> list(Role role, Page<Role> page) {
        Page<Role> pageRole = roleService.selectRoleList(page, role);
        return PageResult.data(pageRole.getTotal(), pageRole.getRecords());
    }

    /**
     * 添加角色
     */
    @PostMapping("/create")
    @Operation(summary = "添加角色")
    public Result<?> create(@RequestBody CreateRoleDTO role) {
        boolean result = roleService.createRole(role);
        return Result.result(result);
    }

    /**
     * 修改角色
     */
    @PostMapping("/update")
    @Operation(summary = "更新角色")
    public Result<?> update(@RequestBody UpdateRoleDTO role) {
        boolean result = roleService.updateRole(role);
        return Result.result(result);
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete/{roleId}")
    @Operation(summary = "删除角色")
    @Parameter(name = "roleId", description = "角色ID", in = ParameterIn.PATH)
    public Result<?> delete(@PathVariable("roleId") Long roleId) {
        if (null == roleId) {
            return Result.error("ID不能为空");
        }
        boolean result = roleService.deleteRole(roleId);
        return Result.result(result);
    }

    /**
     * 批量删除角色
     */
    @PostMapping("/batch/delete")
    @Operation(summary = "批量删除角色")
    @Parameter(name = "roleIds", description = "角色ID列表")
    public Result<?> batchDelete(@RequestBody List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Result.error("角色ID列表不能为空");
        }
        boolean result = roleService.batchDeleteRole(roleIds);
        return Result.result(result);
    }

    /**
     * 修改角色状态
     */
    @PostMapping("/status/{roleId}/{roleStatus}")
    @Operation(summary = "修改角色状态")
    @Parameters({
        @Parameter(name = "roleId", description = "角色ID", in = ParameterIn.PATH),
        @Parameter(name = "roleStatus", description = "角色状态", in = ParameterIn.PATH)})
    public Result<?> updateStatus(@PathVariable("roleId") Long roleId,
        @PathVariable("roleStatus") Integer roleStatus) {
        if (null == roleId || StringUtils.isEmpty(roleStatus)) {
            return Result.error("ID和状态不能为空");
        }
        UpdateRoleDTO role = new UpdateRoleDTO();
        role.setId(roleId);
        role.setRoleStatus(roleStatus);
        boolean result = roleService.updateRole(role);
        return Result.result(result);
    }

    /**
     * 获取角色资源
     * 
     * @param roleId
     * @return
     */
    @GetMapping(value = "/resource/{roleId}")
    @Operation(summary = "获取角色的权限资源")
    @Parameter(name = "roleId", description = "角色ID", in = ParameterIn.PATH)
    public Result<List<RoleResource>> queryRoleResource(@PathVariable("roleId") Integer roleId) {
        LambdaQueryWrapper<RoleResource> ew = new LambdaQueryWrapper<>();
        ew.eq(RoleResource::getRoleId, roleId);
        List<RoleResource> list = roleResourceService.list(ew);
        return Result.data(list);
    }

    /**
     * 查询所有角色列表
     * 
     * @return
     */
    @GetMapping(value = "/all")
    @Operation(summary = "查询所有角色列表")
    public Result<List<Role>> queryAll() {
        List<Role> result = roleService.list();
        return Result.data(result);
    }

}
