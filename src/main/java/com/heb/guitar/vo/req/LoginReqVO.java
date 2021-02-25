package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 16:34
 */
@Data
public class LoginReqVO {

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "登录类型(1:pc;2:App)")
    @NotBlank(message = "登录类型不能为空")
    private String type;
}
