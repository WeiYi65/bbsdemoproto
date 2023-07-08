package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.LoginTicketMapper;
import com.coderbbs.bbsdemo.dao.UserMapper;
import com.coderbbs.bbsdemo.entity.LoginTicket;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.MailClient;
import com.coderbbs.bbsdemo.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")//这里是在引用properties里的值并让他变成domain
    private String domain;

    @Value("${server.servlet.context-path}")//项目名
    private String contextPath;

    //提供根据用户id查询用户的服务
    //这是非常频繁使用的方法，所以现在不再用sql而是用redis来访问，提高效率。现在去最下方建立缓存查找方法，如果找不到再调这里的
    public User findUserById(int id){
        //return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null){
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user){//注册方法
        Map<String, Object> map = new HashMap<>();

        //对空值输入进行判断
        if(user==null){
            throw new IllegalArgumentException("User can not be empty!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("UsernameMsg", "Username can not be empty!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("PasswordMsg", "Password can not be empty!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("EmailMsg", "Email can not be empty!");
            return map;
        }

        //验证信息是否已被注册过
        User u = userMapper.selectByName(user.getUsername());//u是数据库里真实存在的数据，user是正在注册的数据
        if(u!=null){
            map.put("UsernameMsg", "The username has been taken!");
            return map;
        }
        //然后是验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("EmailMsg", "The email has been used!");
            return map;
        }

        //根据信息注册用户，先对用户密码进行加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));//生成盐
        //user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));//把盐和密码一起加密
        user.setPassword(user.getPassword());
        user.setType(0);//帮用户设定好type和status
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());//给用户发送的激活码

        //给用户一个默认的头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%d.png", new Random().nextInt(1000)));
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
        //mailClient.sendMail(user.getEmail(), "Active the account", content);

        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            //注册的时候设置为0，激活后改为1。若已经是1，说明已被激活
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            //如果激活码和传入的激活码一致，那么激活成功
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FAIL;
        }
    }


    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值判断
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "Username can not be empty!");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "Password can not be empty!");
            return map;
        }

        //存在和激活判断
        User user = userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg", "The account does not exist.");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg", "The account is not activated.");
            return map;
        }

        //验证密码
        //password = CommunityUtil.md5(password+user.getSalt());
        //System.out.println(user.getPassword());这里似乎没有给本来的密码进行过加密
        //System.out.println(password);
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "The password is not correct.");
            return map;
        }

        //以上验证都通过，那么可以登录。生成新的登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);//把新生成的凭证放进sql里

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    //退出登录
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl){
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public int updatePassword(int userId, String password){
        return userMapper.updatePassword(userId, password);
    }


    public boolean oldPassword(int userId, String oldPassword){
        User user = userMapper.selectById(userId);
        return user.getPassword().equals(oldPassword);
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    //缓存管理用户数据方法：1、优先从缓存取值。2、若取不到，初始化缓存数据。3、数据变更时，清除缓存。
    //先写第一个方法
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //就是把用户放进缓存里
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);//缓存的数据会保存一个小时
        return user;
    }

    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
