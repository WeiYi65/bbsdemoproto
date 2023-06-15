package com.coderbbs.bbsdemo;

import com.coderbbs.bbsdemo.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "HERE IS ABLE TO abuse, gamble, pro, stitution";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

    }
}
