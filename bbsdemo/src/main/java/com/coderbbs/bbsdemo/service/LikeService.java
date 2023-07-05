package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞和取消赞
    public void like(int userId, int entityType, int entityId, int entityUserId){
        //因为要对点赞代码进行重构，使之能够支持根据用户统计点赞数，所以全部注释掉
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        //看下用户是否点过赞，如果点过就是true
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember){//取赞
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        }else {//点赞
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                //判断一下点没点过赞.注意要在开启redis事务之前查询，不然中途无法查询
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();

                if (isMember){
                    //如果曾经点过赞，那么帖子和用户界面都要做取消赞的处理
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                }
                else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
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

    //查询某个用户获得的赞量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        //默认得到的是object所以强制转换
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();
    }

}
