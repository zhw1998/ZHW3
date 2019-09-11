package com.zhw.utils;


import com.alibaba.fastjson.JSON;

/**
 * 响应结果集
 */
public class ResultBean {
    private int code;
    private String mesg;
    private Object result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMesg() {
        return mesg;
    }

    public void setMesg(String mesg) {
        this.mesg = mesg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


    public String toJsonString() {
        return  JSON.toJSONString(this);
    }
    @Override
    public String toString() {
        return "ResultBean{" +
                "code=" + code +
                ", mesg='" + mesg + '\'' +
                ", result=" + result +
                '}';
    }
}
