package com.study.nuwa.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.study.nuwa.platform.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据权限配置表
 * </p>
 *
 * @author GitEgg
 * @since 2021-05-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_data_permission_role")
@Schema(description ="DataPermissionRole对象")
public class DataPermissionRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "功能权限id")
    private Long resourceId;

    @Schema(description = "数据权限名称")
    private String dataName;

    @Schema(description = "数据权限对应的mapper方法全路径")
    private String dataMapperFunction;

    @Schema(description = "需要做数据权限主表")
    private String dataTableName;

    @Schema(description = "需要做数据权限表的别名")
    private String dataTableAlias;

    @Schema(description = "数据权限需要排除的字段")
    private String dataColumnExclude;

    @Schema(description = "数据权限需要保留的字段")
    private String dataColumnInclude;

    @Schema(description = "数据权限表,默认t_sys_organization")
    private String innerTableName;

    @Schema(description = "数据权限表的别名,默认organization")
    private String innerTableAlias;

    @Schema(description = "数据权限类型:1只能查看本人 2只能查看本部门 3只能查看本部门及子部门 4可以查看所有数据")
    private String dataPermissionType;

    @Schema(description = "自定义数据权限类型")
    private String customExpression;

    @Schema(description = "状态 0禁用，1 启用,")
    private Integer status;

    @Schema(description = "备注")
    private String comments;


}
