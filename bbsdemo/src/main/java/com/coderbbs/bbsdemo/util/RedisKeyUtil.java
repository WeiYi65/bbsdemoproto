package com.coderbbs.bbsdemo.util;

import io.lettuce.core.ScanStream;
import io.lettuce.core.cluster.PipelinedRedisFuture;
import org.apache.ibatis.annotations.Param;

public class RedisKeyUtil {
    //提供rediskey的工具
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";

    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";


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

    //验证码获取
    public static String getKaptchaKey(String owner){//使用字符串存入cookie来指代还未登录的用户，这个字符串就叫owner
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    //登录的凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

    //单日uv
    public static String getUVKey(String date){
        return PREFIX_UV+SPLIT+date;
    }

    //区间uv
    public static String getUVKey(String startDate, String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }

    //单日活跃用户dau
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPLIT+date;
    }

    //区间活跃用户
    public static String getDAUKey(String startDate, String endDate){
        return PREFIX_DAU+SPLIT+startDate+SPLIT+endDate;
    }

}
