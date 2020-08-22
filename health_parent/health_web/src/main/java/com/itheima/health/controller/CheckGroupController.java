package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;
import com.itheima.constant.MessageConstant;

@RestController
@RequestMapping("/checkGroup")
public class CheckGroupController{

    @Reference
    private CheckGroupService checkGroupService;

    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult<CheckGroup> pageResult=  checkGroupService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,pageResult);
    }

    @PostMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup, @RequestParam Integer[] checkitemId){
        checkGroupService.add(checkGroup,checkitemId);
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

}
