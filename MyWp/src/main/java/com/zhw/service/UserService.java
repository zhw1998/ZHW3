package com.zhw.service;

import com.zhw.po.User;

public interface UserService {
    int insertUser(User user);
    User isRegisted(String value, String type);

    int updataPassword(String usermail, String password);
}
