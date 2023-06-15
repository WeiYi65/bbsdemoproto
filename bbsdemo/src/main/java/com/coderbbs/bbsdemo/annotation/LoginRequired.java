package com.coderbbs.bbsdemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//注解可以写在哪（这里是可以写在方法上）
@Retention(RetentionPolicy.RUNTIME)//注解生效周期（这里是运行时才有效）
public @interface LoginRequired {//仅表示登录时才可访问，不需要写任何东西
    //本程序需要login才能访问的有setting，修改头像，修改密码
    //其余的内容由interceptor文件完成
}
