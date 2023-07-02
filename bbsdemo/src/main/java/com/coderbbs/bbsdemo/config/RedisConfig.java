package com.coderbbs.bbsdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){//把链接工厂注入进来
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);//链接工厂

        //把java的数据类型存到redis里，需要指定一种序列化的方式（数据转换的方式）
        //key的序列化方式：（这里只支持字符串key）
        template.setKeySerializer(RedisSerializer.string());
        //value的序列化方式：（这里支持json值）
        template.setKeySerializer(RedisSerializer.json());
        //哈希的key的序列化方式：
        template.setHashKeySerializer(RedisSerializer.string());
        //哈希的value的序列化方式：
        template.setHashValueSerializer(RedisSerializer.json());

        //触发才能生效
        template.afterPropertiesSet();
        return template;

    }
}
