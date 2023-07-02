package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.entity.Message;
import com.coderbbs.bbsdemo.entity.Page;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.MessageService;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

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
        //这是选中的私信详情列表，里面应该包含了所有的私信
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

        //用户阅读完后把未读的私信设置成已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

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

    //得到未读消息的id
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if (letterList != null){
            //那么进行遍历
            for (Message message:letterList){
                //只有当前用户是接受者时才会有未读消息
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() ==0){
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    //发送私信，因为是异步所以要加body
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        //通过用户名查询id
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1, "The target user is not exist!");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());//从发送的人里取到他的id
        message.setToId(target.getId());
        //拼会话id
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        message.setContent(content);
        message.setCreateTime(new Date());

        //这些都准备好后直接新增
        messageService.addMessage(message);
        //如果未报错则返回状态0，若报错将来统一解决异常
        return CommunityUtil.getJSONString(0);
    }

}
