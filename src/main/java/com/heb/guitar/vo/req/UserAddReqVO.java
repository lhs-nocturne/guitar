package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UserAddReqVO {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "账号不能为空")
    private String username;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    @ApiModelProperty(value = "手机号码")
    private String phone;
    @ApiModelProperty(value = "创建来源(1.web 2.android 3.ios )")
    private Integer createWhere;
    @ApiModelProperty(value = "所属机构")
    @NotBlank(message = "所属机构不能为空")
    private String deptId;
    @ApiModelProperty(value = "账户状态(1.正常 2.锁定 )")
    private Integer status;
    @ApiModelProperty(value = "性别")
    private Integer sex;

}
