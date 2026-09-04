
package com.study.nuwa.cloud.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author gitegg
 */
@Data
@Schema(description = "用户信息")
public class UserInfo extends User {

    @Schema(description = "机构id")
    private Long organizationId;

    @Schema(description = "机构名称")
    private String organizationName;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "角色标识")
    private String roleKey;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "机构id集合")
    private String organizationIds;

    @Schema(description = "机构名称集合")
    private String organizationNames;

    @Schema(description = "角色id集合")
    private String roleIds;

    @Schema(description = "角色标识集合")
    private String roleKeys;

    @Schema(description = "角色名称集合")
    private String roleNames;

    @Schema(description = "数据权限")
    private String dataPermission;

    @Schema(description = "角色id列表")
    private List<String> roleIdList;

    @Schema(description = "角色key列表")
    private List<String> roleKeyList;

    @Schema(description = "机构id列表")
    private List<String> organizationIdList;

    @Schema(description = "机构名称列表")
    private List<String> organizationNameList;

    @Schema(description = "资源列表字符串")
    private List<String> resourceKeyList;

    @Schema(description = "前端展示的用户菜单树")
    private List<Resource> menuTree;

    @Schema(description = "角色数据权限类型列表字符串")
    private String dataPermissionTypes;

    @Schema(description = "角色数据权限类型列表")
    private List<String> dataPermissionTypeList;

    @Schema(description = "资源请求列表")
    private List<String> resourceUrlList;
}
