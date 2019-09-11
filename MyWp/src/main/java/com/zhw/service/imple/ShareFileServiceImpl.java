package com.zhw.service.imple;

import com.zhw.dao.ShareFileDao;
import com.zhw.po.ShareFile;
import com.zhw.service.ShareFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sharaFileService")
public class ShareFileServiceImpl implements ShareFileService {
    @Autowired
    private ShareFileDao shareFileDao;
    @Override
    public int insert(ShareFile shareFile) {
        return shareFileDao.insert(shareFile);
    }

    @Override
    public ShareFile findById(Integer id) {
        return shareFileDao.findById(id);
    }
}
