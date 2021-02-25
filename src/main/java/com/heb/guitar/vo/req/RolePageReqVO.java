package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RolePageReqVO {
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "状态(1:正常0:弃用)")
    private Integer status;
    @ApiModelProperty(value = "第几页")
    private int pageNum=1;
    @ApiModelProperty(value = "分页数量")
    private int pageSize=10;
}
