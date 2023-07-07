package com.coderbbs.bbsdemo.dao;

import com.coderbbs.bbsdemo.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated //意思是已经不推荐使用了
public interface LoginTicketMapper {
    //增删改方法通常会返回操作的行数，sql每句话后面最好加个空格
    @Insert({
            "insert into login_ticket (user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")//自动生成sql语句，并指定注入的属性
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //也可以加入if条件语句进行动态选择，前提是要把这个改成脚本模式
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
