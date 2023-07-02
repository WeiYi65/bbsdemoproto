package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.dao.DiscussPostMapper;
import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.entity.Page;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.DiscussPostService;
import com.coderbbs.bbsdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    //响应的是网页所以不用写response body了
    //方法调用之前，springMVC会自动实例化model和page，并将page注入给model，所以thymeleaf可以直接访问page
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";//这里返回的是templates包下的index.html
    }

    //在这里配置错误页面的访问
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
