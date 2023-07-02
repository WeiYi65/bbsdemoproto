package com.coderbbs.bbsdemo.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.coderbbs.bbsdemo.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    private void before(JoinPoint joinPoint){
        //输出用户xxx（ip地址）在xxx时间访问了xxx功能调用了xxx方法
        //获取ip和时间
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //前半是调用的服务的类名，后半是方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("User [%s] visited [%s] at [%s]. ", ip, target, now));
    }
}
