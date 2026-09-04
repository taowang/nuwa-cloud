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
 * 用户表
 * </p>
 *
 * @author gitegg
 * @since 2019-10-24
 */
@Data
@TableName("t_sys_user")
@Schema(description ="User对象")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "租户id")
    @TableField(value = "tenant_id")
    private Long tenantId;

    @Schema(description = "账号")
    @TableField("account")
    private String account;

    @Schema(description = "昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "姓名")
    @TableField("real_name")
    private String realName;

    @Schema(description = "1 : 男，0 : 女")
    @TableField("gender")
    private String gender;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "电话")
    @TableField("mobile")
    private String mobile;

    @Schema(description = "密码")
    @TableField("password")
    private String password;

    @Schema(description = "'0'禁用,'1' 启用, '2' 密码过期或初次未修改")
    @TableField("status")
    private Integer status;

    @Schema(description = "头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "国家")
    @TableField("country")
    private String country;

    @Schema(description = "省")
    @TableField("province")
    private String province;

    @Schema(description = "市")
    @TableField("city")
    private String city;

    @Schema(description = "区")
    @TableField("area")
    private String area;

    @Schema(description = "街道详细地址")
    @TableField("street")
    private String street;

    @Schema(description = "备注")
    @TableField("comments")
    private String comments;

}
