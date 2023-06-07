package com.coderbbs.bbsdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";//这个register是模板里的html名字，是包的路径
    }

}
