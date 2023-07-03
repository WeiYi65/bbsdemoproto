package com.coderbbs.bbsdemo.util;

import io.lettuce.core.cluster.PipelinedRedisFuture;
import org.apache.ibatis.annotations.Param;

public class RedisKeyUtil {
    //提供rediskey的工具
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    //生成某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        //这么一堆东西其实就是定位出了一个可以被点赞的帖子/回复，这个帖子就是set里的key
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT +entityId;
    }
}
