package com.heb.guitar.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 16:57
 */
@Data
public class LoginRespVO {
    @ApiModelProperty(value = "token")
    private String accessToken;
    @ApiModelProperty(value = "刷新token")
    private String refreshToken;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "用户id")
    private String id;
    @ApiModelProperty(value = "电话")
    private String phone;
}
