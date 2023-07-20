package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.DiscussPostMapper;
import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.annotations.Mapper;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;


    //caffeine的核心接口：cache，子接口：loading cache，AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if (key==null || key.length()==0){
                            throw new IllegalArgumentException("parameters can not be null.");
                        }

                        String[] param = key.split(":");
                        if (param==null || param.length!=2){
                            throw new IllegalArgumentException("parameters wrong.");
                        }

                        int offset = Integer.valueOf(param[0]);
                        int limit = Integer.valueOf(param[1]);

                        //可以在这里增加一个二级缓存：Redis->mysql

                        //如果都没有则访问数据库并且记日志
                        logger.debug("load post list from db.");

                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });

        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        //查询之前记日志
                        logger.debug("load post rows from db.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });

    }


    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode){
        if (userId==0 && orderMode==1){
            //意思是只缓存热门列表的帖子
            return postListCache.get(offset+":"+limit);
        }
        //访问数据库时记录一次日志
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
        if (userId==0){
            //这时候访问的是首页的帖子
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if(post==null){
            throw new IllegalArgumentException("The post can not be empty.");
        }

        //处理post。敏感词+html脚本语言类过滤
        //处理post的title
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostByPostId(int id){
        return discussPostMapper.selectDiscussPostByPostId(id);
    }

    public int updateCommentCount(int postId, int commentCount){
        return discussPostMapper.updateCommentCount(postId, commentCount);
    }

    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
