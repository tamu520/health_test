package com.itheima.health.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.itheima.constant.MessageConstant;

@RestController
@RequestMapping("/checkItem")
public class CheckItemController {

    @Reference
    private CheckItemService checkItemService;

    @GetMapping("/findAll")
    public Result findAll(){
        List<CheckItem> checkItems = checkItemService.findAll();

        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItems);
    }

    @PostMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){
        checkItemService.add(checkItem);
        return new Result(true,"添加成功");
    }
}
