package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

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
}
