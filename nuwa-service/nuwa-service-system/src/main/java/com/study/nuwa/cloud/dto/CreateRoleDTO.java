
package com.study.nuwa.cloud.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色创建
 * </p>
 *
 * @author gitegg
 * @since 2019-05-19
 */
@Data
@ApiModel(value = "CreateRole对象", description = "创建角色时的对象")
public class CreateRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色标识")
    private String roleKey;

    @ApiModelProperty(value = "角色级别")
    private Integer roleLevel;

    @ApiModelProperty(value = "1有效，0禁用")
    private Integer roleStatus;

    @ApiModelProperty(value = "角色数据权限")
    @TableField("data_permission_type")
    private String dataPermissionType;

    @ApiModelProperty(value = "备注")
    private String comments;

}
