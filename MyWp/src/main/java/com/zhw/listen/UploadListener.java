package com.zhw.listen;

import org.apache.commons.fileupload.ProgressListener;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听文件上传进度
 */

@Component
public class UploadListener implements ProgressListener {
    private HttpSession session;
    private String filesign;
    private String ordernum;
    public void setSession(HttpSession session){
        this.session=session;
    }
    public void setfilemes(String filesign,String ordernum){
        this.filesign = filesign;
        this.ordernum = ordernum;

    }
    @Override
    public void update(long pBytesRead,long pContentLength, int pItems) {
            //第一次上传文件时 直接就开始上传了文件  并没有给ordernum filesign 赋值  每次上传   这里会丢失一个文件块
            if(filesign!=null&&ordernum!=null){
                //存放文件上传进度的
                Map<String, ConcurrentHashMap<String,Long>> uploadwork = (Map) this.session.getAttribute("uploadwork");
                //存入进度中
                ConcurrentHashMap<String,Long> rs = uploadwork.get(filesign);
                if(rs!=null){   //会出现 rs为空情况
                   rs = new ConcurrentHashMap<>();
                }
                rs.put(ordernum,pBytesRead);
                uploadwork.put(filesign,rs);
                session.setAttribute("uploadwork",uploadwork);
            }
    }
}
