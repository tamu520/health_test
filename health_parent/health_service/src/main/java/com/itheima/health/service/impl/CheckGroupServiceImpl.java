package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 查询一条检查组 用于编辑
     * @param id 需要查询的id
     * @return
     */
    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    /**
     * 查找检查组,有哪些检查项
     * @param id
     * @return 返回检查项的 id集合
     */
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    /**
     * 编辑项目组
     * @param checkGroup    修改检查组的信息
     * @param checkitemId   修改检查组与检查项关联表, 先删除原先的再更新
     */
    @Override
    @Transactional
    public void update(CheckGroup checkGroup, Integer[] checkitemId) {
        //更新检查组信息
        checkGroupDao.update(checkGroup);

        //先删除关系表的信息
        checkGroupDao.deleteCheckGroupCheckItem(checkGroup.getId());
        if(null!=checkitemId){
            //然后再添加新的关系表数据
            for (Integer integer : checkitemId) {
                checkGroupDao.addCheckGroupCheckItem(checkGroup.getId(),integer);
            }
        }
    }

    /**
     * 删除检查组,删除前判断是否被套餐表setmeal使用
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Integer id) throws HealthException{
        //判断是否被套餐表setmeal使用
        int count=checkGroupDao.findSetmealCountByCheckGroupId(id);
        if(count>0){
            throw new HealthException("还有套餐表使用这个检查组,不能删除");
        }
        //删除关系表
        checkGroupDao.deleteCheckGroupCheckItem(id);
        //删除检查组
        checkGroupDao.deleteById(id);
    }

}
