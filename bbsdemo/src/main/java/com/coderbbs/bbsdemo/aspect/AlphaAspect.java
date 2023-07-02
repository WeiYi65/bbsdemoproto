package com.coderbbs.bbsdemo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    //这里的指定表达可以非常灵活
    @Pointcut("execution(* com.coderbbs.bbsdemo.service.*.*(..))")
    public void pointcut(){

    }

    //有五种通知，所以也有五种注解
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    //既想在前又想在后

    /**
     *
     * @param joinPoint 目标织入的部位
     * @return
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("aroundBefore");
        Object obj = joinPoint.proceed();//调用目标组件的方法，可能有返回值，那么存储，相当于代替本来对象
        System.out.println("aroundAfter");
        return obj;
    }
}
