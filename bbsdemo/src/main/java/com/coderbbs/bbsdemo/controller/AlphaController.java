package com.coderbbs.bbsdemo.controller;

import com.coderbbs.bbsdemo.service.AlphaService;
import com.coderbbs.bbsdemo.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")//这是个路径的声明
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    // 在springMVC的框架下获得请求、响应对象（处理请求和响应）
    @RequestMapping("http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //这里的request和response都是用的比较底层的老方法，会比较麻烦
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());//获取请求路径
        Enumeration<String> enumeration = request.getHeaderNames(); //一个很老的迭代器，不推荐使用了
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }

        System.out.println(request.getParameter("code"));

        //下面是response
        //返回数据的类型
        response.setContentType("text/html; charset=utf-8");
        try(PrintWriter writer = response.getWriter();) {//在这里创建的好处是不需要再写close了
            //获取一个输出流,默认需要抛出异常
            writer.write("<h1>web bbs</h1>");//在网页上写标题

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    //这里使用的是新的封装方法，请求和响应数据
    //get请求（默认就是get请求）
    //student?current=1&limit=20 这是给服务器传参，意思是传入student，?代表后面的条件，&连接不同条件
    @RequestMapping(path = "student", method = RequestMethod.GET)//这样就只能处理get请求，缩小漏洞的可能性
    @ResponseBody
    public String getStudents(//保证和传过来的参数一致。这里我们可以使用更具体的注解.required = false指的是可以不传这个参
                               @RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "10")int limit){
        System.out.println(current);//这个出现在程序的输出台
        System.out.println(limit);
        return "some students";//这个出现在网页上
    }

    //这个和上面的区别是，上面的路径是带问号的参数，这里是直接把这些东西变成路径的一部分
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){//这个注解就是用来变成路径的一部分的
        System.out.println(id);
        return "a student";
    }


    //post请求(get请求也可以提交数据，但是十分有限，所以一般用post请求来收取用户提交的数据）
    //这里会链接到写好的student.html里
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应一个动态的html数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "Yi");
        mav.addObject("age", 30);
        mav.setViewName("/demo/view");//模板的路径名，在templates文件下，view指的是view.html
        return mav;
    }

    //第二种响应方式，比上面的略简单一些,效果一样的，第二种多
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "McMaster University");
        model.addAttribute("age", 100);
        return "/demo/view";
    }

    //响应JSON数据（一般是在异步请求当中）异步请求：当前网页不刷新，但是访问了数据库来判断（比如判断新用户的id是否已被占用）
    //假设返回java对象、js对象，JSON可以把两种对象变成通用的
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "Bob");
        emp.put("age", "30");
        emp.put("salary", 8000.00);
        return emp;
    }//会自动转成JSON字符串发送给浏览器

    //制作一个数组
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "Bob");
        emp.put("age", 30);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "Alice");
        emp.put("age", 20);
        emp.put("salary", 6000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "David");
        emp.put("age", 40);
        emp.put("salary", 9000.00);
        list.add(emp);

        return list;
    }
    //cookie示例.这是用户向服务器请求一个cookie，但cookie只能存小量的字符串
    @RequestMapping(path="/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建一个cookie对象
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效范围
        cookie.setPath("/community/alpha");
        //cookie默认存在浏览器内存里，如果浏览器关了就没了。如果需要更久的保存，要设置保存时间
        cookie.setMaxAge(60*10);//单位是秒，这里意思是十分钟
        //发送cookie，放到response的头里
        response.addCookie(cookie);
        return "set cookie";
    }

    //获得浏览器给服务端的cookie,这里东西会输出在控制台
    @RequestMapping(path="/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){//这里获得之前set cookie的时候生成的code来判断得到的cookie
        System.out.println(code);
        return "get cookie";
    }

    //和cookie类似的session，但是session可以存很多数据、任何类型的数据，因为它固定存储在服务器中
    @RequestMapping(path="/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        //springmvc可以自动创建session然后注入
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    //获得session
    @RequestMapping(path="/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //AJAX示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAJAX(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "Operated successfully!");
    }

}
