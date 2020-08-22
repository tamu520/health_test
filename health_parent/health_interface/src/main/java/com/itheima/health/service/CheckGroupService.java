package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckGroup;

public interface CheckGroupService{
    void add(CheckGroup checkGroup,Integer[] checkitemId);

    PageResult<CheckGroup> findPage(QueryPageBean queryPageBean);
}
