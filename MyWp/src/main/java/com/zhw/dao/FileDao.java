package com.zhw.dao;

import com.zhw.po.Cutfiles;
import com.zhw.po.Userfiles;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FileDao {
    //通过md5查询文件块
    Cutfiles findCutfilesByMd5(String md5);
    //file_sign  查找用户文件 返回fid
    Integer findUserFilesBySign(@Param("fileSign") String filesign,@Param("userid")  Integer userid);
    //插入用户文件记录
    int insertUserFiles(Userfiles userfiles);
    //添加用户文件和 文件块 的关联记录
    int insertUserCutFiles(@Param("cid")Integer cid,@Param("fid") Integer fid,@Param("ordernum") int ordernum);
    //插入文件块记录
    int insertCutFiles(Cutfiles cutfiles);
    //获取文件  通过 当前目录
    List<Userfiles> getFiles(@Param("currentpath")String currentpath,@Param("uid") Integer uid);
    //获取文件夹  通过 当前目录
    List<String> getFolders(@Param("currentpath")String currentpath,@Param("uid") Integer uid);
    //通过fid查找用户文件记录
    Userfiles findUserFileById(Integer fid);
    //通过fid 联合查询文件块路径
    List<String> findCutFPathByfid(Integer fid);
    //更新用户文件的状态值
    int updateUserFileState(@Param("uid") Integer uid,@Param("fid")Integer fid,@Param("state") int state);

    int updateUserFolderState(@Param("uid")Integer uid,@Param("foldname") String foldname, @Param("state")int state);

    List<Userfiles> getFilesByType(@Param("types")List<String> list,@Param("uid") Integer uid);

    List<Userfiles> getFilesLike(@Param("like")String like,@Param("uid") Integer uid);
}
