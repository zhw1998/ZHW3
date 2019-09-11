package com.zhw.controller;

import com.zhw.po.ShareFile;
import com.zhw.po.User;
import com.zhw.po.Userfiles;
import com.zhw.service.FileService;
import com.zhw.service.ShareFileService;
import com.zhw.service.UserService;
import com.zhw.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 另外一个controller写的太多了
 */

@RequestMapping(value = "file")
@Controller
public class dealFileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private ShareFileService shareFileService;

    /**
     *根据类型查找
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "file/getfilebytype.action",method = RequestMethod.GET)
    @ResponseBody
    public String getFileByTpye(String type, HttpServletRequest request){
        ResultBean rs = new ResultBean();
        List<String> list = TypeUtil.gettypeList(type);
        if(list.size()==0){
            rs.setCode(404);
            return rs.toJsonString();
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<Userfiles> userfiles = fileService.getFilesByType(list, user.getId());
        rs.setCode(200);
        rs.setResult(userfiles);
        return rs.toJsonString();
    }


    /**
     * 模糊查询文件名   文件夹暂时不实现
     * @param like
     * @param request
     * @return
     */
    @RequestMapping(value = "getfilelike.action",method = RequestMethod.GET)
    @ResponseBody
    public String getFileLike(String like,HttpServletRequest request){
        ResultBean rs = new ResultBean();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<Userfiles> files =  fileService.getFilesLike(like,user.getId());
        rs.setCode(200);
        rs.setResult(files);
        return rs.toJsonString();
    }


    /**
     * 文件分享
     * @param fid
     * @param time   有限期  one seven always
     * @param request
     * @return
     */
    @RequestMapping(value = "share.action",method = RequestMethod.POST)
    @ResponseBody
    public String shareFile(Integer fid,String time,HttpServletRequest request){
        ResultBean rs = new ResultBean();
        if(fid==null||time==null) {rs.setCode(302);}
        else {
            //验证文件是否属于该用户
            Userfiles userfiles = fileService.findUserFileById(fid);
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if(userfiles!=null&&userfiles.getUserId().equals(user.getId())){
                //根据time 和当前时间算出到期时间
                String overtime = getOverTime(time);
                //生成一个随机的提取码
                String code = GetCodeUtil.getCode();
                //插入数据返回id
                ShareFile shareFile = new ShareFile();
                shareFile.setfId(fid);
                shareFile.setDownCode(code);
                shareFile.setOverTime(overtime);
                int a = shareFileService.insert(shareFile);
                if(a!=0&&shareFile.getId()!=null) {
                    //将提取码和链接返回
                    StringBuffer sb = request.getRequestURL();

                    String link = sb.toString().replace("file/share.action","file/getshare.action?id="+String.valueOf(shareFile.getId()));
                    rs.setCode(200);
                    rs.setMesg(link);
                    rs.setResult(shareFile);
                }
            }else{
                rs.setCode(308);
            }
        }
        return rs.toJsonString();
    }

    /**
     * 获取分享的文件
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "getshare.action",method = RequestMethod.GET)
    public ModelAndView getShare(Integer id, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        //通过id获取sharefile
        ShareFile shareFile = shareFileService.findById(id);
        //判断是否过期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //没过期查询文件  返回文件信息
            Date now = new Date();
            Date date=df.parse(shareFile.getOverTime());
            if(now.before(date)){//有效期
                 Userfiles  userfiles = fileService.findUserFileById(shareFile.getfId());
                 if(userfiles.getState()!=0){ //没有被删除
                     mav.addObject(userfiles);
                     mav.addObject(shareFile);
                 }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mav.setViewName("share");
        return mav;
    }


    /**
     * 分享文件的下载
     * @param shareid
     * @return
     */
    @RequestMapping(value = "downshare.action",method = RequestMethod.GET)
    @ResponseBody
    public void downShare(Integer shareid, String downcode, HttpServletResponse response) throws IOException {
        ResultBean rs = new ResultBean();
        OutputStream out = response.getOutputStream();
        //通过id获取sharefile
        ShareFile shareFile = shareFileService.findById(shareid);
        try {
            //判断提取码是否正确

            if(!shareFile.getDownCode().equals(downcode)){
                out.write("<script type='text/javascript'> alert('提取码错误！')</script>".getBytes());
            }else{
                Userfiles  userfiles = fileService.findUserFileById(shareFile.getfId());
                if(userfiles.getState()!=0){ //没有被删除
                    //   下载
                    // 联合查询文件片段的路径 拼接查找文件片段顺序  order by ordernum
                    List<String> fpaths = fileService.findCutFPathByfid(userfiles.getId());
                    if(fpaths.size()>0){
                        response.setHeader("Content-Type", "application/octet-stream");
                        response.setContentType("multipart/form-data");
                        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(userfiles.getFileName(), "UTF-8"));
                        for(String path:fpaths){
                            //将每个文件片段写入 输出流中  /files/de268f1ab986ffb97c0496a20c14a488  需要将 /file/ 换成D://WPUpload/
                            path = path.replace(FileUploadConfig.WEB_PATH,FileUploadConfig.FILE_UPLOAD_PATH);
                            FileInputStream fin = new FileInputStream(new File(path));
                            byte[] bs = new byte[2048];
                            while (true){
                                int num = fin.read(bs);
                                if(num==-1) break;
                                out.write(bs,0,num);
                            }
                        }
                    }else{
                        //文件不存在 或出错
                        out.write("<script type='text/javascript'> alert('没有找到，或已损坏！')</script>".getBytes());
                    }
                }else{
                    out.write("<script type='text/javascript'> alert('文件已被删除！')</script>".getBytes());
                }
            }
        } catch (Exception e) {
            out.write("<script type='text/javascript'> alert('下载失败！')</script>".getBytes());
        }finally {
            out.flush();
            out.close();
        }

    }

    private String getOverTime(String time){
        if("always".equals(time)){
            return "9999-12-30 19:08:09";
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            if("one".equals(time)){
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                return sdf.format(calendar.getTimeInMillis());
            }else if("seven".equals(time)){
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
                return sdf.format(calendar.getTimeInMillis());
            }
        }
        return  "0000-00-00 19:08:09";
    }

}
