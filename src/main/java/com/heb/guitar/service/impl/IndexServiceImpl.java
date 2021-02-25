package com.heb.guitar.service.impl;

import com.alibaba.fastjson.JSON;
import com.heb.guitar.entity.SysUser;
import com.heb.guitar.mapper.SysUserMapper;
import com.heb.guitar.service.IndexService;
import com.heb.guitar.service.PermissionService;
import com.heb.guitar.vo.resp.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/1/31
 * Time: 12:20
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private PermissionService permissionService;

    @Override
    public IndexHomeRespVO getIndexRespVO(String userId) {
        IndexHomeRespVO indexHomeRespVO = new IndexHomeRespVO();
        /*String menuInfo ="[{\n" +
                "  \"title\": \"主页模板\",\n" +
                "  \"href\": \"\",\n" +
                "  \"icon\": \"fa fa-home\",\n" +
                "  \"target\": \"_self\",\n" +
                "  \"child\": [\n" +
                "\t{\n" +
                "\t  \"title\": \"菜单权限管理\",\n" +
                "\t  \"href\": \"/index/menus\",\n" +
                "\t  \"icon\": \"fa fa-tachometer\",\n" +
                "\t  \"target\": \"_self\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t  \"title\": \"主页二\",\n" +
                "\t  \"href\": \"page/welcome-2.html\",\n" +
                "\t  \"icon\": \"fa fa-tachometer\",\n" +
                "\t  \"target\": \"_self\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t  \"title\": \"主页三\",\n" +
                "\t  \"href\": \"page/welcome-3.html\",\n" +
                "\t  \"icon\": \"fa fa-tachometer\",\n" +
                "\t  \"target\": \"_self\"\n" +
                "\t}\n" +
                "  ]\n" +
                "}]";
        List<MenuInfoRespVO> list = JSON.parseArray(menuInfo,MenuInfoRespVO.class);*/
        List<MenuInfoRespVO> list = permissionService.permissionTreeList(userId);
        indexHomeRespVO.setMenuInfo(list);;
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        UserInfoRespVO respVO=new UserInfoRespVO();
        if(sysUser!=null){
            respVO.setUsername(sysUser.getUsername());
            respVO.setDeptName("测试单位");
            respVO.setId(sysUser.getId());
            respVO.setRealName(sysUser.getRealName());
        }
        indexHomeRespVO.setUserInfo(respVO);
        LogoInfoRespVO logoInfoRespVO = new LogoInfoRespVO();
        logoInfoRespVO.setTitle("MY SYSTEM");
        logoInfoRespVO.setImage("/images/logo.png");
        logoInfoRespVO.setHref("");
        HomeInfoRespVO homeInfoRespVO = new HomeInfoRespVO();
        homeInfoRespVO.setHref("/index/main");
        homeInfoRespVO.setTitle("首页");
        indexHomeRespVO.setLogoInfo(logoInfoRespVO);
        indexHomeRespVO.setHomeInfo(homeInfoRespVO);
        return indexHomeRespVO;
    }

}
