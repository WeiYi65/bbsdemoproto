package com.coderbbs.bbsdemo.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    //for kafka: 定义一个类封装事件
    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    //这个是以防以后出现更多新特性需要封装但此时还不知道，因此统一加入map
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    //设置成返回值的好处是调用set后还可以直接取到该值并继续调用该对象的其他get set方法，可以一股脑狂用
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    //这样也可以无限调用方法 .setData().setData()
    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
