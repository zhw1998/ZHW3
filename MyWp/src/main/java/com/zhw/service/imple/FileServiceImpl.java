package com.zhw.service.imple;

import com.zhw.dao.FileDao;
import com.zhw.po.Cutfiles;
import com.zhw.po.Userfiles;
import com.zhw.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("fileService")
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDao fileDao;
    @Override
    public Cutfiles findCutfilesByMd5(String md5) {
        return fileDao.findCutfilesByMd5(md5);
    }

    @Override
    public Integer findUserFilesBySign(String filesign,Integer userid) {
        return fileDao.findUserFilesBySign(filesign,userid);
    }

    @Override
    public int insertUserFiles(Userfiles userfiles) {
        return fileDao.insertUserFiles(userfiles);
    }

    @Override
    public int insertUserCutFiles(Integer cid, Integer fid, int ordernum) {
        return fileDao.insertUserCutFiles(cid,fid,ordernum);
    }

    @Override
    public int insertCutFiles(Cutfiles cutfiles) {
        return fileDao.insertCutFiles(cutfiles);
    }

    @Override
    public List<Userfiles> getFiles(String currentpath, Integer uid) {
        return fileDao.getFiles(currentpath,uid);
    }

    @Override
    public List<String> getFolders(String currentpath, Integer uid) {
        return fileDao.getFolders(currentpath,uid);
    }

    @Override
    public Userfiles findUserFileById(Integer fid) {
        return fileDao.findUserFileById(fid);
    }

    @Override
    public List<String> findCutFPathByfid(Integer fid) {
        return fileDao.findCutFPathByfid(fid);
    }

    @Override
    public int updateUserFileState(Integer uid, Integer fid, int state) {
        return fileDao.updateUserFileState(uid,fid,state);
    }

    @Override
    public int updateUserFolderState(Integer uid, String foldname, int state) {
        return fileDao.updateUserFolderState(uid,foldname,state);
    }

    @Override
    public List<Userfiles> getFilesByType(List<String> list, Integer uid) {
        return fileDao.getFilesByType(list,uid);
    }

    @Override
    public List<Userfiles> getFilesLike(String like, Integer uid) {
        return fileDao.getFilesLike(like,uid);
    }
}
