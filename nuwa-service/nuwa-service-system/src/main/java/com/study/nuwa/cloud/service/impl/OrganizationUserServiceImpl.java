package com.study.nuwa.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.nuwa.cloud.entity.OrganizationUser;
import com.study.nuwa.cloud.service.IOrganizationUserService;
import com.study.nuwa.cloud.mapper.OrganizationUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织机构和用户关联关系服务实现类
 * </p>
 *
 * @author gitegg
 * @since 2019-05-19
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrganizationUserServiceImpl extends ServiceImpl<OrganizationUserMapper, OrganizationUser>
        implements IOrganizationUserService {
}
