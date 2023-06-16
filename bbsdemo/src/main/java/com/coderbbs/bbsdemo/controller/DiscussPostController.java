package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.DiscussPostService;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user==null){
            //403代表没权限
            return CommunityUtil.getJSONString(403, "You have to log in first.");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        discussPostService.addDiscussPost(discussPost);

        //本处可能诞生的bug由将来统一处理
        //0说明状态正确
        return CommunityUtil.getJSONString(0, "Post successfully!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        DiscussPost post = discussPostService.findDiscussPostByPostId(discussPostId);
        model.addAttribute("post", post);
        //查找帖子作者（要注入user service
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //开发完回复功能后补充
        return "/site/discuss-detail";
    }

}
