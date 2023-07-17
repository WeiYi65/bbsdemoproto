package com.coderbbs.bbsdemo;

import com.coderbbs.bbsdemo.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.*;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class ThreadTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadTest.class);


    //JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);//意思是搞5个线程

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //注入spring线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //注入spring的定时任务线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;


    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //jdk普通线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello executor");
            }
        };

        for (int i = 0; i<10; i++){
            executorService.submit(task);//这样会随机分配一个线程给他

        }
        sleep(10000);//10s
    }

    //定时任务线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello schedule executor");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }

    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello spring executor");
            }
        };

        for (int i = 0; i<10; i++){
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    //spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello spring scheduler");
            }
        };

        Date startTime = new Date(System.currentTimeMillis()+10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(10000);
    }

    //简便的spring线程池使用方式：在任意bean里声明使用，示例在alpha service类里
    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for (int i = 0; i<10; i++){
            alphaService.execute1();
        }
        sleep(10000);
    }

    @Test
    //好像有方法在跑就会自动调用定时线程
    public void testThreadPoolTaskSchedulerSimple(){
        sleep(30000);
    }

}
