package com.heb.guitar.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 17:55
 */
@Data
public class UserReqVO {

    @ApiModelProperty(value = "当前第几页")
    private Integer pageNum=1;

    @ApiModelProperty(value = "当前页数量")
    private Integer pageSize=10;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "账户状态(1.正常 2.锁定 ")
    private Integer status;

    @ApiModelProperty(value = "姓名")
    private String realName;
}
