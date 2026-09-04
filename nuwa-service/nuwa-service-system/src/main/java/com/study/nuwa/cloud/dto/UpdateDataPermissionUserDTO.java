package com.study.nuwa.cloud.dto;

import com.study.nuwa.platform.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author GitEgg
 * @since 2021-05-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description ="DataPermissionUser对象")
public class UpdateDataPermissionUserDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "机构id")
    private Long organizationId;

    @Schema(description = "状态 0禁用，1 启用,")
    private Integer status;

    @Schema(description = "需要添加的机构权限")
    private List<Long> addDataPermissions;

    @Schema(description = "需要删除的机构权限")
    private List<Long> removeDataPermissions;


}
