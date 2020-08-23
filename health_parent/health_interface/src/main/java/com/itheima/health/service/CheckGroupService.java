package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService{
    void add(CheckGroup checkGroup,Integer[] checkitemId);

    PageResult<CheckGroup> findPage(QueryPageBean queryPageBean);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void update(CheckGroup checkGroup, Integer[] checkitemId);

    void deleteById(Integer id) throws HealthException;
}
