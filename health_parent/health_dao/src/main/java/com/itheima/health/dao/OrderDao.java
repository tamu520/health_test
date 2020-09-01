package com.itheima.health.dao;

import com.itheima.health.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    int findByCondition(Order order);

    void add(Order order);

    Map<String, Object> findById(int id);

    int findOrderCountByDate(String todayDate);

    int findVisitsCountByDate(String todayDate);

    int findOrderCountBetweenDate(Map<String, String> weekMap);

    int findVisitsCountAfterDate(Map<String, String> monthMap);

    List<Map<String, Object>> findHotSetmeal();
}
