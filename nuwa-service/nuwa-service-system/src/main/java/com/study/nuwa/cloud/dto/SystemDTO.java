package com.study.nuwa.cloud.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class SystemDTO {

    @NotNull
    @Min(value = 10, message = "id必须大于10")
    @Max(value = 150, message = "id必须小于150")
    private Long id;

    @NotNull(message = "名称不能为空")
    @Size(min = 3, max = 20, message = "名称长度必须在3-20之间")
    private String name;
}
