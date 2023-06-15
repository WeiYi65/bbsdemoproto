package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.annotation.LoginRequired;
import com.coderbbs.bbsdemo.entity.User;
import com.coderbbs.bbsdemo.service.UserService;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import com.coderbbs.bbsdemo.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //注入配置里的信息
    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path="/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //修改头像
    @LoginRequired
    @RequestMapping(path="/upload", method = RequestMethod.POST)
    //上传东西的时候表单的提交方式必须为post
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error", "There is no image.");
            return "/site/setting";
        }

        //为了不重复文件名要重命名
        //先判断文件是否为空
        String fileName = headerImage.getOriginalFilename();
        //再判断文件格式是否合理
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "The file format is not correct. ");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID()+suffix;
        //确认文件存放路径
        File dest = new File(uploadPath+"/"+fileName);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("Upload failed: " + e.getMessage());
            throw new RuntimeException("Upload failed, server error.");
        }

        //走到这说明头像无误，可以更新头像路径了（外部访问路径）
        //http://localhost:8080/community/user/header/xxx.png

        //先获取当前用户是谁
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "/site/setting";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    //只是获取头像路径所以是get
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse httpServletResponse){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        //解析后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        httpServletResponse.setContentType("image/"+suffix);
        try (FileInputStream fis = new FileInputStream(fileName);
             OutputStream os = httpServletResponse.getOutputStream();){//这样的话输入流会自己关闭

            byte[] buffer = new byte[1024];//是缓冲，意思是1b1b地输出
            int b = 0;//这个是游标
            while ((b = fis.read(buffer)) != -1) {//读到-1说明是最后了
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("Fail to read the picture: " + e.getMessage());
        }
    }

    //修改密码
    @LoginRequired
    @RequestMapping(path="/reset", method = RequestMethod.POST)
    public String resetPassword(String oldPassword, String newPassword, String newPassword2, Model model){
        User user = hostHolder.getUser();
        System.out.println(model);

        //旧密码不正确
        if(!userService.oldPassword(user.getId(), oldPassword)){
            model.addAttribute("errorMsg", "The old password is not correct.");
            return "/site/setting";
        }
        System.out.println("try");

        //新密码为空
        if(newPassword==null){
            model.addAttribute("nullMsg", "There is no password.");
            return "/site/setting";
        }

        //两次新密码不一致
        if(!newPassword.equals(newPassword2)){
            model.addAttribute("diffMsg", "The two passwords are not the same.");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(), newPassword);

        return "/site/setting";
    }


}
