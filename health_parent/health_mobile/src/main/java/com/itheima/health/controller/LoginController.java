package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Reference
    private MemberService memberService;

    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/check")
    public Result check(@RequestBody Map<String,String> loginInfo, HttpServletResponse res){
        Jedis jedis = jedisPool.getResource();
        String code = jedis.get(RedisMessageConstant.SENDTYPE_LOGIN + ":" + loginInfo.get("telephone"));
        if(code==null){
            return new Result(false,"请先获取验证码");
        }
        //验证码错误
        if(!code.equals(loginInfo.get("validateCode"))){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //正确就移除验证码
        //jedis.del(RedisMessageConstant.SENDTYPE_ORDER + ":" + loginInfo.get("telephone"));

        //判断是否是新用户
        Member member=memberService.findByTelephone(loginInfo.get("telephone"));

        //增加cookie
        Cookie cookie = new Cookie("login_member_telephone",loginInfo.get("telephone"));
        cookie.setMaxAge(24*60*60*60);
        //全路径都能使用
        cookie.setPath("/");
        res.addCookie(cookie);

        return new Result(true,null,member);
    }
}
