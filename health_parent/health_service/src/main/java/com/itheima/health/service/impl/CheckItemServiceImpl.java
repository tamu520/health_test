package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        //使用原本的方式查询
        /*
        //计算从哪开始查
        Integer currentPage = queryPageBean.getCurrentPage();
        queryPageBean.setCurrentPage((currentPage-1)*queryPageBean.getPageSize());
        //查询结果
        List<CheckItem> pageRows = checkItemDao.findPage(queryPageBean);
        //查询条数
        Long count=checkItemDao.getCount(queryPageBean);
        */

        //使用pageHelper插件
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());

        //判断是否有条件
        String queryString = queryPageBean.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            queryPageBean.setQueryString("%" + queryString + "%");
        }

        //获取查询结果  ,拿到条件下所有的结果
        //使用pageHelper分页
        Page<CheckItem> page = checkItemDao.findPage(queryPageBean);

        //能拿到所有的条件结果就 等于 拿到了所有的结果的条数 , 就不用再执行一次获取 数量的sql语句
        //从page拿到处理的结果
        return new PageResult<CheckItem>(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(Integer id) throws HealthException {
        int countByCheckItemId = checkItemDao.findCountByCheckItemId(id);

        if (countByCheckItemId > 0) {
            throw new HealthException("这个检查项有关联的检查组,不能删除");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }
}
