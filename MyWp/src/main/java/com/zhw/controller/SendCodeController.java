package com.zhw.controller;

import com.zhw.utils.EmailCodeUtil;
import com.zhw.utils.GetCodeUtil;
import com.zhw.utils.ResultBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 验证码处理
 */
@Controller
public class SendCodeController {

    @RequestMapping(value = "code/getcode.action",method = RequestMethod.POST)
    @ResponseBody
    public String getcode(String mail, HttpServletRequest request){
        //获取一个验证码
        String code = GetCodeUtil.getCode();
        //发送一个邮件
        boolean b = EmailCodeUtil.sendMail(mail,code);
        ResultBean rs = new ResultBean();
        if(b){
            //将session存入session中
            HttpSession session = request.getSession();
            session.setAttribute(mail,code);
            rs.setCode(200);
            rs.setMesg("验证码发送成功！");
        }else{
            rs.setCode(202);
            rs.setMesg("验证码发送失败");
        }

        return  rs.toJsonString();
    }


}
