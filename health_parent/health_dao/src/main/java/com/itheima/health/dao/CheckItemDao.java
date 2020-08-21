package com.itheima.health.dao;

import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    List<CheckItem> findPage(QueryPageBean queryPageBean);
    Long getCount(QueryPageBean queryPageBean);
}
