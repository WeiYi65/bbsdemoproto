package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //关注方法
    public void follow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                //连续做两次存储操作，按时间存（因为是有序集合）
                operations.multi();//这是启用事务
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    //取关方法
    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                //连续做两次存储操作，按时间存（因为是有序集合）
                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    //查询关注的东西的粉丝的数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询自己的粉丝数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注了所观察的目标
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        //如果这个不是空的，说明确实关注了，如果是空的说明没关注
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    //查询某用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        //查找关注列表
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset+limit-1);
        //没有关注就返回空
        if(targetIds==null){
            return null;
        }
        //这个list里面存放所有关注的人的信息，遍历所有关注者
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);

            //还原关注时间。map里现在有两个属性：user和follow time。然后加入list里
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }

    //查询某用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset+limit-1);

        if (targetIds==null){
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);

            //还原关注时间。map里现在有两个属性：user和follow time。然后加入list里
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;


    }

}
