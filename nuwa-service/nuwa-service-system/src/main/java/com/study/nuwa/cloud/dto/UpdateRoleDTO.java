
package com.study.nuwa.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色更新
 * </p>
 *
 * @author gitegg
 * @since 2019-05-19
 */
@Data
@Schema(description = "UpdateRole对象")
public class UpdateRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "父id")
    private Long parentId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识")
    private String roleKey;

    @Schema(description = "角色级别")
    private Integer roleLevel;

    @Schema(description = "1有效，0禁用")
    private Integer roleStatus;

    @Schema(description = "角色数据权限")
    private String dataPermissionType;

    @Schema(description = "备注")
    private String comments;

}
