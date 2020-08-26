package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import com.itheima.health.utils.POIUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;

    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String month) {
        List<Map<String,Integer>> list=orderSettingService.getOrderSettingByMonth(month);
        return new Result(true, null,list);
    }

    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile) {
        try {
            //使用工具类获取 时间 | 可预约数量
            List<String[]> strings = POIUtils.readExcel(excelFile);

            List<OrderSetting> list = new ArrayList<OrderSetting>();
            SimpleDateFormat sdf = new SimpleDateFormat(POIUtils.DATE_FORMAT);

            //将List<String[]>转成 List<OrderSetting>
            for (String[] string : strings) {
                //将字符串转成Date
                Date parse = sdf.parse(string[0]);
                int i = Integer.parseInt(string[1]);
                list.add(new OrderSetting(parse, i));
                System.out.println(parse);
            }
            //给业务层进行处理
            orderSettingService.add(list);
            return new Result(true, "上传成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "上传失败");
    }

    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        System.out.println(orderSetting.getOrderDate());
        orderSettingService.editNumberByDate(orderSetting);
        return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    }
}
