package com.coderbbs.bbsdemo.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    public static  String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map!=null){
            for(String key: map.keySet()){
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", "25");
        System.out.println(getJSONString(0, "ok", map));//这样会得到JSON格式的字符串
    }
}
