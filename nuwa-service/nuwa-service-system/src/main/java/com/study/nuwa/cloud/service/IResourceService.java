package com.study.nuwa.cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.nuwa.cloud.dto.QueryUserResourceDTO;
import com.study.nuwa.cloud.entity.Resource;

import java.util.List;

/**
 * @author gitegg
 */
public interface IResourceService extends IService<Resource> {

    /**
     * 查询资源权限列表
     * @param wrapper
     * @return
     */
    List<Resource> selectResourceList(QueryWrapper<Resource> wrapper);

    /**
     * 查询用户菜单
     * @param userId
     * @return
     */
    List<Resource> queryMenuTreeByUserId(Long userId);

    /**
     * 查询用户菜单列表
     * @param queryUserResourceDTO
     * @return
     */
    List<Resource> queryResourceListByUserId(QueryUserResourceDTO queryUserResourceDTO);

    /**
     * 查询资源权限列表
     * @param
     * @return
     */
    List<Resource> queryResourceRoles();

}
