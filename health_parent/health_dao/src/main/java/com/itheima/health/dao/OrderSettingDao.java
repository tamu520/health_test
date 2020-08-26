package com.itheima.health.dao;

import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao{
    OrderSetting findByOrderDate(Date orderDate);

    void add(OrderSetting orderSetting) throws HealthException;

    void updateNumber(OrderSetting orderSetting);

    List<Map<String, Integer>> getOrderSettingByMonth(Map<String, String> map);
}
