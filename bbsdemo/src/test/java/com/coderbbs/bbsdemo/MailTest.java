package com.coderbbs.bbsdemo;

import com.coderbbs.bbsdemo.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;//将template里的email模板启用

    @Test
    public void testTextMail(){
        //mailClient.sendMail("xiaoshayeleng@gmail.com", "Test", "An email test. ");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "sunday");//这里会把字传给模板，模板的变量是username，所以这里也写username，sunday是用户名

        //会生成一个动态网页，也就是我们要发送的网页
        String content = templateEngine.process("/mail/maildemo.html", context);
        System.out.println(content);

        //这里是发送语句
        //mailClient.sendMail("xiaoshayeleng@gmail.com", "Welcome", content);
    }
}
