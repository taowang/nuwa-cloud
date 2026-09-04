
package com.study.nuwa.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 用户创建
 * </p>
 *
 * @author gitegg
 * @since 2019-05-26
 */
@Data
@Schema(description = "CreateUser对象")
public class CreateUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "账号")
    @NotBlank(message="账号不能为空")
    @Pattern(regexp = "^[a-z0-9_-]{3,16}$", message="账号格式不正确")
    private String account;

    @Schema(description = "昵称")
    @Size(min=2,max=16,message="昵称长度不正确")
    private String nickname;

    @Schema(description = "姓名")
    @Size(min=2,max=16,message="姓名长度不正确")
    private String realName;

    @Schema(description = "1 : 男，0 : 女， 2: 未知")
    private String gender;

    @Schema(description = "邮箱")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "手机号码")
    @NotBlank(message="手机号码不能为空")
    @Size(min=11,max=11,message="手机号码长度不正确")
    private String mobile;

    @Schema(description = "密码")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,18}$", message="密码格式不正确")
    private String password;

    @Schema(description = "用户状态 '0'禁用,'1' 启用, '2' 密码过期或初次未修改")
    private Integer status;

    @Schema(description = "头像图片地址")
    @Pattern(regexp = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$", message="头像图片地址格式不正确")
    private String avatar;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String area;

    @Schema(description = "用户地区ID数组")
    private List<String> areas;

    @Schema(description = "街道详细地址")
    private String street;

    @Schema(description = "备注")
    @Size(min=0,max=255,message="备注信息长度不正确")
    private String comments;

    @Schema(description = "角色id（单角色时）")
    private Long roleId;

    @Schema(description = "组织机构id")
    private Long organizationId;

    @Schema(description = "用户角色id数组（多角色时）")
    private List<Long> roleIds;
}
