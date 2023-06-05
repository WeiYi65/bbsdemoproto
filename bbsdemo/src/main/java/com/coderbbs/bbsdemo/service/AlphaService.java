package com.coderbbs.bbsdemo.service;
//这个包主要负责业务功能的处理和实现

import com.coderbbs.bbsdemo.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//内含容器的初始化、管理和销毁方法
public class AlphaService {

    @Autowired //注入Dao使service依赖dao.查询业务会用到dao。controller依赖service，而service依赖dao
    private AlphaDao alphaDao;

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

}
