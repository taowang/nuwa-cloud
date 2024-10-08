package com.study.nuwa.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.study.nuwa.platform.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author gitegg
 * @since 2019-10-24
 */
@Data
@TableName("t_sys_resource")
@ApiModel(value = "Resource对象", description = "权限表")
public class Resource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "资源名称")
    @TableField("resource_name")
    private String resourceName;

    @ApiModelProperty(value = "父id")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "资源标识")
    @TableField("resource_key")
    private String resourceKey;

    @ApiModelProperty(value = "资源类型：1、模块 2、菜单 3、按钮 4、链接")
    @TableField("resource_type")
    private String resourceType;

    @ApiModelProperty(value = "资源图标")
    @TableField("resource_icon")
    private String resourceIcon;

    @ApiModelProperty(value = "资源路径")
    @TableField("resource_path")
    private String resourcePath;

    @ApiModelProperty(value = "资源链接")
    @TableField("resource_url")
    private String resourceUrl;

    @ApiModelProperty(value = "资源级别")
    @TableField("resource_level")
    private Integer resourceLevel;

    @ApiModelProperty(value = "是否显示")
    @TableField("resource_show")
    private Boolean resourceShow;

    @ApiModelProperty(value = "是否缓存")
    @TableField("resource_cache")
    private Boolean resourceCache;

    @ApiModelProperty(value = "页面name")
    @TableField("resource_page_name")
    private String resourcePageName;

    @ApiModelProperty(value = "1有效，0禁用")
    @TableField("resource_status")
    private Integer resourceStatus;

    @ApiModelProperty(value = "备注")
    @TableField("comments")
    private String comments;

    /**
     * 子菜单，必须初始化否则vue新增不展示树子菜单
     */
    @TableField(exist = false)
    private List<Resource> children = new ArrayList<>();

    /**
     * 拥有资源权限角色id集合
     */
    @TableField(exist = false)
    private List<Long> roleIds;

    /**
     * 拥有资源权限角色集合
     */
    @TableField(exist = false)
    private List<Role> roles;

}
