package com.itheima.health.job;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;

import java.util.List;

public class ClearImgJob {
    @Reference
    private SetmealService setmealService;

    public void clearImg(){
        //获取七牛云的所有图
        List<String> imgIn7Niu = QiNiuUtils.listFile();
        System.out.println(imgIn7Niu);

        //获取数据库的所有图片
        List<String> imgInDB = setmealService.findImgs();

        //集合中删除 另一个集合的值
        imgIn7Niu.removeAll(imgInDB);

        //集合转成数组(因为工具类需要传一个数组),再调用工具类删除
        String[] imgArray = imgIn7Niu.toArray(new String[]{});
        QiNiuUtils.removeFiles(imgArray);
    }
}
