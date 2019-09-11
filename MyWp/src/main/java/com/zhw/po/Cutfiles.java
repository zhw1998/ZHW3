package com.zhw.po;

import java.io.Serializable;

public class Cutfiles implements Serializable {
    private Integer id;
    private String fileMd5;
    private String filePath;
    private Long cutSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getCutSize() {
        return cutSize;
    }

    public void setCutSize(Long cutSize) {
        this.cutSize = cutSize;
    }

}
