package com.study.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.study.service.system.dto.QueryUserResourceDTO;
import com.study.service.system.entity.Resource;
import com.study.service.system.enums.ResourceEnum;
import com.study.service.system.mapper.ResourceMapper;
import com.study.service.system.service.IResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gitegg
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

    private final ResourceMapper resourceMapper;

    @Override
    public List<Resource> selectResourceList(QueryWrapper<Resource> wrapper) {
        List<Resource> list = this.list(wrapper);
        return list;
    }

    @Override
    public List<Resource> queryMenuTreeByUserId(Long userId) {
        QueryUserResourceDTO queryUserResourceDTO = new QueryUserResourceDTO();
        queryUserResourceDTO.setUserId(userId);
        queryUserResourceDTO.setResourceTypeList(Lists.newArrayList(ResourceEnum.MENU.getCode()));
        List<Resource> resourceList = resourceMapper.queryResourceByUserId(queryUserResourceDTO);
        Map<Long, Resource> resourceMap = new HashMap<>();
        List<Resource> menus = this.assembleResourceTree(resourceList, resourceMap);
        return menus;
    }

    @Override
    public List<Resource> queryResourceListByUserId(QueryUserResourceDTO queryUserResourceDTO) {
        List<Resource> resourceList = resourceMapper.queryResourceByUserId(queryUserResourceDTO);
        return resourceList;
    }

    @Override
    public List<Resource> queryResourceRoles() {
        //List<Resource> resourceList = resourceMapper.queryResourceRoleIds();
        List<Resource> resourceList = resourceMapper.queryResourceRoles();
        return resourceList;
    }

    /**
     * 组装子父级目录
     *
     * @param resourceList
     * @param resourceMap
     * @return
     */
    private List<Resource> assembleResourceTree(List<Resource> resourceList, Map<Long, Resource> resourceMap) {
        List<Resource> menus = new ArrayList<>();
        for (Resource resource : resourceList) {
            resourceMap.put(resource.getId(), resource);
        }
        for (Resource resource : resourceList) {
            Long treePId = resource.getParentId();
            Resource resourceTree = resourceMap.get(treePId);
            if (null != resourceTree && !resource.equals(resourceTree)) {
                List<Resource> nodes = resourceTree.getChildren();
                if (null == nodes) {
                    nodes = new ArrayList<>();
                    resourceTree.setChildren(nodes);
                }
                nodes.add(resource);
            } else {
                menus.add(resource);
            }
        }
        return menus;
    }
}
