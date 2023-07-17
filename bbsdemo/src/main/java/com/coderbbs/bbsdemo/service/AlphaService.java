package com.coderbbs.bbsdemo.service;
//这个包主要负责业务功能的处理和实现

import com.coderbbs.bbsdemo.dao.AlphaDao;
import com.coderbbs.bbsdemo.dao.DiscussPostMapper;
import com.coderbbs.bbsdemo.dao.UserMapper;
import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.PrivateKey;
import java.util.Date;

@Service
//内含容器的初始化、管理和销毁方法
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired //注入Dao使service依赖dao.查询业务会用到dao。controller依赖service，而service依赖dao
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //构造器
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //初始化bean
    @PostConstruct //意思是这个方法会在构造器之后调用 初始化方法一般都如此。为了验证是否如此，可以做一个构造方法
    public void init(){
        System.out.println("Initialize the AlphaService.");
    }

    //销毁方法
    @PreDestroy //在销毁之前调用，可以释放某些资源
    public void destroy(){
        System.out.println("Destroy the AlphaService.");
    }

    //这里是依赖dao的方法
    public String find(){
        return alphaDao.select();
    }

    //测试事务用方法
    //在前方加入注解，方法内任何地方报错都会回滚
    //read committed是第二类宽松度
    //propa是传播机制，用于解决两个事务发生交叉时的问题。Required, required_new和nested是最常用的。
    //required是支持外部的事务，A调用B，那么用A。如果不存在A就创建A。对于被调用的事务而言，调用它的是外部事务
    //requirednew是创建一个新事务，并且暂停当前的外部事务。
    //nested是如果当前存在外部事务，则嵌套在这个事务中执行，有独立的提交和回滚，否则和required一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增一个帖子
        DiscussPost  discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("Hello");
        discussPost.setContent("Hi everybody!");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer.valueOf("abc");
        return "ok";
    }

    //1的另一种方法，比较复杂。也可以保证事务回滚
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                //新增用户
                User user = new User();
                user.setUsername("BETA");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增一个帖子
                DiscussPost  discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("Hello");
                discussPost.setContent("Hi everybody!");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                Integer.valueOf("abc");

                return "OK";
            }
        });
    }


    //简易使用spring线程池
    @Async//这个注解可以让该方法在多线程环境下被异步地调用
    public void execute1(){
        logger.debug("execute1");
    }

    //定时地调用注解演示
    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2(){
        logger.debug("execute2");
    }
}
