package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.UserMapper;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")//这里是在引用properties里的值并让他变成domain
    private String domain;

    //提供根据用户id查询用户的服务
    public User findUserById(int id){
        return userMapper.selectById(id);
    }


}
