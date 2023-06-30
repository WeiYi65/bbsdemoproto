package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.Comment;
import com.coderbbs.bbsdemo.service.CommentService;
import com.coderbbs.bbsdemo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;//用来得到当前评论的用户id

    //回复的方法
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());//若有空异常，后面做统一的处理
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //重定向回帖子的详情页面
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
