package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

        //计算从哪开始查
        Integer currentPage = queryPageBean.getCurrentPage();
        queryPageBean.setCurrentPage((currentPage-1)*queryPageBean.getPageSize());

        //查询结果
        List<CheckItem> pageRows = checkItemDao.findPage(queryPageBean);

        //查询条数
        Long count=checkItemDao.getCount(queryPageBean);

        return new PageResult<CheckItem>(count,pageRows);
    }

    @Override
    public void deleteById(Integer id) {
        checkItemDao.deleteById(id);
    }
}
