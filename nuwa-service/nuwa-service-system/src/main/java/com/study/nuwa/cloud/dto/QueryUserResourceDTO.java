
package com.study.nuwa.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 查询用户权限
 * </p>
 *
 * @author gitegg
 * @since 2019-05-26
 */
@Data
@Schema(description = "QueryUserResourceDTO")
public class QueryUserResourceDTO implements Serializable
{
    
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "资源权限类型")
    private List<String> resourceTypeList;

}
