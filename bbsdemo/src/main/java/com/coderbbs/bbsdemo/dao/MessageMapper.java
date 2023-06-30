package com.coderbbs.bbsdemo.dao;

import com.coderbbs.bbsdemo.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询当前用户的私信列表，并且对每个私信会话返回最新一条的私信内容
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的所有私信内容
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询单个会话的未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);



}
