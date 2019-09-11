package com.zhw.controller;

import com.qiniu.util.Md5;
import com.zhw.po.User;
import com.zhw.service.UserService;
import com.zhw.utils.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.rsa.RSASignature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = "user")
public class UserController {


    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @return
     */
    @RequestMapping(value = "register.action",method = RequestMethod.POST)
    @ResponseBody
    public String register(String username, String usemail, String password, String code, HttpServletRequest request){
        ResultBean rs = new ResultBean();
        //基本注册信息验证
        if(!check(username,usemail,password,code)){
            rs.setCode(201);
            rs.setMesg("输入信息不符");
            return rs.toJsonString();
        }
        //判断是否已经注册 双重验证
        User a1 = userService.isRegisted(username,"username");
        if(a1!=null){
            rs.setCode(204);
            rs.setMesg("该用户名已被注册");
            return rs.toJsonString();
        }
        User a2 = userService.isRegisted(usemail,"mail");
        if(a2!=null){
            rs.setCode(204);
            rs.setMesg("该邮箱已被注册");
            return rs.toJsonString();
        }
        //获取session 中的code 验证是否正确
        HttpSession session = request.getSession(false);
        if(session == null||code==null){    //没有发送过验证码
            rs.setCode(202);
            rs.setMesg("验证码错误");
        } else{
            if(code.equals((String) session.getAttribute(usemail))){
                //注册
                User user = new User();
                user.setUuid(UUID.randomUUID().toString());
                user.setUsername(username);
                //md5密码加密
                String md5password = Md5.md5(password.getBytes());
                user.setPassword(md5password);
                user.setMail(usemail);

                int r = userService.insertUser(user);
                System.out.println(r);
                if(r==0){
                    rs.setCode(203);
                    rs.setMesg("注册失败");
                }else{
                    rs.setCode(200);
                    rs.setMesg("注册成功");
                    //存入session
                    session.setAttribute("user",user);
                }
            }else{
                rs.setCode(202);
                rs.setMesg("验证码错误");
            }
        }
        return rs.toJsonString();
    }

    /**
     * 判断用户名和邮箱是否已注册
     * @param value  邮箱或用户名值
     * @param type   username 代表用户名   mail 代表邮箱
     * @return
     */
    @RequestMapping(value ="check.action",method = RequestMethod.GET)
    @ResponseBody
    public String  isRegisted(String value,String type){
        ResultBean rs = new ResultBean();
        value = value.trim();
        type = type.trim();
        if(value == ""||value == null||type == ""||type == null)  return null;
        User a = userService.isRegisted(value,type);
        if(a==null){
            //没有被注册
            rs.setCode(200);
            rs.setMesg("可以注册");
        }else{
            rs.setCode(204);
            rs.setMesg("已被注册");
        }
        return rs.toJsonString();
    }



    /**
     * 用户登录   邮箱登录  用户名的登录
     * @return
     */
    @RequestMapping(value = "login.action",method = RequestMethod.POST)
    @ResponseBody
    public String login(String code,String password,HttpSession session){
        ResultBean rs = new ResultBean();
        rs.setCode(200);
        rs.setMesg("登录成功");
        //登录方式可能为  username + password  mail+password
        User userbyname = userService.isRegisted(code,"username");
        String passwordmd5 = Md5.md5(password.getBytes());
        if (userbyname!=null&&userbyname.getPassword().equals(passwordmd5)){
            userbyname.setPassword("");
            session.setAttribute("user",userbyname);

            rs.setResult(userbyname);
        }else{
            User userbymail = userService.isRegisted(code,"mail");
            if(userbymail!=null&&userbymail.getPassword().equals(passwordmd5)){
                userbymail.setPassword("");
                session.setAttribute("user",userbymail);
                rs.setResult(userbymail);
            }else{
                rs.setCode(205);
                rs.setMesg("登录失败，账号或密码错误");
            }
        }
        return rs.toJsonString();
    }


    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @RequestMapping(value = "logout.action",method = RequestMethod.GET)
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }


    @RequestMapping(value = "updatepassword.action",method = RequestMethod.POST)
    @ResponseBody
    public String updatePassword(String code,String usermail,String password,HttpServletRequest request){
        ResultBean rs = new ResultBean();
        //获取session 中的code 验证是否正确
        HttpSession session = request.getSession(false);
        String thiscode = (String) session.getAttribute(usermail);
        if(session == null||thiscode==null){    //没有发送过验证码
            rs.setCode(202);
            rs.setMesg("验证码错误");
        } else{
            if(code.equals(thiscode)){
                 //修改密码
                password =   Md5.md5(password.getBytes());
               int a =  userService.updataPassword(usermail,password);
               if(a==0){
                   rs.setCode(206);
                   rs.setMesg("修改密码失败");
               }else{
                   rs.setCode(200);
                   rs.setMesg("成功");
               }
            }else{
                rs.setCode(202);
                rs.setMesg("验证码错误");
            }
        }
        return rs.toJsonString();
    }



    /**
     * 用户注册参数格式验证
     * @param username
     * @param usemail
     * @param password
     * @param code
     * @return
     */
    private boolean check(String username, String usemail, String password, String code){
        
        //非空判断
        if(username==null||username==""||usemail==null||usemail==""||password==null||password==""||code==null||code=="") return false;
        //用户名格式判断  中文结合 长度3-10
        if(username.length()<3||username.length()>10) return  false;
        //密码8-14字符  不允许空格
        if(password.length()<8||password.length()>14||password.indexOf(" ")!=-1)return false;
        //不允许中文
        Pattern p =  Pattern.compile("[\u4E00-\u9FA5]");
        Matcher matcher = p.matcher(password);
        if(matcher.find()) return false;
        //邮箱可不做判断
        return true;
    }




}
