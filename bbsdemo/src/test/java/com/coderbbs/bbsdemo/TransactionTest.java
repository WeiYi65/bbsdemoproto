package com.coderbbs.bbsdemo;

import com.coderbbs.bbsdemo.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    //以下两个方法都是可以保证事务出错就回滚的。一般选择第一种，较为简单
    @Test
    public void testSave1(){
        Object obj = alphaService.save1();
        System.out.println(obj);
    }

    @Test
    public void testSave2(){
        Object obj = alphaService.save2();
        System.out.println(obj);
    }
}
