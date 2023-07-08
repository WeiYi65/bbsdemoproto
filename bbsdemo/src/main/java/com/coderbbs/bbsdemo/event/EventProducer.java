package com.coderbbs.bbsdemo.event;

import com.alibaba.fastjson.JSONObject;
import com.coderbbs.bbsdemo.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件（也就是发一个消息）
    public void fireEvent(Event event){
        //将事件发布到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
