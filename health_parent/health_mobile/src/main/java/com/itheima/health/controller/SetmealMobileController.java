package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/setmeal")
public class SetmealMobileController {

    @Reference
    private SetmealService setmealService;

    @GetMapping("/getSetmeal")
    public Result getSetmeal(){
        List<Setmeal> setmeals = setmealService.findAll();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("setmeals", setmeals);
        map.put("domain", QiNiuUtils.DOMAIN);
        return new Result(true, null, map);
    }

    @GetMapping("/findDetailById")
    public Result findDetailById(int id){
        Setmeal setmeal=setmealService.findDetailById(id);
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        return new Result(true,null,setmeal);
    }

    @PostMapping("/findById")
    public Result findById(int id){
        Setmeal setmeal = setmealService.findById(id);
        setmeal.setImg(QiNiuUtils.DOMAIN+setmeal.getImg());
        return new Result(true,null,setmeal);
    }
}
