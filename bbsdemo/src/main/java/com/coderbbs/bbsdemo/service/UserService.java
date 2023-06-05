package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.UserMapper;
import com.coderbbs.bbsdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    //提供根据用户id查询用户的服务
    public User findUserById(int id){
        return userMapper.selectById(id);
    }


}
