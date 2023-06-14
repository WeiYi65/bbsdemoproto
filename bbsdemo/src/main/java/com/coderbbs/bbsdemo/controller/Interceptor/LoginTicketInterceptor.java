package com.coderbbs.bbsdemo.controller.Interceptor;

import com.coderbbs.bbsdemo.entity.LoginTicket;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CookieUtil;
import com.coderbbs.bbsdemo.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //一开始就要请求一个login ticket，这个从cookie里得到
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");

        if(ticket!=null){
            //意思是登录了，那么查询ticket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //判断凭证是否有效
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                //意思是只有当登录ticket不为空，并且状态是有效，且超时时间晚于当前时间
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());

                //要暂存在本次请求中持有的user。最好能同时处理多个用户（因为服务器是多线程并发，要考虑线程隔离
                //解决多线程隔离的工具叫做ThreadLocal,在util包下
                hostHolder.setUser(user);
            }
        }

        return true;
    }

    //在调用模板引擎之前应该把host线程存到model里
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            modelAndView.addObject("loginUser", user);
        }
    }

    //在模板都执行完以后把线程里的东西清掉
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
