package com.pract.advisor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //放置的目标位置为方法级别
@Retention(RetentionPolicy.RUNTIME) //注解在运行时执行
public @interface OperationLogAnnotation {
    String optModule() default "";  //操作模块

    String optType() default ""; //操作类型

    String optDesc() default ""; //操作描述
}
