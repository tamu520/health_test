package com.itheima.health.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Random;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    /*@Reference
    private*/
    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        Jedis jedis = jedisPool.getResource();

        String key=RedisMessageConstant.SENDTYPE_ORDER+":" + telephone;
        //查找redis中是否有对应的值
        String redisByPhone = jedis.get(key);
        //如果已发过就不能发
        if(redisByPhone!=null){
            jedis.close();
            return new Result(false,"验证码已存在");
        }

        //随机数字
        Random random = new Random();
        int i = random.nextInt(10000);
        String code=null;
        if(i<1000){
            code = "0" + i;
        }else{
            code=i+"";
        }
        System.out.println("验证码:"+code);

        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code);
        } catch (ClientException e) {
            //e.printStackTrace();
            jedis.close();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //没发过则添加redis
        jedis.setex(key,300,code);
        jedis.close();
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        Jedis jedis = jedisPool.getResource();

        String key=RedisMessageConstant.SENDTYPE_LOGIN+":" + telephone;
        //查找redis中是否有对应的值
        String redisByPhone = jedis.get(key);
        //如果已发过就不能发
        if(redisByPhone!=null){
            jedis.close();
            return new Result(false,"验证码已存在");
        }

        //随机数字
        Random random = new Random();
        int i = random.nextInt(10000);
        String code=null;
        if(i<1000){
            code = "0" + i;
        }else{
            code=i+"";
        }
        System.out.println("验证码:"+code);

        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code);
        } catch (ClientException e) {
            //e.printStackTrace();
            jedis.close();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //没发过则添加redis
        jedis.setex(key,300,code);
        jedis.close();
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
