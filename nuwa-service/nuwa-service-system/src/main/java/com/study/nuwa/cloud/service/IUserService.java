package com.study.nuwa.cloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.nuwa.cloud.dto.CreateUserDTO;
import com.study.nuwa.cloud.entity.User;
import com.study.nuwa.cloud.entity.UserInfo;

/**
 * @ClassName: IUserService
 * @Description: 用户相关操接口
 * @author gitegg
 * @date
 */
public interface IUserService extends IService<User> {

    /**
     * 查询用户详细信息
     * @param user
     * @return
     */
    UserInfo queryUserInfo(User user);

    /**
     * 创建用户，成功后将id和对象返回
     * @param createUser
     * @return
     */
    CreateUserDTO createUser(CreateUserDTO createUser);

    /**
     * 校验用户是否存在
     * @param userEntity
     * @return
     */
    boolean checkUserExist(User userEntity);
}
