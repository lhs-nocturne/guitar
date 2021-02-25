package com.heb.guitar.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2020/7/13
 * Time: 18:38
 */
@Data
public class PermissionRespNodeVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "菜单权限名称")
    private String title;
    @ApiModelProperty(value = "接口地址")
    private String url;
    private List<?> children;
    @ApiModelProperty(value = "是否展开 默认不展开(false)")
    private boolean spread = false;
    @ApiModelProperty(value = "是否选中 默认false")
    private boolean checked;
}
