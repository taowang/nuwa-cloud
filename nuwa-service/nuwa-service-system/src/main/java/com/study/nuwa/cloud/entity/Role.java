package com.study.nuwa.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.study.nuwa.platform.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author gitegg
 * @since 2019-10-24
 */
@Data
@TableName("t_sys_role")
@Schema(description ="Role对象")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "父id")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;

    @Schema(description = "角色标识")
    @TableField("role_key")
    private String roleKey;

    @Schema(description = "角色级别")
    @TableField("role_level")
    private Integer roleLevel;

    @Schema(description = "1有效，0禁用")
    @TableField("role_status")
    private Integer roleStatus;

    @Schema(description = "角色数据权限")
    @TableField("data_permission_type")
    private String dataPermissionType;

    @Schema(description = "备注")
    @TableField("comments")
    private String comments;

}
