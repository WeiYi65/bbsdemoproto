package com.coderbbs.bbsdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration //普通配置类就用这个。配置类一般是借用别人造的轮子需要自己修改的时候
public class AlphaConfig {
    @Bean //意思是配置第三方的Bean，这是注解。
    public SimpleDateFormat simpleDateFormat(){//这是方法名，同时也是Bean的名字。Bean以方法命名
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//指定了一个格式
    }
}
