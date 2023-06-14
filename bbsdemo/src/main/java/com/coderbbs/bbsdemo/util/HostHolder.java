package com.coderbbs.bbsdemo.util;

import com.coderbbs.bbsdemo.entity.User;
import org.springframework.stereotype.Component;

@Component
//作用是持有用户信息，用于代替session对象
public class HostHolder {

    //这个线程里存的是user
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    //也要注意经常清理线程，免得太多
    public void clear(){
        users.remove();
    }
}
