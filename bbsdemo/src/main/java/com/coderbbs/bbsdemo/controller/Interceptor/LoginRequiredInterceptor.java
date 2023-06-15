package com.coderbbs.bbsdemo.controller.Interceptor;

import com.coderbbs.bbsdemo.annotation.LoginRequired;
import com.coderbbs.bbsdemo.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.logging.Handler;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {//拦截器要实现这个接口

    //注意，本类仍需配置（为了节省静态资源拦截请求的资源），所以与config类的webmvcconfig有联动

    @Autowired
    private HostHolder hostHolder;
    @Override
    //要在执行之前就判断用户有没有登录，所以是pre
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //意思是，如果handler object是一个方法，那么就给他强制转型成后面这个类型
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //这样可以直接获取到拦截的对象的方法
            Method method = handlerMethod.getMethod();
            //就可以取这个方法蕴含的注解了，所有需要登录的注解都会被取到
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);

            //因为也有注解为空的可能性所以要判断
            //这里是当前方法需要登录，但是用户没有登录的情况
            if (loginRequired!=null && hostHolder.getUser()==null){
                //强制重定向要登录界面
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
