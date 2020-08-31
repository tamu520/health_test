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
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 查找所有的图片,用于清理
     * @return
     */
    @Override
    public List<String> findImgs() {
        return setmealDao.findImgs();
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findDetailById(int id) {
        return setmealDao.findDetailById(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }


    //静态化页面区域

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //生成模板路径的前缀
    @Value("${out_put_path}")
    private String out_put_path;

    /**
     * 用于将模板 生成 静态页面的方法
     * @param templateName  模板的文件名
     * @param dataMap   需要处理显示到页面的数据
     * @param filename  生成静态页面的文件名
     */
    private void generateHtml(String templateName, Map<String,Object> dataMap,String filename){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        //获取模板的对象
        try {
            Template template = configuration.getTemplate(templateName);
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
            //将数据写进模板里面
            template.process(dataMap,bf);

            bf.flush();
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 套餐列表的专属 生成静态页面方法
     * @param setmealList   所有套餐的信息
     */
    /*@PostConstruct*/
    private void generateSetmealList(List<Setmeal> setmealList){
        //用于给模板的map
        Map<String, Object> map = new HashMap<>();
        //将套餐集合放进map里,key需要与模板的#{xxx}名 对应
        map.put("setmealList",setmealList);

        //拼接路径
        String file = out_put_path + "mobile_setmeal.html";
        //生成静态页面
        generateHtml("mobile_setmeal.ftl",map,file);
    }

    private void generateSetmealDetals(){

    }
}
