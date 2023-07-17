package com.coderbbs.bbsdemo.controller.Interceptor;

import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.DataService;
import com.coderbbs.bbsdemo.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计uv
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        //统计dau
        User user = hostHolder.getUser();
        if (user!=null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
