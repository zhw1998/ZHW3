package com.zhw.service.imple;

import com.zhw.dao.UserDao;
import com.zhw.po.User;
import com.zhw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public int insertUser(User user) {
        return userDao.insertUser(user);
    }

    @Override
    public User isRegisted(String value, String type) {
        return userDao.isRegisted(value,type);
    }

    @Override
    public int updataPassword(String usermail, String password) {
        return userDao.updataPassword(usermail,password);
    }
}
