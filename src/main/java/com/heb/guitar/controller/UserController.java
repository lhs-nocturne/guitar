package com.heb.guitar.controller;

import com.heb.guitar.aop.annotation.MyLog;
import com.heb.guitar.constants.Constant;
import com.heb.guitar.entity.SysUser;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.service.UserService;
import com.heb.guitar.utils.DataResult;
import com.heb.guitar.utils.JwtTokenUtil;
import com.heb.guitar.vo.req.*;
import com.heb.guitar.vo.resp.LoginRespVO;
import com.heb.guitar.vo.resp.PageVO;
import com.heb.guitar.vo.resp.UserOwnRoleRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 17:19
 */
@Slf4j
@RestController
@Api(tags = "组织模块-用户管理")
@RequestMapping("/api")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/user/login")
    @ApiOperation(value = "用户登录接口")
    @MyLog(title = "用户管理",action = "用户登录")
    public DataResult<LoginRespVO> login(@RequestBody LoginReqVO vo) {
        DataResult<LoginRespVO> result = DataResult.success(userService.login(vo));
        return result;
    }

    @GetMapping("/user/logout")
    @ApiOperation(value = "用户登出接口")
    public DataResult logout(HttpServletRequest request) {
        try {
            String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
            String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
            userService.logout(accessToken, refreshToken);
        } catch (Exception e) {
            log.error("logout error{}", e);
        }
        return DataResult.success();
    }

    @GetMapping("/user/unLogin")
    @ApiOperation(value = "引导客户端去登录")
    public DataResult unLogin(){
        DataResult result= DataResult.getResult(BaseResponseCode.TOKEN_NOT_NULL);
        return result;
    }

    @MyLog(title = "用户管理",action = "获取用户数据分页列表")
    @PostMapping("/users")
    @ApiOperation(value = "分页获取用户列表接口")
    @RequiresPermissions("sys:user:list")
    public DataResult pageInfo(@RequestBody UserReqVO vo){
        PageVO pageVO = new PageVO();
        pageVO.setList(userService.getUserList(vo));
        pageVO.setTotalRows(userService.userCount(vo));
        DataResult result=DataResult.success(pageVO);
        return result;
    }

    @PostMapping("/user")
    @ApiOperation(value = "新增用户接口")
    @RequiresPermissions("sys:user:add")
    public DataResult addUser(@RequestBody @Valid UserAddReqVO vo){
        userService.addUser(vo);
        return DataResult.success();
    }

    @GetMapping("/user/roles/{userId}")
    @ApiOperation(value = "赋予角色-获取用户拥有角色接口")
    @RequiresPermissions("sys:user:role:update")
    public DataResult<UserOwnRoleRespVO> getUserOwnRole(@PathVariable("userId")String userId){
        DataResult<UserOwnRoleRespVO> result=DataResult.success();
        result.setData(userService.getUserOwnRole(userId));
        return result;
    }

    @PutMapping("/user/roles")
    @ApiOperation(value = "保持用户拥有的角色信息接口")
    public DataResult saveUserOwnRole(@RequestBody @Valid UserRoleOperationReqVO vo){
        DataResult result=DataResult.success();
        userService.setUserOwnRole(vo);
        return result;
    }

    @GetMapping("/user/token")
    @ApiOperation(value = "用户刷新token接口")
    public DataResult<String> refreshToken(HttpServletRequest request){
        String refreshToken=request.getHeader(Constant.REFRESH_TOKEN);
        DataResult<String> result=DataResult.success();
        result.setData(userService.refreshToken(refreshToken));
        return result;
    }

    @PutMapping("/user")
    @ApiOperation(value = "列表更新用户信息接口")
    @RequiresPermissions("sys:user:update")
    public DataResult updateUserInfo(@RequestBody @Valid UserUpdateReqVO vo, HttpServletRequest request){
        String operationId= JwtTokenUtil.getUserId(request.getHeader(Constant.ACCESS_TOKEN));
        userService.updateUserInfo(vo,operationId);
        return DataResult.success();
    }

    @DeleteMapping("/user/{userId}")
    @ApiOperation(value = "删除用户接口")
    @RequiresPermissions("sys:user:delete")
    public DataResult deletedUser(@PathVariable("userId")String userId, HttpServletRequest request){
        String operationId = JwtTokenUtil.getUserId(request.getHeader(Constant.ACCESS_TOKEN));
        userService.deletedUsers(userId,operationId);
        return DataResult.success();
    }

    @GetMapping("/user")
    @ApiOperation(value = "查询用户详情接口")
    public DataResult<SysUser> youSelfInfo(HttpServletRequest request){
        String id=JwtTokenUtil.getUserId(request.getHeader(Constant.ACCESS_TOKEN));
        DataResult<SysUser> result=DataResult.success();
        result.setData(userService.detailInfo(id));
        return result;
    }

    @PutMapping("/user/info")
    @ApiOperation(value = "更新用户信息接口")
    @MyLog(title = "用户管理",action = "个人用户更新用户信息接口")
    public DataResult updateUserInfoById(@RequestBody @Valid UserUpdateDetailInfoReqVO vo,
                                         HttpServletRequest request){
        String userId= JwtTokenUtil.getUserId(request.getHeader(Constant.ACCESS_TOKEN));
        userService.userUpdateDetailInfo(vo,userId);
        return DataResult.success();
    }

    @PutMapping("/user/pwd")
    @ApiOperation(value = "修改密码接口")
    @MyLog(title = "用户管理",action = "更新密码")
    public DataResult updatePwd(@RequestBody UpdatePasswordReqVO vo,HttpServletRequest request){
        String accessToken=request.getHeader(Constant.ACCESS_TOKEN);
        String refreshToken=request.getHeader(Constant.REFRESH_TOKEN);
        String userId=JwtTokenUtil.getUserId(accessToken);
        userService.updatePwd(vo,userId,accessToken,refreshToken);
        return DataResult.success();
    }

}
