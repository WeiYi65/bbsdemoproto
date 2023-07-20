package com.coderbbs.bbsdemo;

import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    //生产大量数据
    public void initDataForTest(){
        for (int i = 0; i<300000; i++){
            DiscussPost discussPost = new DiscussPost();
            discussPost.setUserId(111);
            discussPost.setTitle("this is cache test post");
            discussPost.setContent("a sample post content");
            discussPost.setCreateTime(new Date());
            discussPost.setScore(Math.random()*2000);
            discussPostService.addDiscussPost(discussPost);
        }
    }

    @Test
    public void testCache(){
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 0));
    }
}
