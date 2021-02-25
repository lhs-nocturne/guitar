package com.heb.guitar.service;

import com.heb.guitar.vo.resp.IndexHomeRespVO;
import com.heb.guitar.vo.resp.MenuInfoRespVO;

import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/1/31
 * Time: 12:16
 */
public interface IndexService {

    IndexHomeRespVO getIndexRespVO(String userId);


}
