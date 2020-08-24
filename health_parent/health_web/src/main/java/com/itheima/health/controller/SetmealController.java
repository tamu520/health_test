package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.itheima.health.constant.MessageConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult<Setmeal> pageResult = setmealService.findPage(queryPageBean);
        return new Result(true, null, pageResult);
    }

    //接受的文件名,需要与前端的name一致
    @PostMapping("/upload")
    public Result upload(MultipartFile imgFile) {
        try {
            //获取上传的文件名
            String originalFilename = imgFile.getOriginalFilename();
            //获取后缀名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成随机名+后缀名
            String randomName = UUID.randomUUID() + extension;

            //使用七牛云的接口上传到服务器
            //使用工具类中uploadViaByte方法 传入上传文件的字节码, 和上面得到的随机名
            QiNiuUtils.uploadViaByte(imgFile.getBytes(), randomName);

            //向前端返回两个数据: 文件名,和上传到七牛云的文件域名
            //拼起来就是文件名的网址

            //可以自定义个Pojo类或者使用Map响应json
            Map<String, String> imgMap = new HashMap<>();
            imgMap.put("imgName", randomName);
            imgMap.put("domain", QiNiuUtils.DOMAIN);

            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, imgMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new HealthException(MessageConstant.PIC_UPLOAD_FAIL);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        setmealService.add(setmeal,checkgroupIds);
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @GetMapping("/findById")
    public Result findById(Integer id){
        Setmeal setmeal=setmealService.findById(id);
        //这里需要多加一个图片的域名拼接,用于前端显示,因为数据库之存储文件名
        //所以使用map
        Map<String, Object> map = new HashMap<>();
        map.put("setmeal",setmeal);
        map.put("domain",QiNiuUtils.DOMAIN);
        return new Result(true,null,map);
    }

    @GetMapping("/findCheckgroupIdsBySetmealId")
    public Result findCheckgroupIdsBySetmealId(Integer id){
        List<Integer> checkGroupId = setmealService.findCheckgroupIdsBySetmealId(id);
        return new Result(true,null,checkGroupId);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        setmealService.update(setmeal,checkgroupIds);
        return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
    }

    @GetMapping("/deleteById")
    public Result deleteById(int id){
        setmealService.deleteById(id);
        return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
    }
}
