package com.coderbbs.bbsdemo.util;

import io.lettuce.core.cluster.PipelinedRedisFuture;
import org.apache.ibatis.annotations.Param;

public class RedisKeyUtil {
    //提供rediskey的工具
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_FOLLOWER = "follower";

    //生成某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        //这么一堆东西其实就是定位出了一个可以被点赞的帖子/回复，这个帖子就是set里的key
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT +entityId;
    }

    //某个用户一共有多少赞
    //like:user:userId ->int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体（不一定是用户）
    //followee:userId:entityType -> zset(entityId, now)这里的entityId是关注东西的用户,zset是有序集合
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId, now) 这里有粉丝的可能是帖子或者用户
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

}
