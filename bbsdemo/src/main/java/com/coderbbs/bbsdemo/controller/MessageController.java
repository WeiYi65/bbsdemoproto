package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.Message;
import com.coderbbs.bbsdemo.entity.Page;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.MessageService;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表页面
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());

        //有很多私信数量要统计
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);

            }
        }
        model.addAttribute("conversations", conversations);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }


    //这里是单个私信的内容详情界面
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        //这里是查这个会话里面有多少条私信
        page.setRows(messageService.findLetterCount(conversationId));
        //这是选中的私信详情
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for (Message message:letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        //letter里面有发信人，而下边又写入了收信人。这里的消息应该是按时间一个一个装入的
        model.addAttribute("letters", letters);

        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        return "/site/letter-detail";

    }

    //这个方法封装的是这个私信的私信对象
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        //如果当前的用户是0，那么它的私信对象是1
        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else {//如果不是0，说明私信对象是0
            return userService.findUserById(id0);
        }
    }

}
