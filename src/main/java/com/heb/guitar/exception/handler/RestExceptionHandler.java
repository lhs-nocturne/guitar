package com.heb.guitar.exception.handler;

import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.utils.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

/**
 * @Description:统一异常处理类
 * @Author: sai
 * @Date:2020/6/22
 * @Time:20:12
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    // 获取到日志对象
    //private Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public DataResult exceptionHandler(Exception e){
        log.error("Exception,exception:{}"+e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public DataResult businessExceptionHandler(BusinessException e){
        log.error("BusinessException,businessException:{}"+e);
        return DataResult.getResult(e.getCode(),e.getMsg());
    }

    /**
     * 处理validation 框架异常
     * @Author:lhs
     * @UpdateUser:
     * @Version:0.0.1
     * @param e
     * @return com.heb.guitar.utils.DataResult<T>
     * @throws
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidExceptionHandler bindingResult.allErrors():{},exception:{}",
                e.getBindingResult().getAllErrors(), e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String[] msgs = new String[errors.size()];
        int i = 0;
        for(ObjectError error:errors){
            msgs[i] = error.getDefaultMessage();
            log.info("msg{}"+msgs[i]);
            i++;
        }
        return DataResult.getResult(BaseResponseCode.VALIDATOR_ERROR.getCode(), msgs[0]);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public DataResult unauthorizedException(UnauthorizedException e){
        log.error("UnauthorizedException:{}",e);
        return DataResult.getResult(BaseResponseCode.NOT_PERMISSION);
    }

}
