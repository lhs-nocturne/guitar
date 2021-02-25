package com.heb.guitar.exception;

import com.heb.guitar.exception.code.ResponseCodeInterface;

/**
 * @Description:异常处理类
 * @Author: sai
 * @Date:2020/6/21
 * @Time:14:30
 */
public class BusinessException extends RuntimeException{

    private final int code;

    private final String msg;

    /**
     * 构造函数
     * @param code 异常码
     */
    public BusinessException(ResponseCodeInterface code) {
        this(code.getCode(), code.getMsg());
    }

    public BusinessException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
