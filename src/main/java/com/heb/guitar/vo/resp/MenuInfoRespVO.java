package com.heb.guitar.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/1/31
 * Time: 11:32
 */
@Data
public class MenuInfoRespVO {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "title")
    private String title;
    @ApiModelProperty(value = "icon")
    private String icon;
    @ApiModelProperty(value = "href")
    private String href;
    @ApiModelProperty(value = "target")
    private String target;
    private List<?> child;

}
