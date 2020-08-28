package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderMobileController {

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo){
        //首先判断验证码是否正确
        Jedis jedis = jedisPool.getResource();
        String code = jedis.get(RedisMessageConstant.SENDTYPE_ORDER + ":" + orderInfo.get("telephone"));
        if(code==null){
            return new Result(false,"请先获取验证码");
        }
        //验证码错误
        if(!code.equals(orderInfo.get("validateCode"))){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //正确就移除验证码
        //jedis.del(RedisMessageConstant.SENDTYPE_ORDER + ":" + orderInfo.get("telephone"));

        //预约的类型,前端没有传,因为这里是微信中直接预约,所以可以直接赋值
        orderInfo.put("orderType",Order.ORDERTYPE_WEIXIN);
        //进入业务层处理预约
        try {
            Order order = orderService.submitOrder(orderInfo);
            return new Result(true,MessageConstant.ORDER_SUCCESS,order);
        }catch (HealthException e){
            return new Result(false,e.getMessage());
        }
    }

    @PostMapping("/findById")
    public Result findById(int id){
        Map<String,String> orderServiceById=orderService.findById(id);
        return new Result(true,null,orderServiceById);
    }
}
