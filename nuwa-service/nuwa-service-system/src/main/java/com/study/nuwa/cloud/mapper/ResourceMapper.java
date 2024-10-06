package com.study.nuwa.cloud.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.nuwa.cloud.dto.QueryUserResourceDTO;
import com.study.nuwa.cloud.entity.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author gitegg
 * @since 2018-05-19
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 查询用户权限资源
     * @param queryUserResourceDTO
     * @return
     */
    List<Resource> queryResourceByUserId(@Param("userResource") QueryUserResourceDTO queryUserResourceDTO);

    /**
     * queryResourceTreeProc
     * 
     * @Title: queryResourceTreeProc
     * @Description: 查询登陆用户的许可权限(使用存储过程递归查询所有权限树信息)
     * @param parentId
     * @return List<Resource>
     */
    List<Resource> queryResourceTreeProc(Long parentId);

    /**
     * 查询拥有权限资源的角色 使用@InterceptorIgnore注解
     * 
     * @param
     * @return
     */
    @InterceptorIgnore
    List<Resource> queryResourceRoleIds();

    /**
     * 查询拥有权限资源的角色 使用@InterceptorIgnore注解
     *
     * @param
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Resource> queryResourceRoles();
}
