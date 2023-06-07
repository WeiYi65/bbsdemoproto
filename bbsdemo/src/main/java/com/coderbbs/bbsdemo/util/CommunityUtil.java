package com.coderbbs.bbsdemo.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //因为很简单所以不加容器了

    //功能1：生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");//因为会生成横线，我们不要横线，用空字符串代替
    }

    //MD5算法对密码进行加密。它只能加密不能解密。但除此之外还要使用salt加上随机的字符串进行二次加密。
    public static String md5(String key){
        //key实际上就是密码+salt拼一块，然后传进来给md5加密
        if(StringUtils.isBlank(key)){
            //key是null，空串或者空格都会被判定为空
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//传入的参数必须是bytes
    }
}
