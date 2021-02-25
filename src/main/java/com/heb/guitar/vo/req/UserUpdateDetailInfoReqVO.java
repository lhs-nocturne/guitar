package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserUpdateDetailInfoReqVO {
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "性别(1.男 2.女)")
    private Integer sex;
    @ApiModelProperty(value = "真实名称")
    private String realName;
    @ApiModelProperty(value = "手机号")
    private String phone;
}
