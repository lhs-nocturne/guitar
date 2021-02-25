package com.heb.guitar.service;

import com.heb.guitar.entity.SysUser;
import com.heb.guitar.vo.req.*;
import com.heb.guitar.vo.resp.LoginRespVO;
import com.heb.guitar.vo.resp.UserOwnRoleRespVO;
import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 17:01
 */
public interface UserService {

    LoginRespVO login(LoginReqVO vo);
    /**
     * 退出登录
     */
    void logout(String accessToken,String refreshToken);

    List<SysUser> getUserList(UserReqVO vo);

    long userCount(UserReqVO vo);

    void addUser(UserAddReqVO vo);

    //根据用户id获取用户拥有角色
    UserOwnRoleRespVO getUserOwnRole(String userId);

    void setUserOwnRole(UserRoleOperationReqVO vo);

    String refreshToken(String refreshToken);

    void updateUserInfo(UserUpdateReqVO vo,String operationId);

    void deletedUsers(String id, String operationId);

    SysUser detailInfo(String userId);
    //个人用户编辑信息接口
    void userUpdateDetailInfo(UserUpdateDetailInfoReqVO vo,String userId);

    void updatePwd(UpdatePasswordReqVO vo,String userId,String accessToken, String refreshToken);


}
