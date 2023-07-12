package com.coderbbs.bbsdemo.service;

import com.coderbbs.bbsdemo.dao.elasticsearch.DiscussPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
}
