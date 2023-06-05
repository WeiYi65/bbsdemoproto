package com.coderbbs.bbsdemo.dao;

import com.coderbbs.bbsdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
//不要忘记添加注解让spring和mybatis来装配这个bean（如果没有mybatis参与就用repository，否则可以用mapper
@Mapper
public interface UserMapper {

    //这里的每个方法都要连接上他所需要的sql，因此在resource处创建一个xml文件来实现这个指引.在xml文件中指向这个接口
    //使用用户信息查找user
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);

    //加入新用户
    int insertUser(User user);

    //修改已有数据
    int updateStatus(int id, int status);
    int updateHeader(int id, String headerUrl);
    int updatePassword(int id, String password);

}
