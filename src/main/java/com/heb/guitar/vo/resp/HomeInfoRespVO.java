package com.heb.guitar.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 注解
 * User: sai
 * Date: 2021/1/31
 * Time: 11:37
 */
@Data
public class HomeInfoRespVO {
    @ApiModelProperty(value = "title")
    private String title;
    @ApiModelProperty(value = "href")
    private String href;
}
