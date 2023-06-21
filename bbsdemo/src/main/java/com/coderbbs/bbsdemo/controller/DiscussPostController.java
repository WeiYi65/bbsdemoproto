package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.Comment;
import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.entity.Page;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.CommentService;
import com.coderbbs.bbsdemo.service.DiscussPostService;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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

    //非常非常非常复杂
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost post = discussPostService.findDiscussPostByPostId(discussPostId);
        model.addAttribute("post", post);
        //查找帖子作者（要注入user service
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //评论分页信息完善
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //这里是给原贴的评论（不包含楼中楼）
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(),
                page.getOffset(), page.getLimit());
        //Vo指的是view of，意思是显示对象
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList){
                //一个原贴评论的VO
                Map<String, Object> commentVo = new HashMap<>();
                //这是往vo里放了个评论和评论的作者
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //这里开始是楼中楼（上边评论的评论）
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                //回复的vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply:replyList){
                        Map<String, Object> replyVo = new HashMap<>();

                        //楼中楼的vo
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));

                        //楼中楼的回复对象
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys", replyVoList);

                //统计楼中楼数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

}
