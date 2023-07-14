package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    //开一个日志记录本controller内的异常
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";//这个register是模板里的html名字，是包的路径
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            //userservice里的方法，如果那个方法没有包含任何错误就是null，说明创建了新user，要么要出现成功后跳转的页面
            //自动跳转的html在operate result html文件里
            model.addAttribute("msg", "Registered successfully! An activation email has been sent to your email. ");
            model.addAttribute("target", "/index");//这是自动跳转去的网页
            return "/site/operate-result";//这个意思是返回操作结果页面，然后这个页面会再跳转去index
        }
        else{
            //这是注册不成功时的问题
            model.addAttribute("UsernameMsg", map.get("UsernameMsg"));
            model.addAttribute("PasswordMsg", map.get("PasswordMsg"));
            model.addAttribute("EmailMsg", map.get("EmailMsg"));
            //回退到注册页面
            return "/site/register";
        }
    }

    @RequestMapping(path="/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if (result==ACTIVATION_SUCCESS){
            model.addAttribute("msg", "Active successfully! ");
            model.addAttribute("target", "/login");
        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg", "You have already redeemed the account! ");
            model.addAttribute("target", "/index");
        }else{
            model.addAttribute("msg", "Your activation code is not correct! ");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    //验证码图片刷新
    @RequestMapping(path="/kaptcha", method=RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){//重构后已不需要用session
        //验证码是敏感信息，要存在客户端里，所以要使用session
        //生成验证码（使用前要把producer那个类作为bean注入）
        String text = kaptchaProducer.createText();//会根据config类里的配置生成字符串
        BufferedImage image = kaptchaProducer.createImage(text);

        //把验证码存入session
        //session.setAttribute("kaptcha", text);

        //改为存入redis里
        //解决验证码的归属问题，利用随机生成的字符串
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);//60s就过期
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //把验证码存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);


        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            //如果有异常，计入日志
            logger.error("Fail to get the verification code: "+ e.getMessage());
        }
    }

    //可以重复路径，前提是method里的方法要不同。上面的是get，这里的是post
    @RequestMapping(path="/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, /*HttpSession session, */HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner){
        //session是因为验证码处用了，response也是一样（用了cookie）
        //String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        //判断key有没有过期
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        //上面是因为getAttri返回的是object，要强制转换成String

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "Code is not correct.");
            return "/site/login";//验证码不对，返回登录界面
        }

        //检查账号密码

        int expiredSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        //是在这里调用登录的方法登录的，user service里的方法不能直接登录，要在这里调用
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        //如果登录无误，login方法里会生成ticket，如果有ticket就说明登录没问题，直接登上
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());//登录信息存进cookie里
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{//有问题就回退登录页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path="/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);//这里是业务层和表现层的区分
        SecurityContextHolder.clearContext();
        return "redirect:/login";//默认回去get请求的那个login
    }
}
