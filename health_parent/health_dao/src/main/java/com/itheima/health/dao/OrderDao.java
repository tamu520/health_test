package com.itheima.health.dao;

import com.itheima.health.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    int findByCondition(Order order);

    void add(Order order);

    Map<String, String> findById(int id);
}
