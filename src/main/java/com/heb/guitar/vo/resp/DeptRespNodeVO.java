package com.heb.guitar.vo.resp;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class DeptRespNodeVO {

    @ApiModelProperty(value = "部门id")
    private String id;
    @ApiModelProperty(value = "部门编码")
    private String deptNo;
    @ApiModelProperty(value = "部门名称")
    private String title;
    @ApiModelProperty(value = "部门父级id")
    private String pid;
    @ApiModelProperty(value = "部门状态")
    private Integer status;
    @ApiModelProperty(value = "部门关系id")
    private String relationCode;
    @ApiModelProperty(value = "是否展开 默认不展开(false)")
    private boolean spread;
    @ApiModelProperty(value = "子集")
    private List<?> children;

}
