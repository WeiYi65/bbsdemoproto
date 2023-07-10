package com.coderbbs.bbsdemo.event;

import com.alibaba.fastjson.JSONObject;
import com.coderbbs.bbsdemo.entity.Event;
import com.coderbbs.bbsdemo.entity.Message;
import com.coderbbs.bbsdemo.service.MessageService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    //因为这几个站内信格式相似，所以统一用一个方法表达
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if (record==null || record.value()==null){
            logger.error("Message is null.");
            return;
        }

        //把JSON格式的字符串return回对象格式
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event==null){
            logger.error("Message Format Error.");
        }
        //上面两个if，是确保内容不为空且格式正确的


        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());//这是被点赞了要接收通知的人
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());//这是事件的触发者（点赞的人）
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        //有些event消息数据比较多，多余的就也存进content里
        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()){
                //每次遍历得到的key value
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }
}
