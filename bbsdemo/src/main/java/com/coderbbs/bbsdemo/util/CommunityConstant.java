package com.coderbbs.bbsdemo.util;

public interface CommunityConstant {
    //主管邮箱激活码点击后的处理
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAIL = 2;

    //默认状态登录凭证超时时间
    int DEFAULT_EXPIRED_SECONDS = 3600*12;

    //记住状态的凭证超时时间
    int REMEMBER_EXPIRED_SECONDS = 3600*24*100;

    //实体类型：帖子
    int ENTITY_TYPE_POST = 1;

    //实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;

    //实体类型：用户
    int ENTITY_TYPE_USER = 3;

    //topic：评论
    String TOPIC_COMMENT = "comment";

    //topic：点赞
    String TOPIC_LIKE = "like";

    //topic：关注
    String TOPIC_FOLLOW = "follow";

    //发帖
    String TOPIC_PUBLISH = "publish";

    //系统用户的id
    int SYSTEM_USER_ID = 1;

    //用户权限:普通用户
    String AUTHORITY_USER = "user";

    //管理员
    String AUTHORITY_ADMIN = "admin";

    //版主
    String AUTHORITY_MODERATOR = "moderator";
}
