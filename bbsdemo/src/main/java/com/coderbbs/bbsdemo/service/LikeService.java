package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞和取消赞
    public void like(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //看下用户是否点过赞，如果点过就是true
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember){//取赞
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        }else {//点赞
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    }

    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //返回了这个帖子被点赞的次数
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询用户对某帖子是否点过赞.用int是方便以后业务扩展，比如用户点过踩，可以是状态3
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1:0;
    }

}
