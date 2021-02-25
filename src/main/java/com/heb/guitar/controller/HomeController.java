package com.heb.guitar.controller;

import com.heb.guitar.constants.Constant;
import com.heb.guitar.service.IndexService;
import com.heb.guitar.utils.DataResult;
import com.heb.guitar.utils.JwtTokenUtil;
import com.heb.guitar.vo.resp.IndexHomeRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 注解
 * User: sai
 * Date: 2021/1/31
 * Time: 12:54
 */
@Api(tags = "首页数据")
@RestController
@RequestMapping("/api")
public class HomeController {

    @Resource
    private IndexService indexService;

    @GetMapping("/home")
    @ApiOperation(value ="获取首页数据接口")
    public DataResult<IndexHomeRespVO> getHomeInfo(HttpServletRequest request){
        String accessToken=request.getHeader(Constant.ACCESS_TOKEN);
        /**
         * 通过access_token拿userId
         */
        String userId= JwtTokenUtil.getUserId(accessToken);
        DataResult<IndexHomeRespVO> result=DataResult.success(indexService.getIndexRespVO(userId));
        return result;
    }

}
