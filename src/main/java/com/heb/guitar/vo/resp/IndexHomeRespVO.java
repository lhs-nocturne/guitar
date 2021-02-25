package com.heb.guitar.vo.resp;

import lombok.Data;

import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/1/31
 * Time: 11:31
 */
@Data
public class IndexHomeRespVO {

    private UserInfoRespVO userInfo;

    private HomeInfoRespVO homeInfo;

    private LogoInfoRespVO logoInfo;

    private List<MenuInfoRespVO> menuInfo;

}
