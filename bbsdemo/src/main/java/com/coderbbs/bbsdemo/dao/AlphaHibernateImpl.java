package com.coderbbs.bbsdemo.dao;

import org.springframework.stereotype.Repository;

@Repository("hib") //访问数据库的bean，就加这个。若数据库bean太多，可以取名，在这里加上("name")即可

//这里在自动装配测试要用的bean，会在test类里容器检查的时候出现
public class AlphaHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
