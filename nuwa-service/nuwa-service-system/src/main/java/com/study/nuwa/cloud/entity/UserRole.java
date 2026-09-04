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
 * 用户和角色关联表
 * </p>
 *
 * @author gitegg
 * @since 2019-10-24
 */
@Data
@TableName("t_sys_user_role")
@Schema(description ="UserRole对象")
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "角色id")
    @TableField("role_id")
    private Long roleId;
}
