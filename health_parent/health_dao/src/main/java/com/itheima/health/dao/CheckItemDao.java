package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    List<CheckItem> findAll();

    void add(CheckItem checkItem);

    //使用pageHelper分页,用Page类接收
    Page<CheckItem> findPage(QueryPageBean queryPageBean);
    Long getCount(QueryPageBean queryPageBean);

    void deleteById(Integer id);
    //删除前 检查外键表是否还有 关联的数据
    int findCountByCheckItemId(Integer id);
}
