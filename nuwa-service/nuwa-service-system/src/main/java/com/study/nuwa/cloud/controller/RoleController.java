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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "RoleController|角色相关的前端控制器")
@RefreshScope
public class RoleController {

    private final IRoleService roleService;

    private final IRoleResourceService roleResourceService;

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询角色列表")
    public PageResult<Role> list(Role role, Page<Role> page) {
        Page<Role> pageRole = roleService.selectRoleList(page, role);
        return PageResult.data(pageRole.getTotal(), pageRole.getRecords());
    }

    /**
     * 添加角色
     */
    @PostMapping("/create")
    @ApiOperation(value = "添加角色")
    public Result<?> create(@RequestBody CreateRoleDTO role) {
        boolean result = roleService.createRole(role);
        return Result.result(result);
    }

    /**
     * 修改角色
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新角色")
    public Result<?> update(@RequestBody UpdateRoleDTO role) {
        boolean result = roleService.updateRole(role);
        return Result.result(result);
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete/{roleId}")
    @ApiOperation(value = "删除角色")
    @ApiImplicitParam(paramType = "path", name = "roleId", value = "角色ID", required = true, dataType = "Long")
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
    @ApiOperation(value = "批量删除角色")
    @ApiImplicitParam(name = "roleIds", value = "角色ID列表", required = true, dataType = "List")
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
    @ApiOperation(value = "修改角色状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "roleStatus", value = "角色状态", required = true, dataType = "Integer",
            paramType = "path")})
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
    @ApiOperation(value = "获取角色的权限资源")
    @ApiImplicitParam(paramType = "path", name = "roleId", value = "角色ID", required = true, dataType = "Integer")
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
    @ApiOperation(value = "查询所有角色列表")
    public Result<List<Role>> queryAll() {
        List<Role> result = roleService.list();
        return Result.data(result);
    }

}
