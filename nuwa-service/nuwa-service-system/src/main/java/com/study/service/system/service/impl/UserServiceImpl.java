package com.study.service.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.platform.base.constant.AuthConstant;
import com.study.platform.base.constant.NuwaConstant;
import com.study.platform.base.enums.ResultCode;
import com.study.platform.base.exception.BusinessException;
import com.study.platform.base.util.BeanCopierUtils;
import com.study.service.system.dto.CreateUserDTO;
import com.study.service.system.dto.QueryUserResourceDTO;
import com.study.service.system.entity.*;
import com.study.service.system.enums.ResourceEnum;
import com.study.service.system.mapper.UserMapper;
import com.study.service.system.service.IOrganizationUserService;
import com.study.service.system.service.IResourceService;
import com.study.service.system.service.IUserRoleService;
import com.study.service.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gitegg
 * @ClassName: UserServiceImpl
 * @Description: 用户相关操作接口实现类
 * @date 2018年5月18日 下午3:20:30
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Value("${system.defaultRoleId:2}")
    private Long defaultRoleId;
    @Value("${system.defaultUserStatus:1}")
    private int defaultUserStatus;
    @Value("${system.defaultPwd:123456")
    private String defaultPwd;
    @Value("${system.defaultPwdChangeFirst:no}")
    private boolean defaultPwdChangeFirst;
    @Value("${system.defaultOrgId:1}")
    private Long defaultOrgId;

    private final UserMapper userMapper;

    private final IUserRoleService userRoleService;

    private final IResourceService resourceService;

    private final IOrganizationUserService organizationUserService;

    private final DataPermissionUserServiceImpl dataPermissionUserService;

    @Override
    public UserInfo queryUserInfo(User user) {

        UserInfo userInfo = userMapper.queryUserInfo(user);
        if (null == userInfo) {
            throw new BusinessException(ResultCode.INVALID_USERNAME.getMessage());
        }
        String roleIds = userInfo.getRoleIds();
        String roleKeys = userInfo.getRoleKeys();
        //组装角色ID列表，用于Oatuh2和Gateway鉴权
        if (!StringUtils.isEmpty(roleIds)) {
            String[] roleIdsArray = roleIds.split(StrUtil.COMMA);
            userInfo.setRoleIdList(Arrays.asList(roleIdsArray));
        }

        //组装角色key列表，用于前端页面鉴权
        if (!StringUtils.isEmpty(roleKeys)) {
            String[] roleKeysArray = roleKeys.split(StrUtil.COMMA);
            userInfo.setRoleKeyList(Arrays.asList(roleKeysArray));
        }

        String dataPermissionTypes = userInfo.getDataPermissionTypes();
        // 获取用户的角色数据权限级别
        if (!StringUtils.isEmpty(dataPermissionTypes)) {
            String[] dataPermissionTypeArray = dataPermissionTypes.split(StrUtil.COMMA);
            userInfo.setDataPermissionTypeList(Arrays.asList(dataPermissionTypeArray));
        }

        String organizationIds = userInfo.getOrganizationIds();
        // 获取用户机构数据权限列表
        if (!StringUtils.isEmpty(organizationIds)) {
            String[] organizationIdArray = organizationIds.split(StrUtil.COMMA);
            userInfo.setOrganizationIdList(Arrays.asList(organizationIdArray));
        }

        QueryUserResourceDTO queryUserResourceDTO = new QueryUserResourceDTO();
        queryUserResourceDTO.setUserId(userInfo.getId());
        List<Resource> resourceList = resourceService.queryResourceListByUserId(queryUserResourceDTO);

        // 查询用户权限列表key，用于前端页面鉴权
        List<String> menuList = resourceList.stream().map(Resource::getResourceKey).collect(Collectors.toList());
        userInfo.setResourceKeyList(menuList);

        // 查询用户资源列表，用于SpringSecurity鉴权
        List<String> resourceUrlList = resourceList.stream().filter(s -> !ResourceEnum.MODULE.getCode().equals(s.getResourceType()) && !ResourceEnum.MENU.getCode().equals(s.getResourceType())).map(Resource::getResourceUrl).collect(Collectors.toList());
        userInfo.setResourceUrlList(resourceUrlList);

        // 查询用户菜单的列表，用于前端页面鉴权
        List<Resource> menuTree = resourceService.queryMenuTreeByUserId(userInfo.getId());
        userInfo.setMenuTree(menuTree);
        return userInfo;
    }

    @Override
    public CreateUserDTO createUser(CreateUserDTO user) {
        User userEntity = BeanCopierUtils.copyByClass(user, User.class);
        // 查询已存在的用户，用户名、昵称、邮箱、手机号有任一重复即视为用户已存在，真实姓名是可以重复的。
        List<User> userList = userMapper.queryExistUser(userEntity);
        if (!CollectionUtils.isEmpty(userList)) {
            throw new BusinessException("账号已经存在");
        }

        // 如果为空时设置默认角色
        Long roleId = user.getRoleId();
        List<Long> roleIds = user.getRoleIds();
        if (null == roleId && CollectionUtils.isEmpty(roleIds)) {
            // 默认值，改成配置
            roleId = defaultRoleId;
        }

        // 设置默认状态
        if (null == userEntity.getStatus()) {
            userEntity.setStatus(defaultUserStatus);
        }

        // 处理前端传过来的省市区
        userEntity = resolveAreas(userEntity, user.getAreas());

        String pwd = userEntity.getPassword();
        if (StringUtils.isEmpty(pwd)) {
            // 默认密码，配置文件配置
            pwd = defaultPwd;
            // 初次登录需要修改密码
            if (defaultPwdChangeFirst) {
                userEntity.setStatus(NuwaConstant.UserStatus.NOT_ACTIVE);
            }
        }
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String cryptPwd = passwordEncoder.encode(pwd);
        userEntity.setPassword(cryptPwd);
        // 保存用户
        boolean result = this.save(userEntity);
        if (result) {
            //保存用户和组织机构的关系
            Long organizationId = null == user.getOrganizationId() ? defaultOrgId : user.getOrganizationId();
            OrganizationUser organizationUser = new OrganizationUser();
            organizationUser.setUserId(userEntity.getId());
            organizationUser.setOrganizationId(organizationId);
            organizationUserService.save(organizationUser);

            //默认增加用户所在机构数据权限值，但是否有操作权限还是会根据资源权限判断
            DataPermissionUser dataPermission = new DataPermissionUser();
            dataPermission.setUserId(userEntity.getId());
            dataPermission.setOrganizationId(organizationId);
            dataPermissionUserService.save(dataPermission);

            //保存用户多个角色信息
            List<UserRole> userRoleList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(roleIds)) {
                for (Long role : roleIds) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userEntity.getId());
                    userRole.setRoleId(role);
                    userRoleList.add(userRole);
                }
                userRoleService.saveBatch(userRoleList);
            }

            //保存用户单个角色信息
            if (null != roleId) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userEntity.getId());
                userRole.setRoleId(roleId);
                userRoleService.save(userRole);
            }
            user.setId(userEntity.getId());
        }
        return user;
    }

    /**
     * 处理省市区数据
     *
     * @param userEntity
     * @param areas
     * @return
     */
    private User resolveAreas(User userEntity, List<String> areas) {
        if (!CollectionUtils.isEmpty(areas)) {
            userEntity.setProvince(areas.get(NuwaConstant.Address.PROVINCE));
            userEntity.setCity(areas.get(NuwaConstant.Address.CITY));
            userEntity.setArea(areas.get(NuwaConstant.Address.AREA));
        }
        return userEntity;
    }

    @Override
    public boolean checkUserExist(User userEntity) {
        List<User> userList = userMapper.queryExistUser(userEntity);
        return !CollectionUtils.isEmpty(userList) ? true : false;
    }
}
