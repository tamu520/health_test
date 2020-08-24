package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());

        String queryString = queryPageBean.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            queryString = "%" + queryString + "%";
        }

        Page<Setmeal> page = setmealDao.findPage(queryString);
        return new PageResult<Setmeal>(page.getTotal(), page.getResult());
    }

    /**
     * 添加一条套餐管理
     * @param setmeal 添加的数据
     * @param checkgroupIds 检查组的多项id
     */
    @Override
    @Transactional
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);

        if(checkgroupIds!=null){
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(),checkgroupId);
            }
        }
    }

    /**
     * 查找一条套餐,用于编辑
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    /**
     * 查找这个套餐,有多少检查组
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckgroupIdsBySetmealId(Integer id) {
        return setmealDao.findCheckgroupIdsBySetmealId(id);
    }

    @Override
    @Transactional
    public void update(Setmeal setmeal,Integer[] checkgroupIds) {
        //更新信息
        setmealDao.update(setmeal);
        //删除关联的检查组
        setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        //添加新的关联的检查组
        if(checkgroupIds!=null) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(),checkgroupId);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(int id) throws HealthException{
        //先查询 订单表 是否使用了这个套餐
        int count=setmealDao.findOrderCountBySetmealId(id);
        if(count>0){
            throw new HealthException("有订单正在使用这个套餐,不能删除");
        }
        //删除与检查组的关系
        setmealDao.deleteSetmealCheckGroup(id);
        //删除套餐
        setmealDao.deleteById(id);
    }
}
