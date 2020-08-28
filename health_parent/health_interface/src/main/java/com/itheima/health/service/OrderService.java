package com.itheima.health.service;

import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Order;

import java.util.Map;

public interface OrderService {
    Order submitOrder(Map<String, String> orderInfo) throws HealthException;

    Map<String, String> findById(int id);
}
