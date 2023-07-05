package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.LikeService;
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
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody //因为是异步请求所以要加入这个
    public String like(int entityType, int entityId, int entityUserId){
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

        //异步请求所以返回JSON
        return CommunityUtil.getJSONString(0, null, map);
    }
}
