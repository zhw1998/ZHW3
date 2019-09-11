package com.zhw.service;

import com.zhw.po.ShareFile;

public interface ShareFileService {
    int insert(ShareFile shareFile);

    ShareFile findById(Integer id);
}
