package com.itheima.health.dao;

import com.itheima.health.pojo.User;

public interface UserDao {

    User findByUsername(String username);
}
