package com.coderbbs.bbsdemo;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.coderbbs.bbsdemo.dao.DiscussPostMapper;
import com.coderbbs.bbsdemo.dao.elasticsearch.DiscussPostRepository;
import com.coderbbs.bbsdemo.entity.DiscussPost;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQuery;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private RestClient restClient;

    @Test
    //每次只能插入一条数据
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostByPostId(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostByPostId(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostByPostId(243));
    }

    @Test
    //每次能插入多条数据，根据用户id创建
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    //也是可修改数据的，等于把之前的内容覆盖掉
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectDiscussPostByPostId(231);
        post.setContent("我是新人");
        discussPostRepository.save(post);
    }

    @Test
    //删除
    public void testDelete(){
        //discussPostRepository.deleteById(231);
        //discussPostRepository.deleteAll();
    }


    //es最核心的搜索功能
    @Test
    public void testMatchAll() throws IOException {
        RestClient restClient1 = RestClient.builder(new HttpHost("localhost", 9200)).build();
    }

}
