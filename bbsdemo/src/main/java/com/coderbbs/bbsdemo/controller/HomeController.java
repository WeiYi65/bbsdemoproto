package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.dao.DiscussPostMapper;
import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.entity.Page;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.DiscussPostService;
import com.coderbbs.bbsdemo.service.LikeService;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    //响应的是网页所以不用写response body了
    //方法调用之前，springMVC会自动实例化model和page，并将page注入给model，所以thymeleaf可以直接访问page
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list = discussPostService
                .findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                //把赞的数量查询后放到首页帖子
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";//这里返回的是templates包下的index.html
    }

    //在这里配置错误页面的访问
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}
