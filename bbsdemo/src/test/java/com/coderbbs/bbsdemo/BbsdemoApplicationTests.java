package com.coderbbs.bbsdemo;

import com.coderbbs.bbsdemo.dao.AlphaDao;
import com.coderbbs.bbsdemo.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)// 启用bbsapp类作为配置类
class BbsdemoApplicationTests implements ApplicationContextAware {//得到spring容器，需额外写一个方法

	@Test
	void contextLoads() {
	}
	private ApplicationContext applicationContext;
	//记录这个application context的次数，和下面的方法是一起的
	@Override
	//得到spring容器的额外的方法。归属于application context接口,它的顶层接口是bean factory，不过这个子接口功能更多
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}


	@Test
	public void testApplicationContext(){
		//用于测试spring容器，直接把对象打印出来看，看它的值
		System.out.println(applicationContext);
		//它还要管理bean，因此需要创造一个bean。
		//这样调用接口的好处是调用方和实现类不会有耦合（这里是调用方）。如果以后想要更换实现类，
		//不必修改这里的调用代码，只需要在新的实现类开头加上@primary即可。
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		//有重复实现类时，想要调用已经不用的特定类，可以用类的名字指定类。因为默认返回object，所以需要用
		//get class方法返回类
		alphaDao = applicationContext.getBean("hib", alphaDao.getClass());
		System.out.println(alphaDao.select());
	}

	@Test
	//@Scope("prototype") //因为默认是生成单个实例，如果想要多个，就需要写上这个。每次访问bean都会创造新实例。一般单例就好
	public void testBeanManagement(){//测试bean管理的方式，这个bean在service中.
		//通过容器获取service
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);//获取bean

		System.out.println(alphaService);
	}

	@Test
	public void TestBeanConfig(){
		SimpleDateFormat simpleDateFormat =
				applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired //这里用的是依赖注入方法，和下面的test一起用
	@Qualifier("Hib")//这样可以指定bean
	private AlphaDao alphaDao;

	@Autowired //这样可以一次注入好几个，只需要一个test方法
	private SimpleDateFormat simpleDateFormat;

	@Autowired
	private AlphaService alphaService;

	@Test
	public void testDI(){//DI 是依赖注入的简写
		System.out.println(alphaDao);
		System.out.println(simpleDateFormat);
		System.out.println(alphaService);
	}
}
