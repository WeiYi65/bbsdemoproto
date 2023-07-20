package com.coderbbs.bbsdemo.quartz;


import com.coderbbs.bbsdemo.entity.DiscussPost;
import com.coderbbs.bbsdemo.service.DiscussPostService;
//import com.coderbbs.bbsdemo.service.ElasticSearchService;
import com.coderbbs.bbsdemo.service.LikeService;
import com.coderbbs.bbsdemo.util.CommunityConstant;
import com.coderbbs.bbsdemo.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

//    @Autowired
//    private ElasticSearchService elasticSearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e+ "fail to initialize.");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        //先判断有没有人做了会让分数改变的操作
        if (operations.size()==0){
            //没有人做
            logger.info("task cancelled: no need to refresh");
            return;
        }

        logger.info("task started: refreshing "+operations.size());
        while (operations.size()>0){
            this.refresh((Integer) operations.pop());
        }
        logger.info("task finished: refreshing completed");

    }

    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostByPostId(postId);

        if (post==null){
            logger.error("post does not exist: post id: " + postId );
            return;
        }

        //是否精华
        boolean wonderful = post.getStatus()==1;
        int commentCount = post.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        //分数公式计算
        //权重计算
        double w = (wonderful? 75: 0) + commentCount*10 + likeCount*2;
        double score = Math.log10(Math.max(w, 1))//避免出现负数
                + (post.getCreateTime().getTime() - epoch.getTime())/(1000*3600*24);

        //更新帖子分数
        discussPostService.updateScore(postId, score);

        //同步搜索数据
        //elasticSearchService.saveDiscussPost(post);

    }
}
