package com.study.service.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.service.system.dto.DataPermissionUserDTO;
import com.study.service.system.dto.QueryDataPermissionUserDTO;
import com.study.service.system.dto.QueryUserDTO;
import com.study.service.system.entity.DataPermissionUser;
import com.study.service.system.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author GitEgg
 * @since 2021-05-13
 */
public interface DataPermissionUserMapper extends BaseMapper<DataPermissionUser> {

    /**
    * 查询列表
    * @param page
    * @param dataPermissionUserDTO
    * @return
    */
    Page<DataPermissionUserDTO> queryDataPermissionUserList(Page<DataPermissionUserDTO> page, @Param("dataPermissionUser") QueryDataPermissionUserDTO dataPermissionUserDTO);

    /**
    * 查询信息
    * @param dataPermissionUserDTO
    * @return
    */
    DataPermissionUserDTO queryDataPermissionUser(@Param("dataPermissionUser") QueryDataPermissionUserDTO dataPermissionUserDTO);


    /**
     * 分页查询机构权限下的用户列表
     * @param page
     * @param user
     * @return
     */
    Page<UserInfo> selectOrganizationUserList(Page<UserInfo> page, @Param("user") QueryUserDTO user);
}
