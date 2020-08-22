package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    /**
     * 添加一条检查组,并根据选择的检查项向t_checkgroup_checkitem中间表添加0或多个数据
     * @param checkGroup 前端传过来的检查组信息
     * @param checkitemId 这个检查组所需的检查项id
     */
    @Override
    @Transactional  //应为可能会有一组的数据需要完成所以需要事务
    public void add(CheckGroup checkGroup,Integer[] checkitemId) {
        //添加检查组的基本信息
        checkGroupDao.add(checkGroup);

        //然后根据 检查组选择的检查项  循环添加进 t_checkgroup_checkitem表
        if(checkitemId!=null){
            //循环将选择的检查项添加数据
            for (Integer integer : checkitemId) {
                checkGroupDao.addCheckGroupCheckItem(checkGroup.getId(),integer);
            }
        }
    }

    /**
     * 检查组 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {

        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());

        String queryString = queryPageBean.getQueryString();
        if(queryString!=null && queryString.length()>0){
            queryString = "%"+queryString+"%";
        }

        Page<CheckGroup> checkGroups = checkGroupDao.findByCondition(queryString);

        return new PageResult<CheckGroup>(checkGroups.getTotal(),checkGroups.getResult());
    }
}
