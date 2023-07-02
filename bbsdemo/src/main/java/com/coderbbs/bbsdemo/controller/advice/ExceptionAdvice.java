package com.coderbbs.bbsdemo.controller.advice;

import com.coderbbs.bbsdemo.util.CommunityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

//这个包用来处理统一捕获的异常，因为扫描全局范围过大，一般还要限制路径
//这里是只扫描带有controller注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    //异常需要记录日志
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    //这个注解专门处理报错后对error的捕获，最常用
    @ExceptionHandler({Exception.class})
    //这里的参数不仅限三个，不过这三个最常用
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //先记日志
        logger.error("server error: " + e.getMessage());
        //然后遍历异常的栈的信息
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //浏览器希望返回网页时返回500，但异步请求时需要返回JSON string，不能重定向回500
        //先判断是网页还是异步请求
        String xRequestWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestWith)){
            //XML代表异步，说明这是一个异步请求
            response.setContentType("application/plain:charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "server error."));
        }
        else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
