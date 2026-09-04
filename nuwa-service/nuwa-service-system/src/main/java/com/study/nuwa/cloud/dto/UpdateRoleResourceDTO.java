package com.study.nuwa.cloud.dto;


import com.study.nuwa.cloud.entity.RoleResource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gitegg
 */
@Data
@Schema(description = "UpdateRoleResource对象")
public class UpdateRoleResourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "需要操作的角色id")
    private Long roleId;

    @Schema(description = "添加的资源列表")
    private List<RoleResource> addResources;

    @Schema(description = "删除的资源列表")
    private List<RoleResource> delResources;

}
