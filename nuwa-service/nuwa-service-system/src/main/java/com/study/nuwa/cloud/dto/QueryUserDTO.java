
package com.study.nuwa.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 用户查询
 * </p>
 *
 * @author gitegg
 * @since 2019-05-26
 */
@Data
@Schema(description = "QueryUser对象")
public class QueryUserDTO implements Serializable
{
    
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "1 : 男，0 : 女， 2: 未知")
    private String gender;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户状态 '0'禁用,'1' 启用, '2' 密码过期或初次未修改")
    private Integer status;

    @Schema(description = "头像图片地址")
    private String avatar;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String area;
    
    /**
     * vue级联选择
     */
    @Schema(description = "地址数组")
    private List<String> areas;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "开始时间")
    private String beginDateTime;

    @Schema(description = "结束时间")
    private String endDateTime;

    @Schema(description = "组织机构id")
    private Long organizationId;
    
    @Schema(description = "通过批量查询用户")
    private List<Long> userIds;
    
    @Schema(description = "角色id批量查询用户")
    private List<Long> roleIds;
    
    @Schema(description = "角色key批量查询用户")
    private List<String> roleKeys;
    
    @Schema(description = "机构id批量查询用户")
    private List<Long> organizationIds;
}
