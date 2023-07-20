package com.coderbbs.bbsdemo.dao;

import com.coderbbs.bbsdemo.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //可根据用户查看他发布的帖子，userid可要可不要，也要支持翻页的功能
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    //这个参数也是可要可不要的，但因为它只有一个参数，所以必须带注释，注释的另一个作用是给参数取别名（这里没变）
    int selectDiscussPostRows(@Param("userId") int userId);


    int insertDiscussPost(DiscussPost discussPost);

    //查看帖子详情方法
    DiscussPost selectDiscussPostByPostId(int id);

    //每次有回帖都要更新原贴的回复数量
    int updateCommentCount(int postId, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);

}
