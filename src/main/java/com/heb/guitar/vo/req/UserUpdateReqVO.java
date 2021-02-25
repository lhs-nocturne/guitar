package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateReqVO {
    @ApiModelProperty(value = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "手机号码")
    private String phone;
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "所属机构")
    private String deptId;
    @ApiModelProperty(value = "账户状态(1.正常 2.锁定 )")
    private Integer status;
    @ApiModelProperty(value = "性别")
    private Integer sex;
}
