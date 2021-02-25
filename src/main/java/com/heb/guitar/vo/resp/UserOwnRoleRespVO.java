package com.heb.guitar.vo.resp;

import com.heb.guitar.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class UserOwnRoleRespVO {

    @ApiModelProperty("所有角色集合")
    private List<SysRole> allRole;
    @ApiModelProperty(value = "用户所拥有角色集合")
    private List<String> ownRoles;

}
