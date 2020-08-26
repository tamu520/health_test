package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 业务层判断,上传的文件中数据是否正常
     * @param list
     */
    @Override
    @Transactional
    public void add(List<OrderSetting> list) throws HealthException{
        /*
            需要判断:
                如果日期在数据库中存在:
                    则更新那天的数据的可预约人数,并且 可预约人数不能小于 已经预约的人数
                如果日期在数据库中不存在:
                    则添加那天数据
         */
        for (OrderSetting orderSetting : list) {
            //查询数据库中是否有这个日期
            OrderSetting obj=orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
            if(null!=obj){
                //如果有就是更新
                //更新前判断是否 低于已预约的数量
                if(obj.getReservations()>orderSetting.getNumber()){
                    //大于则抛出异常
                    throw new HealthException(obj.getOrderDate()+"的可预约人数低于已预约人数,导入失败");
                }else if(obj.getNumber()!=orderSetting.getNumber()){
                    orderSettingDao.updateNumber(orderSetting);
                    System.out.println(obj.getOrderDate());
                }
                //如果一样则忽略
            }else{
                //如果没有,则是添加
                orderSettingDao.add(orderSetting);
            }
        }
    }

    @Override
    public List<Map<String, Integer>> getOrderSettingByMonth(String month) {
        String minDate = month + "-01";
        String maxDate = month + "-31";

        Map<String, String> map = new HashMap<>();
        map.put("minDate",minDate);
        map.put("maxDate",maxDate);
        return orderSettingDao.getOrderSettingByMonth(map);
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        OrderSetting orderDate = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
        if(null!=orderDate){
            if(orderSetting.getNumber()<orderDate.getReservations()){
                throw new HealthException(orderDate.getOrderDate()+"的可预约人数低于已预约人数,设置失败");
            }else if(orderSetting.getNumber()!=orderDate.getNumber()){
                orderSettingDao.updateNumber(orderSetting);
            }
        }else{
            orderSettingDao.add(orderSetting);
        }
    }
}
