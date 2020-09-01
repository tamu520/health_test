package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {
    //会员表
    @Autowired
    private MemberDao memberDao;

    //预约表
    @Autowired
    private OrderSettingDao orderSettingDao;

    //预约的订单
    @Autowired
    private OrderDao orderDao;

    @Override
    public Order submitOrder(Map<String, String> orderInfo) throws HealthException {
        //判断选择的预约日期格式 ,是否存在
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //存放日期
        Date orderDate=null;

        //判断格式
        try {
            orderDate=sdf.parse(orderInfo.get("orderDate"));
        } catch (ParseException e) {
            //e.printStackTrace();
            throw new HealthException("预约格式错误");
        }

        //预约日期是否存在
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(orderDate);
        //如果不存在
        if(null==orderSetting){
            throw new HealthException("当前选择的日期不能预约");
        }

        //能预约的话就判断预约人数是否满了
        if(orderSetting.getReservations()>=orderSetting.getNumber()){
            throw new HealthException("当前选择的日期预约人数已满");
        }

        //判断用户是不是会员,根据手机查找,因为需要验证码才能通过
        Member member = memberDao.findByTelephone(orderInfo.get("telephone"));
        //如果不是会员则新增会员
        if (null == member) {
            //否则是新增会员
            member = new Member();
            member.setName(orderInfo.get("name"));
            member.setSex(orderInfo.get("sex"));
            member.setIdCard(orderInfo.get("idCard"));
            member.setPhoneNumber(orderInfo.get("telephone"));
            member.setRegTime(new Date());

            //通过selectKey拿到新增id
            memberDao.add(member);
        }

        //用户是否有重复预约
        Order order = new Order();
        order.setMemberId(member.getId());
        order.setOrderDate(orderDate);
        order.setSetmealId(Integer.parseInt(orderInfo.get("setmealId")));

        int orderCount = orderDao.findByCondition(order);
        if(orderCount>0){
            throw new HealthException("该用户已经预约了套餐");
        }

        //没有问题则添加订单
        order.setOrderType(Order.ORDERTYPE_WEIXIN);
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        //添加订单
        orderDao.add(order);

        //预约数量+1
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return order;
    }

    @Override
    public Map<String, Object> findById(int id) {
        return orderDao.findById(id);
    }
}
