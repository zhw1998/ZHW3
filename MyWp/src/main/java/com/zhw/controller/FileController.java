package com.zhw.controller;

import com.zhw.po.Cutfiles;
import com.zhw.po.User;
import com.zhw.po.Userfiles;
import com.zhw.service.FileService;
import com.zhw.utils.FileUploadConfig;
import com.zhw.utils.ResultBean;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping(value = "file")
@RestController   //所有都返回json
public class FileController {

    @Autowired
    private FileService fileService;


    /**文件块 上传
     * @param file

     * @param fid       用户文件的id
     * @param ordernum  文件块顺序
     * @return
     */
    @RequestMapping(value = "upload.action",method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file,String fileMd5,String fileSign, Integer fid,Integer ordernum,HttpServletRequest request){
        ResultBean rs = new ResultBean();
        //参数的非空验证
        if(fileMd5==null||fileMd5.trim()==""||fid==null||ordernum==null||fileSign==null||fileSign==""){
            System.out.println("参数错误");
            rs.setCode(302);
            rs.setMesg("参数错误");
            return rs.toJsonString();
        }
        if(file.isEmpty()){
            rs.setCode(301);
            rs.setMesg("文件不能为空");
        }else{
            File fileup = new File(FileUploadConfig.FILE_UPLOAD_PATH);
            if(!fileup.exists()){
                fileup.mkdirs();
            }
            //文件上传
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(),new File(fileup,fileMd5));
                //文件块表插入数据
                Cutfiles cutfiles = new Cutfiles();
                cutfiles.setFileMd5(fileMd5);
                cutfiles.setFilePath(FileUploadConfig.WEB_PATH.concat(fileMd5));
                cutfiles.setCutSize(file.getSize());
                int a = fileService.insertCutFiles(cutfiles);
                if(a==0){
                    rs.setCode(303);
                    rs.setMesg("文件上传失败");
                    return rs.toJsonString();
                }
                //获取cutfileid
                int cid = cutfiles.getId();
//                System.out.println("文件片："+ordernum);
                //插入到用户和文件块关联表
                int b = fileService.insertUserCutFiles(cid,fid,ordernum);
                if(b==0){
                    rs.setCode(303);
                    rs.setMesg("文件上传失败");
                }else{
                    rs.setCode(200);
                    rs.setMesg("文件上传成功");
                    //设置该文件块
                    //存放文件上传进度的
                    HttpSession session = request.getSession();
                    Map<String, ConcurrentHashMap<String,Long>> uploadwork = (Map) session.getAttribute("uploadwork");
                    //存入进度中
                    ConcurrentHashMap<String,Long> map = uploadwork.get(fileSign);
                    if(map == null){
                        map = new ConcurrentHashMap<>();
                    }
                    map.put(String.valueOf(ordernum),file.getSize());
                    uploadwork.put(fileSign,map);
                    session.setAttribute("uploadwork",uploadwork);
                }
            } catch (IOException e) {
//                e.printStackTrace();
                rs.setCode(200);
                rs.setMesg(e.getMessage());
            }
        }
        return rs.toJsonString();
    }

    /**
     * 验证文件是否存在  如果以存在直接存入数据库 order 如果文件是切割后的话就需要一个序号
     */
    @RequestMapping(value = "isUploaded.action",method = RequestMethod.POST)
    public String isUploaded(Cutfiles cfile,Userfiles ufile,Integer order, HttpServletRequest request){
        ResultBean rs = new ResultBean();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //参数非空验证
        if(cfile==null|| ufile==null ||order==null ){
            System.out.println("参数错误");
            rs.setCode(302);
            rs.setMesg("参数错误");
            return rs.toJsonString();
        }

        //不管用户文件表是否存在该文件都需要插入一条上传文件的数据  这里会发生线程安全问题
        Integer fid = null;
        String filesign = ufile.getFileSign();
        String newfilesign =  user.getId().toString()+ufile.getFileSign();  //加上用户id 作为文件唯一id
        synchronized(newfilesign.intern()){
            //存放文件上传进度的  先创建 防止上传文件时出现并发问题重复创建
            Map<String,ConcurrentHashMap<String,Long>> uploadwork = (Map) session.getAttribute("uploadwork");
            if(uploadwork==null){
                uploadwork = new HashMap<String,ConcurrentHashMap<String,Long>>();
            }
            //存放进度信息到session
            ConcurrentHashMap<String,Long> map = uploadwork.get(filesign);
            if(map == null){
                //使用ConcurrentHashMap 多线程下修改HashMap会发生错误
                map = new ConcurrentHashMap<>();
            }

            fid = fileService.findUserFilesBySign(newfilesign,user.getId());
            if(fid==null){ //说明需要为这个文件存入了相关数据
                ufile.setUserId(user.getId());
                ufile.setFileSign(newfilesign);
                int a = fileService.insertUserFiles(ufile);
                if(a!=0){ //插入成功
                    fid = ufile.getId();
                }else{
                    rs.setCode(303);
                    rs.setMesg("文件上传失败");

                }
            }
            //后面不需要再插入数据进user_files中
            //通过md5查询文件id
            Cutfiles  cutfiles = fileService.findCutfilesByMd5(cfile.getFileMd5());
            //存在该文件块
            if(cutfiles!=null&&cutfiles.getId()!=null){
                    // 插入数据到关联表user_cut_files
                    int b = fileService.insertUserCutFiles(cutfiles.getId(),fid,order);
                    if(b==0){
                        rs.setCode(303);
                        rs.setMesg("文件上传失败");
                    }else{
                        rs.setCode(200);
                        rs.setMesg("文件上传成功");
                        map.put(String.valueOf(order),cfile.getCutSize());
                    }
//                System.out.println("存在该文件块"+filemd5);
            }else{
                //不存在
                rs.setCode(304);
                rs.setResult(fid);  //返回用户文件的id
                rs.setMesg("需要上传文件");
            }

            uploadwork.put(filesign,map);
            session.setAttribute("uploadwork",uploadwork);
        }
        //如果不存在则返回 要求前端上传文件
        return  rs.toJsonString();
    }


    /**
     * 新建文件夹
     * @param filepath
     * @param request
     * @return
     */
    @RequestMapping(value = "newfolder.action",method = RequestMethod.GET)
    public String newFolder(String filepath,HttpServletRequest request){
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("user");
        //直接插入数据库
        Userfiles userfiles = new Userfiles();
        userfiles.setFilePath(filepath);
        userfiles.setUserId(u.getId());
        userfiles.setFileName("");
        userfiles.setFileSign("");
        int a = fileService.insertUserFiles(userfiles);
        ResultBean rs = new ResultBean();
        if(a==0){
            rs.setCode(305);
            rs.setMesg("文件夹创建失败");
        }else{
            rs.setCode(200);
            rs.setMesg("文件夹创建成功");
        }
        return rs.toJsonString();
    }


    /**
     * 获取当前路径下的所有文件
     * @param currentpath
     * @return
     */
    @RequestMapping(value = "getfiles.action",method = RequestMethod.GET)
    public String getFilesBypath(String currentpath,HttpServletRequest request){
        ResultBean rs = new ResultBean();
        Map<String,Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //获取文件
        List<Userfiles> files = fileService.getFiles(currentpath,user.getId());
        map.put("file",files);
        //获取文件夹
        List<String>  folders = fileService.getFolders(currentpath,user.getId());
        //对获取到的文件夹集合做处理 set 去重
        if(folders.size()>0){
            Set<String> folderlist = getfolds(folders,currentpath);
            map.put("folder",folderlist);
        }
        if(files.size()==0&&folders.size()==0){
            rs.setCode(404);
            rs.setMesg("没有任何文件");
        }else{
            rs.setCode(200);
            rs.setResult(map);
        }
        return rs.toJsonString();
    }


    /**
     * 文件下载
     * @param fid
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "downfile.action",method = RequestMethod.GET)
    public void downFile(Integer fid, HttpServletResponse response,HttpServletRequest request) throws IOException {
        //根据文件的id 检验用户和文件的合法性  如果是本人可以下载  如果是他人 查看该文件是否开放的下载权限  state = 3
        Userfiles file = fileService.findUserFileById(fid);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //这里只判断本人
        OutputStream out = response.getOutputStream();
        if(  user.getId().equals(file.getUserId()) ){
            //可以下载
            // 联合查询文件片段的路径 拼接查找文件片段顺序  order by ordernum
               List<String> fpaths = fileService.findCutFPathByfid(fid);
               if(fpaths.size()>0){
                   response.setHeader("Content-Type", "application/octet-stream");
                   response.setContentType("multipart/form-data");
                   response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(file.getFileName(), "UTF-8"));
                   for(String path:fpaths){
                       //将每个文件片段写入 输出流中  /files/de268f1ab986ffb97c0496a20c14a488  需要将 /file/ 换成D://WPUpload/
                       path = path.replace(FileUploadConfig.WEB_PATH,FileUploadConfig.FILE_UPLOAD_PATH);
                       System.out.println(path);
                       FileInputStream fin = new FileInputStream(new File(path));
                       byte[] bs = new byte[1024];
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
            //没有权限
            out.write("<script type='text/javascript'> alert('没权限!')</script>".getBytes());
        }
        out.flush();
        out.close();
    }

    /**
     * 删除文件
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "deletefile.action",method = RequestMethod.GET)
    public String deleteFile(String type,HttpServletRequest request){
        ResultBean rs = new ResultBean();
        if(type==null||type==""){
            rs.setCode(302);
            rs.setMesg("参数错误");
            return rs.toJsonString();
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //删除  只改变state为 0  可以定期删除用户的文件信息表数据  比如10天
        if("file".equals(type)){//删除文件
            Integer fid = Integer.valueOf(request.getParameter("fid"));
            fileService.updateUserFileState(user.getId(),fid,0);
        }else if("folder".equals(type)){//删除文件
            String foldname = request.getParameter("foldname");
            fileService.updateUserFolderState(user.getId(),foldname,0);
        }
        rs.setCode(200);
        rs.setMesg("文件删除成功");
        return rs.toJsonString();
    }

    /**
     * 获取文件上传进度
     * @return
     */
    @RequestMapping(value = "uploadspeed.action",method = RequestMethod.GET)
    public String uploadSpeed(HttpServletRequest request){
        ResultBean rs = new ResultBean();
        HttpSession session = request.getSession();
        Map<String,ConcurrentHashMap<String,Long>> map = (Map) session.getAttribute("uploadwork");
        if(map!=null&&map.size()>0){
            rs.setCode(200);
        }else{
            rs.setCode(307);
        }
        rs.setResult(map);
        return rs.toJsonString();
    }

    /**
     * 获取文件夹 去重操作
     * @param folders
     * @param currentpath
     * @return
     */
    private Set<String> getfolds(List<String> folders,String currentpath) {

        Set<String> set = new HashSet<>();
        for (String s:folders){
            //获取去除当前路径剩下的路径
            String cut1 = s.substring(currentpath.length(),s.length());
            //获取出现第一个/之前的路径 就是文件夹
            String path = cut1.substring(0,cut1.indexOf("/"));
            set.add(path);
        }
        return set;
    }


}
