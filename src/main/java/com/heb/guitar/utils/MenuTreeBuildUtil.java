package com.heb.guitar.utils;

import com.heb.guitar.entity.SysPermission;
import com.heb.guitar.vo.resp.MenuInfoRespVO;
import java.util.ArrayList;
import java.util.List;

public  class MenuTreeBuildUtil {
    /**
     * 递归获取表格菜单树
     * @param all
     * @return
     */
    public static List<MenuInfoRespVO> getMenuTree(List<SysPermission> all){
        List<MenuInfoRespVO> list=new ArrayList<>();
        if (all==null||all.isEmpty()){
            return list;
        }
        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals("0")){
                MenuInfoRespVO menuInfoRespVO=new MenuInfoRespVO();
                menuInfoRespVO.setId(sysPermission.getId());
                menuInfoRespVO.setTitle(sysPermission.getName());
                menuInfoRespVO.setHref(sysPermission.getUrl());
                menuInfoRespVO.setIcon(sysPermission.getIcon());
                menuInfoRespVO.setTarget(sysPermission.getTarget());
                menuInfoRespVO.setChild(getMenuChildExcBtn(sysPermission.getId(),all));
                list.add(menuInfoRespVO);
            }
        }
        return list;
    }

    /**
     * 只递归获取目录和菜单
     * @param id
     * @param all
     * @return
     */
    public static List<MenuInfoRespVO> getMenuChildExcBtn(String id, List<SysPermission> all){
        List<MenuInfoRespVO> list=new ArrayList<>();
        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals(id)&&sysPermission.getType()!=3){
                MenuInfoRespVO menuInfoRespVO=new MenuInfoRespVO();
                menuInfoRespVO.setId(sysPermission.getId());
                menuInfoRespVO.setTitle(sysPermission.getName());
                menuInfoRespVO.setHref(sysPermission.getUrl());
                menuInfoRespVO.setIcon(sysPermission.getIcon());
                menuInfoRespVO.setTarget(sysPermission.getTarget());
                menuInfoRespVO.setChild(getMenuChildExcBtn(sysPermission.getId(),all));
                list.add(menuInfoRespVO);
            }
        }
        return list;
    }
}
