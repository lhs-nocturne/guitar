package com.heb.guitar.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 22:11
 */
@Api(tags = "视图",description = "负责返回视图")
@Controller
@RequestMapping("/index")
public class IndexController {

    @GetMapping("/404")
    @ApiOperation(value = "跳转404错误页面")
    public String error404(){
        return "error/404";
    }

    @GetMapping("/login")
    @ApiOperation(value = "跳转登录界面")
    public String logout(){
        return "login";
    }

    @GetMapping("/home")
    @ApiOperation(value = "跳转首页界面")
    public String home(){
        return "home";
    }

    @GetMapping("/main")
    @ApiOperation(value = "跳转默认主页方法")
    public String indexHome(){
        return "main";
    }

    @GetMapping("/menus")
    @ApiOperation(value = "跳转菜单权限页面")
    public String menusList(){
        return "page/menus/menu";
    }

    @GetMapping("/userSetting")
    @ApiOperation(value = "跳转默认主页方法")
    public String userSetting(){
        return "page/users/user_setting";
    }

    @GetMapping("/userPassword")
    @ApiOperation(value = "跳转默认主页方法")
    public String userPassword(){
        return "page/users/user_password";
    }

    @GetMapping("/roles")
    @ApiOperation(value = "跳转角色管理页面")
    public String roleList(){
        return "page/roles/role";
    }

    @GetMapping("/depts")
    @ApiOperation(value = "跳转部门管理页面")
    public String deptList(){
        return "page/depts/dept";
    }

    @GetMapping("/users")
    @ApiOperation(value = "跳转用户管理页面")
    public String userList(){
        return "page/users/user";
    }

    @GetMapping("/logs")
    @ApiOperation(value = "跳转日志管理页面")
    public String logList(){
        return "page/logs/log";
    }

    @GetMapping("/icons")
    @ApiOperation(value = "跳转图标页面")
    public String iconList(){
        return "page/icon";
    }


}
