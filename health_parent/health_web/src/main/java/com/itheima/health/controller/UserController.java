package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.Result;
import com.itheima.health.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    @GetMapping("/getLoginUserName")
    public Result getLoginUserName(){
        //获取框架储存User的信息
        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //拿到用户名的名字
        String username = user.getUsername();
        return new Result(true,null,username);
    }
}
