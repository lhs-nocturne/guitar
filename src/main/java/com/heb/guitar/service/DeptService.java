package com.heb.guitar.service;

import com.heb.guitar.entity.SysDept;
import com.heb.guitar.vo.req.DeptAddReqVO;
import com.heb.guitar.vo.req.DeptUpdateReqVO;
import com.heb.guitar.vo.resp.DeptRespNodeVO;
import java.util.List;

public interface DeptService {

    List<SysDept> selectAll();

    List<DeptRespNodeVO> deptTreeList(String deptId);

    SysDept addDept(DeptAddReqVO vo);

    void updateDept(DeptUpdateReqVO vo);

    void deleted(String id);

}
