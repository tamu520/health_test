package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;

public interface CheckGroupDao {

    //添加检查组
    void add(CheckGroup checkGroup);

    //添加检查组的 多个选择项
    void addCheckGroupCheckItem(@Param("checkgroup_id") Integer checkgroup_id,@Param("checkitem_id")Integer checkitem_id);

    Page<CheckGroup> findByCondition(String queryString);
}
