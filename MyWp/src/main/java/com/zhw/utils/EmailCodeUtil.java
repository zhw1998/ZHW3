package com.zhw.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * 发送验证码
 */
public class EmailCodeUtil{
    private static String sendmail = "邮箱账号 可以用163";
    private static String sendmailpassword = "密码";

    public static boolean sendMail(String recivedmail,String code){
        Properties pro = new Properties();
        pro.put("mail.host","smtp.163.com");
        pro.put("mail.transport.protocol","smtp");
        pro.put("mail.smtp.auth", true);
        //创建session
        Session session = Session.getInstance(pro);
        //开始session的调试模式 可以查看邮件发送状态
//        session.setDebug(true);
        //获取transport对象   发送邮件的核心api
        // SSL加密
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        // 设置信任所有的主机
            sf.setTrustAllHosts(true);
            pro.put("mail.smtp.ssl.enable", "true");
            pro.put("mail.smtp.ssl.socketFactory", sf);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        Transport transport = null;
        try {
            transport = session.getTransport();
            //邮箱的用户名密码
            transport.connect(sendmail,sendmailpassword);
            //创建邮件
            Message message = getMessage(session,recivedmail,code);
            //发送邮件
            transport.sendMessage(message,message.getAllRecipients());
        }catch (MessagingException e){
            return false;
        }finally {
            try {
                transport.close();
            }catch (MessagingException e){

            }
        }
        return true;
    }

    private static MimeMessage getMessage(Session session,String recivedmail,String code) throws MessagingException {
        //创建邮箱对象
        MimeMessage mimeMessage = new MimeMessage(session);
        //设置发件人
        mimeMessage.setFrom(new InternetAddress(sendmail));
        //设置收件人
        mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(recivedmail));
        //标题
        mimeMessage.setSubject("小胖网盘验证码");
        StringBuilder sb = new StringBuilder();
        sb.append("您的验证码为：");
        sb.append(code);
        sb.append(" 。有效时间5分钟，给别人看打死你哦！！！");
        mimeMessage.setContent(sb.toString(),"text/html;charset=utf-8");
        return mimeMessage;
    }
}
