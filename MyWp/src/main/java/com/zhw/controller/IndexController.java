package com.zhw.controller;


import com.alibaba.fastjson.JSON;
import com.zhw.po.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {



    /**
     * 用于 网站用户登录之后的跳转 wangpan.jsp
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ModelAndView  todisk(HttpServletRequest request){
        HttpSession session = request.getSession();
        ModelAndView mav = new ModelAndView();
        if(session.getAttribute("user")!=null){
            mav.setViewName("wangpan");
        }else{
            mav.setViewName("redirect:/login.html");
        }
        return mav;
    }

    @RequestMapping(value = "/loginchat.action",method = RequestMethod.GET)
    @ResponseBody
    public  String loginchat(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Map<String,String>  map = new HashMap<>();
        map.put("uUID",user.getUuid());
        map.put("username",user.getUsername());
        map.put("usercode",user.getMail());
        map.put("websign","89bb4497-8f45-4c14-9851-9bbe3257c4c7");

        return JSON.toJSONString(map);
    }
}
