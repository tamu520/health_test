package com.itheima.health.service;


import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    PageResult<Setmeal> findPage(QueryPageBean queryPageBean);

    void add(Setmeal setmeal, Integer[] checkgroupIds);

    Setmeal findById(Integer id);

    List<Integer> findCheckgroupIdsBySetmealId(Integer id);

    void update(Setmeal setmeal,Integer[] checkgroupIds);

    void deleteById(int id) throws HealthException;

    List<String> findImgs();

    List<Setmeal> findAll();

    Setmeal findDetailById(int id);

    List<Map<String,Object>> findSetmealCount();
}
