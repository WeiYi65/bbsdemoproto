package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.Event;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.event.EventProducer;
import com.coderbbs.bbsdemo.service.LikeService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody //因为是异步请求所以要加入这个
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHolder.getUser();

        //实现点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        //统计点赞状态和数量返回网页
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        //封装后统一传给页面.这里是返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //点赞之后发通知（取赞就不通知了）
        if (likeStatus==1){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);//这里是为了能让消息通知的时候还给用户发帖子的链接
            eventProducer.fireEvent(event);
        }

        //异步请求所以返回JSON
        return CommunityUtil.getJSONString(0, null, map);
    }
}
