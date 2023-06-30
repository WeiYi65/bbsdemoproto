package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.CommentMapper;
import com.coderbbs.bbsdemo.entity.Comment;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    //需要声明事务
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment==null){
            throw new IllegalArgumentException("Comment can not be empty.");
        }

        //过滤评论可能有的网页语法和敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            //每次都count现在帖子有多少评论
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }
}
