package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysLogPageReqVO {
    @ApiModelProperty(value = "第几页")
    private int pageNum=1;
    @ApiModelProperty(value = "分页数量")
    private int pageSize=10;
    @ApiModelProperty(value = "用户操作动作")
    private String operation;
    @ApiModelProperty(value = "账号")
    private String username;
}
