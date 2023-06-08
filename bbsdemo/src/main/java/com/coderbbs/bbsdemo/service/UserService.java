package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.UserMapper;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    @Value("${server.servlet.context-path}")//项目名
    private String contextPath;

    //提供根据用户id查询用户的服务
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){//注册方法
        Map<String, Object> map = new HashMap<>();

        //对空值输入进行判断
        if(user==null){
            throw new IllegalArgumentException("User can not be empty!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("Username Msg", "Username can not be empty!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("Password Msg", "Password can not be empty!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("Email Msg", "Email can not be empty!");
            return map;
        }

        //验证信息是否已被注册过
        User u = userMapper.selectByName(user.getUsername());//u是数据库里真实存在的数据，user是正在注册的数据
        if(u!=null){
            map.put("Username Msg", "The username has been taken!");
            return map;
        }
        //然后是验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("Email Msg", "The email has been used!");
            return map;
        }

        //根据信息注册用户，先对用户密码进行加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));//生成盐
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));//把盐和密码一起加密
        user.setType(0);//帮用户设定好type和status
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());//给用户发送的激活码

        //给用户一个默认的头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        //注入进数据库中.用户注册时是没有id的，但insert后mybatis会自动生成id（在配置文件中设成了true）
        userMapper.insertUser(user);

        //并给用户发送email激活邮件,邮件的模板来自mail包里的activation.html文件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/id/code 激活路径
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "Active the account", content);

        return map;
    }


}
