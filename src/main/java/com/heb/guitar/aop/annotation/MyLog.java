package com.heb.guitar.aop.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: MyLog
 * TODO:类文件简单描述
 * @Version: 0.0.1
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyLog {

    /** 模块 */
    String title() default "";
    /** 功能 */
    String action() default "";

}
