package com.zhw.po;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id;
    private String uuid;
    private String username;
    private String password;
    private String registerTime;
    private String loginTime;
    private String headimgPath;
    private String mail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getHeadimgPath() {
        return headimgPath;
    }

    public void setHeadimgPath(String headimgPath) {
        this.headimgPath = headimgPath;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", registerTime='" + registerTime + '\'' +
                ", loginTime='" + loginTime + '\'' +
                ", headimgPath='" + headimgPath + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
