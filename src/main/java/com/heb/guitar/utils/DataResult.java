package com.heb.guitar.utils;

import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.exception.code.ResponseCodeInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:描述
 * @Author: sai
 * @Date:2020/6/21
 * @Time:20:02
 */
@Data
public class DataResult <T> {

    /**
     * 请求响应code，0为成功 其他为失败
     */
    @ApiModelProperty(value = "请求响应code，0为成功 其他为失败", name = "code")
    private int code = 0;
    /**
     * 响应异常码详细信息
     */
    @ApiModelProperty(value = "响应异常码详细信息", name = "msg")
    private String msg;
    /**
     * 响应内容 ， code 0 时为 返回 数据
     */
    @ApiModelProperty(value = "需要返回的数据", name = "data")
    private T data;

    public DataResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public DataResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public DataResult(int count, T data) {
        this.code = BaseResponseCode.SUCCESS.getCode();
        this.msg = BaseResponseCode.SUCCESS.getMsg();
        this.data = data;
    }

    public DataResult(){
        this.code = BaseResponseCode.SUCCESS.getCode();
        this.msg = BaseResponseCode.SUCCESS.getMsg();
        this.data = null;
    }

    public DataResult(T data) {
        this.data = data;
        this.code=BaseResponseCode.SUCCESS.getCode();
        this.msg=BaseResponseCode.SUCCESS.getMsg();
    }


    /**
     * 自定义 返回操作 data 可控
     * @Author:sai
     * @UpdateUser:
     * @Version:0.0.1
     * @param code
     * @param msg
     * @param data
     * @return com.heb.guitar.utils.DataResult
     * @throws
     */
    public static <T> DataResult getResult(int code, String msg, T data){
        return new DataResult(code,msg,data);
    }

    /**
     * 操作成功 data 不为null
     * @Author:sai
     * @UpdateUser:
     * @Version:0.0.1
     * @param data
     * @return com.heb.guitar.utils.DataResult<T>
     * @throws
     */

    public static <T> DataResult getResult(T data){
        return new DataResult(data);
    }

    /**
     * 自定义返回 data为null
     * @Author:sai
     * @UpdateUser:
     * @Version:0.0.1
     * @param code
    • * @param msg
     * @return com.heb.guitar.utils.DataResult
     * @throws
     */
    public static DataResult getResult(int code, String msg){
        return new DataResult(code,msg);
    }

    public DataResult(ResponseCodeInterface responseCodeInterface){
          this.code = responseCodeInterface.getCode();
          this.msg = responseCodeInterface.getMsg();
          this.data = null;
    }

    public DataResult(ResponseCodeInterface responseCodeInterface,T data){
        this.code = responseCodeInterface.getCode();
        this.msg = responseCodeInterface.getMsg();
        this.data = data;
    }

    /**
     * 自定义返回 入参一般是异常code枚举 data为空
     * @Author:sai
     * @UpdateUser:
     * @Version:     0.0.1
     * @param responseCodeInterface
     * @return com.heb.guitar.utils.DataResult
     * @throws
     */
    public static DataResult getResult(ResponseCodeInterface responseCodeInterface){
        return new DataResult(responseCodeInterface);
    }

    /**
     * 自定义返回 入参一般是异常code枚举 data 可控
     * @Author:sai
     * @UpdateUser:
     * @Version:0.0.1
     * @param responseCodeInterface
     * @param data
     * @return com.heb.guitar.utils.DataResult
     * @throws
     */
    public static <T> DataResult getResult(ResponseCodeInterface responseCodeInterface,T data){
        return new DataResult(responseCodeInterface,data);
    }

    /**
     * 操作成功 data为null
     * @Author:sai
     * @UpdateUser:
     * @Version:0.0.1
     * @param
     * @return com.heb.guitar.utils.DataResult<T>
     * @throws
     */
    public static DataResult success(){
        return new DataResult();
    }

    /**
     * 操作成功 data 不为null
     * @Author:sai
     * @UpdateUser:
     * @Version:0.0.1
     * @param data
     * @return com.heb.guitar.utils.DataResult<T>
     * @throws
     */
    public static <T> DataResult success(T data){
        return new DataResult(data);
    }

}
