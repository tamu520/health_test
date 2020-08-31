package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface SetmealDao {
    Page<Setmeal> findPage(String queryString);

    void add(Setmeal setmeal);

    void addSetmealCheckGroup(@Param("id") Integer id,@Param("checkgroupId") Integer checkgroupId);

    Setmeal findById(Integer id);

    List<Integer> findCheckgroupIdsBySetmealId(Integer id);

    void update(Setmeal setmeal);

    void deleteSetmealCheckGroup(Integer id);

    int findOrderCountBySetmealId(Integer id);

    void deleteById(Integer id);

    //查找数据库所有的图片名
    List<String> findImgs();

    List<Setmeal> findAll();

    Setmeal findDetailById(int id);


    List<Map<String,Object>> findSetmealCount();
}
