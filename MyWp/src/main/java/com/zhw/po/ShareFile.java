package com.zhw.po;

import java.io.Serializable;

public class ShareFile implements Serializable {
    private Integer id;
    private Integer fId;
    private String downCode;
    private String overTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getfId() {
        return fId;
    }

    public void setfId(Integer fId) {
        this.fId = fId;
    }

    public String getDownCode() {
        return downCode;
    }

    public void setDownCode(String downCode) {
        this.downCode = downCode;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    @Override
    public String toString() {
        return "SharaFile{" +
                "id=" + id +
                ", fId=" + fId +
                ", downCode='" + downCode + '\'' +
                ", overTime='" + overTime + '\'' +
                '}';
    }
}
