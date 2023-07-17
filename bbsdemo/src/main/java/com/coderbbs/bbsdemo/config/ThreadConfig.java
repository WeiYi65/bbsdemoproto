package com.coderbbs.bbsdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync//后边这个两个是为了启用spring的定时线程池必须的配置。这个类就是为了spring的定时而生的
public class ThreadConfig {
}
