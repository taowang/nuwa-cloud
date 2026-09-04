package com.study.nuwa.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import java.io.Serializable;

/**
 * <p>
 * 新增用户
 * </p>
 *
 * @author
 * @since 2019-05-26
 */
@Data
@Schema(description = "UserAddDTO对象")
public class UserAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Schema(description = "账号")
    @Length(max = 32, min = 2, message = "账号长度范围应该在2-32位之间。")
    private String account;
    
    @Schema(description = "昵称")
    @Length(max = 32, min = 2, message = "昵称长度范围应该在2-32位之间。")
    private String nickname;
    
    @Length(max = 8, min = 2, message = "姓名长度范围应该在2-8位之间。")
    @Schema(description = "用户姓名")
    private String realName;
    
    @Schema(description = "用户姓名")
    private String mobile;
    
    @Schema(description = "用户姓名")
    private String password;
    
    @Schema(description = "短信模板编码")
    private String smsCode;
    
    @Schema(description = "短信验证码")
    private String code;
    
    @Schema(description = "邮箱")
    @Email
    private String email;

    @Schema(description = "用户状态")
    private Integer status;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "1 : 男，0 : 女， -1: 未知")
    private String gender;
    
    @Schema(description = "街道详细地址")
    private String street;
    
    @Schema(description = "备注")
    private String comments;

    @Schema(description = "用户角色")
    private Long roleId;
    
    @Schema(description = "第三方用户id")
    private Long socialId;
}
