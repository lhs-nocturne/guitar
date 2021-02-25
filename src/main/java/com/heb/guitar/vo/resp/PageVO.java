package com.heb.guitar.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class PageVO <T>{

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数")
    private Long totalRows;
    /**
     * 数据列表
     */
    @ApiModelProperty(value = "数据列表")
    private List<T> list;

}
