package com.zhw.dao;

import com.zhw.po.User;
import org.apache.ibatis.annotations.Param;


public interface UserDao {
    //注册用户
    int insertUser(User user);

    User isRegisted(@Param("value") String value,@Param("type") String type);

    int updataPassword(@Param("usermail")String usermail,@Param("password") String password);
}
