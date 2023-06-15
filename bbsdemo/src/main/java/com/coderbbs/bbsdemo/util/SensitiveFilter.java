package com.coderbbs.bbsdemo.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //敏感词替换用符号
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    //初始化方法，当容器实例化这个bean以后，在构造器之后这个方法被自动调用
    public void init(){
        //把写在resource文件夹下的敏感词txt加入到target.classes。templates包下
        //类加载器
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitivewords.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while ((keyword = reader.readLine()) != null){
                //把敏感词添加到前缀树
                this.addKeyword(keyword);
            }
        } catch(IOException e){
            logger.error("Fail to load sensitivewords.txt: "+ e.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i = 0; i<keyword.length(); i++){
            char c = keyword.charAt(i);
            //确认一下之前是否挂过相同的子节点
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);//给他挂上新的子节点
            }
            //让指针指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if (i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     *
     * @param text 待过滤的文本
     * @return 已过滤的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1（树的指针）
        TrieNode tempNode = rootNode;
        //指针2（字符串指向敏感词开头的指针）
        int begin = 0;
        //指针3（指向敏感词结尾的指针）
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if(isSymbol(c)){
                //如果指针1处于根节点，意思是这个字符串开始就是符号，那不用判断敏感词，直接将此符号计入结果，让指针2向下走一步（开始查下一个字）
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                //不论符号在开头还是中间，指针3都向下走
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode==null){
                //意思是指针2现在指着的字符不是敏感词的开头，直接存入
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点（以便下一个字查找字符树）
                tempNode = rootNode;
            }else if (tempNode.isKeywordEnd){
                //发现敏感词，将begin到position的字符串替换
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }

        //将最后一批字符存入
        sb.append(text.substring(begin));

        return sb.toString();
    }


    //判断是不是符号
    private boolean isSymbol(Character c){//后半的意思是在东亚文字的范围之外
        return (!CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80 || c>0x9FFF));
    }


    //inner class，不允许外界访问.
    //前缀树子节点生成
    private class TrieNode{
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点,char是下级字符（key），node是下级节点（value）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }



}
