package com.heb.guitar.service.impl;

import com.heb.guitar.constants.Constant;
import com.heb.guitar.entity.SysDept;
import com.heb.guitar.entity.SysUser;
import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.mapper.SysDeptMapper;
import com.heb.guitar.mapper.SysUserMapper;
import com.heb.guitar.service.DeptService;
import com.heb.guitar.service.RedisService;
import com.heb.guitar.utils.CodeUtil;
import com.heb.guitar.vo.req.DeptAddReqVO;
import com.heb.guitar.vo.req.DeptUpdateReqVO;
import com.heb.guitar.vo.resp.DeptRespNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysDept> selectAll() {
        List<SysDept> list = sysDeptMapper.selectAll();
        for (SysDept sysDept:list){
            SysDept parent = sysDeptMapper.selectByPrimaryKey(sysDept.getPid());
            if(parent!=null){
                sysDept.setPidName(parent.getName());
            }
        }
        return list;
    }

    @Override
    public List<DeptRespNodeVO> deptTreeList(String deptId) {
        List<SysDept> list=sysDeptMapper.selectAll();
       //默认加一个顶级方便新增顶级部门
        DeptRespNodeVO respNodeVO=new DeptRespNodeVO();
        respNodeVO.setTitle("默认顶级部门");
        respNodeVO.setId("0");
        respNodeVO.setSpread(true);
        respNodeVO.setChildren(getTree(list));
        List<DeptRespNodeVO> result=new ArrayList<>();
        result.add(respNodeVO);
        return result;
    }

    @Override
    public SysDept addDept(DeptAddReqVO vo) {
        String relationCode;
        long result=redisService.incrby(Constant.DEPT_CODE_KEY,1);
        String deptCode= CodeUtil.deptCode(String.valueOf(result),6,"0");
        SysDept parent=sysDeptMapper.selectByPrimaryKey(vo.getPid());
        if(vo.getPid().equals("0")){
            relationCode=deptCode;
        }else if(null==parent) {
            log.error("传入的 pid:{}不合法",vo.getPid());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }else {
            relationCode=parent.getRelationCode()+deptCode;
        }
        SysDept sysDept=new SysDept();
        //BeanUtils.copyProperties(vo,sysDept);
        sysDept.setName(vo.getName());
        sysDept.setManagerName(vo.getManagerName());
        sysDept.setCreateTime(new Date());
        sysDept.setId(UUID.randomUUID().toString());
        sysDept.setDeptNo(deptCode);
        sysDept.setRelationCode(relationCode);
        sysDept.setStatus(vo.getStatus());
        sysDept.setPid(vo.getPid());
        sysDept.setPhone(vo.getPhone());
        int count=sysDeptMapper.insertSelective(sysDept);
        if (count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        return sysDept;
    }

    @Override
    public void updateDept(DeptUpdateReqVO vo) {
        SysDept sysDept=sysDeptMapper.selectByPrimaryKey(vo.getId());
        if(null==sysDept){
            log.error("传入 的 id:{}不合法",vo.getId());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        SysDept update=new SysDept();
        BeanUtils.copyProperties(vo,update);
        update.setUpdateTime(new Date());
        int count=sysDeptMapper.updateByPrimaryKeySelective(update);
        if(count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        /**
         * 说明层级发生了变化
         */
        if(!vo.getPid().equals(sysDept.getPid())){
            SysDept parent=sysDeptMapper.selectByPrimaryKey(vo.getPid());
            if(!vo.getPid().equals("0")&&null==parent){
                log.error("传入 的 pid:{}不合法",vo.getId());
                throw new BusinessException(BaseResponseCode.DATA_ERROR);
            }
            SysDept oldParent=sysDeptMapper.selectByPrimaryKey(sysDept.getPid());
            String oldRelationCode;
            String newRelationCode;
            /**
             * 根目录降到其他目录
             */
            if (sysDept.getPid().equals("0")){
                oldRelationCode=sysDept.getDeptNo();
                newRelationCode=parent.getRelationCode()+sysDept.getDeptNo();
            }else if(vo.getPid().equals("0")){//其他目录升级到跟目录
                oldRelationCode=sysDept.getRelationCode();
                newRelationCode=sysDept.getDeptNo();
            }else {
                oldRelationCode=oldParent.getRelationCode();
                newRelationCode=parent.getRelationCode();
            }
            sysDeptMapper.updateRelationCode(oldRelationCode,newRelationCode,sysDept.getRelationCode());
        }
    }

    @Override
    public void deleted(String id) {
        //查找它和它的叶子节点
        SysDept sysDept=sysDeptMapper.selectByPrimaryKey(id);
        if(sysDept==null){
            log.info("传入的部门id在数据库不存在{}",id);
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        List<String> list = sysDeptMapper.selectChildIds(sysDept.getRelationCode());
        //判断它和它子集的叶子节点是否关联有用户
        List<SysUser> sysUsers = sysUserMapper.selectUserInfoByDeptIds(list);
        if(!sysUsers.isEmpty()){
            throw new BusinessException(BaseResponseCode.NOT_PERMISSION_DELETED_DEPT);
        }
        //逻辑删除部门数据
        int count=sysDeptMapper.deletedDepts(new Date(),list);
        if(count==0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    private List<DeptRespNodeVO> getTree(List<SysDept> all){
        List<DeptRespNodeVO> list=new ArrayList<>();
        for(SysDept sysDept:all){
            if(sysDept.getPid().equals("0")){
                DeptRespNodeVO deptTree=new DeptRespNodeVO();
                deptTree.setId(sysDept.getId());
                deptTree.setDeptNo(sysDept.getDeptNo());
                deptTree.setTitle(sysDept.getName());
                deptTree.setStatus(sysDept.getStatus());
                deptTree.setRelationCode(sysDept.getRelationCode());
                deptTree.setSpread(true);
                deptTree.setChildren(getChild(sysDept.getId(),all));
                list.add(deptTree);
            }
        }
        return list;
    }

    private List<DeptRespNodeVO>getChild(String id, List<SysDept> all){
        List<DeptRespNodeVO> list=new ArrayList<>();
        for(SysDept sysDept:all){
            if(sysDept.getPid().equals(id)){
                DeptRespNodeVO deptTree=new DeptRespNodeVO();
                deptTree.setId(sysDept.getId());
                deptTree.setDeptNo(sysDept.getDeptNo());
                deptTree.setTitle(sysDept.getName());
                deptTree.setStatus(sysDept.getStatus());
                deptTree.setRelationCode(sysDept.getRelationCode());
                deptTree.setSpread(true);
                deptTree.setChildren(getChild(sysDept.getId(),all));
                list.add(deptTree);
            }
        }
        return list;
    }

}
