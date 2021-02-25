package com.heb.guitar.constants;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 16:28
 */
public class Constant {
    /*** 用户名称key */
    public static final String JWT_USER_NAME = "jwt-user-name-key_";

    /*** 权限key */
    public static final String JWT_PERMISSIONS_KEY = "jwt-permissions-key_";

    /*** 角色key */
    public static final String JWT_ROLES_KEY = "jwt-roles-key_";

    /*** 删除的key */
    public static final String DELETED_USER_KEY = "deleted-user-key_";

    /*** 锁定的key */
    public static final String ACCOUNT_USER_KEY = "account-user-key_";

    /** 刷新的Key */
    public static final String JWT_REFRESH_KEY = "jwt-refresh-key_";

    /** refresh_token 主动退出后加入黑名单 key */
    public static final String JWT_REFRESH_TOKEN_BLACKLIST = "jwt-refresh-token-blacklist_";

    /*** access_token 主动退出后加入黑名单 key */
    public static final String JWT_ACCESS_TOKEN_BLACKLIST = "jwt-access-token-blacklist_";

    /*** 正常token */
    public static final String ACCESS_TOKEN = "authorization";

    /*** 刷新token */
    public static final String REFRESH_TOKEN = "refresh_token";

    /*** 用户权限缓存 */
    public static final String IDENTIFY_CACHE_KEY = "shiro-cache:com.heb.guitar.shiro.CustomRealm.authorizationCache:";

    /*** 部门编码key */
    public static final String DEPT_CODE_KEY="dept-code-key_";

    /*** 标记新的access_token*/
    public static final String JWT_REFRESH_IDENTIFICATION="jwt-refresh-identification_";

    /*** 标记用户是否已经被锁定*/
    public static final String ACCOUNT_LOCK_KEY="account-lock-key_";


}
