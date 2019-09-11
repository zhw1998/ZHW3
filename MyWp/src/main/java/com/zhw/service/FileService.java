package com.zhw.service;

import com.zhw.po.Cutfiles;
import com.zhw.po.Userfiles;

import java.util.List;
import java.util.Map;

public interface FileService {
    Cutfiles findCutfilesByMd5(String md5);

    Integer findUserFilesBySign(String filesign,Integer userid);

    int insertUserFiles(Userfiles userfiles);

    int insertUserCutFiles(Integer cid, Integer fid, int ordernum);

    int insertCutFiles(Cutfiles cutfiles);

    List<Userfiles> getFiles(String currentpath, Integer uid);

    List<String> getFolders(String currentpath, Integer uid);

    Userfiles findUserFileById(Integer fid);

    List<String> findCutFPathByfid(Integer fid);

    int updateUserFileState(Integer uid, Integer fid, int state);


    int updateUserFolderState(Integer uid, String foldname, int state);

    List<Userfiles> getFilesByType(List<String> list, Integer uid);

    List<Userfiles> getFilesLike(String like, Integer uid);
}
