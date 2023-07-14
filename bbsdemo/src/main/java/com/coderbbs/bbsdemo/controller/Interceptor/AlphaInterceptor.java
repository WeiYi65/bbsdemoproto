package com.coderbbs.bbsdemo.controller.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AlphaInterceptor implements HandlerInterceptor {//拦截器的接口

    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    //分别实现以下接口里的三个方法
    //其实不用都实现，仅作为熟练用
    //在做完方法后还需要进行配置。配置文件在condig包下

    //@Override
    //在controller之前执行，一般不咋用
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        logger.debug("preHandle: "+handler.toString());
//        return HandlerInterceptor.super.preHandle(request, response, handler);//先不管他
//    }

    @Override
    //在controller之后执行（在调用模版引擎之前）
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: "+handler.toString());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    //在模版引擎执行完之后执行。
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion: "+handler.toString());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
