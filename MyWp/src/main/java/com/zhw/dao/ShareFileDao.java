package com.zhw.dao;

import com.zhw.po.ShareFile;

public interface ShareFileDao {
    int insert(ShareFile shareFile);

    ShareFile  findById(Integer id);
}
