package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.ReportService;
import com.itheima.health.service.SetmealService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    public static void main(String[] args) {
        //测试用
        Calendar calendar = Calendar.getInstance();
        //创建一个存放现在到过去一年的月份的集合
        List<String> month = new ArrayList<String>();
        //获取一年前到现在的Date,月份为单位
        /*calendar.add(Calendar.MONTH,-12);
        System.out.println(calendar.get(Calendar.MONTH));*/

        //只展示月份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(calendar.getTime());
        System.out.println("日期:"+format);
        //获取一年前到现在的Date,月份为单位
        calendar.add(Calendar.MONTH,-11);

        //循环12次,存放一年的月份
        for (int i = 0; i < 12; i++) {
            //转为date
            Date time = calendar.getTime();
            //然后将date格式化为 指定格式 年-月,用于查询
            //存入集合当中
            month.add(sdf.format(time));
            //然后日历的当前时间要加1,用下一次循环添加
            calendar.add(Calendar.MONTH,1);
        }
        System.out.println(month);
    }

    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        Calendar calendar = Calendar.getInstance();
        //创建一个存放现在到过去一年的月份的集合
        List<String> month = new ArrayList<String>();
        //只展示月份,将指定格式的月份放入集合
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        //获取一年前到现在的Date,月份为单位
        calendar.add(Calendar.MONTH,-11);

        //循环12次,存放一年的月份
        for (int i = 0; i < 12; i++) {
            //转为date
            Date time = calendar.getTime();
            //然后将date格式化为 指定格式 年-月,用于查询
            //存入集合当中
            month.add(sdf.format(time));
            //然后日历的当前时间要加1,用下一次循环添加
            calendar.add(Calendar.MONTH,1);
        }
        //将集合给业务层,返回每月的用户总数
        List<Integer>userCountList=memberService.getMemberReport(month);

        //对应前端,用map存储 月份 和 对应的用户总数数量
        Map<String, List> memberMap = new HashMap<String, List>();
        memberMap.put("months",month);
        memberMap.put("memberCount",userCountList);

        return new Result(true,null,memberMap);
    }

    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        List<Map<String, Object>> setmealCount = setmealService.findSetmealCount();
        //前端要求的数据,其中一个已经上面拿到了
        //另一个需要 套餐名需要 单独拿出来
        List<String> setmealNames = new ArrayList<String>();

        //前端的 饼状图 name是名字,value是对应的数值,所以数据库要用这两个别名

        for (Map<String, Object> stringObjectMap : setmealCount) {
            System.out.println(stringObjectMap.get("name"));
            setmealNames.add((String)stringObjectMap.get("name"));
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("setmealNames",setmealNames);
        resultMap.put("setmealCount",setmealCount);

        return new Result(true,null,resultMap);
    }

    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        Map<String, Object> resultMap = reportService.getBusinessReport();
        return new Result(true,null,resultMap);
    }

    @RequestMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest req , HttpServletResponse resp){
        //获取
        String realPath = req.getSession().getServletContext().getRealPath("/template/report_template.xlsx");

        try (Workbook workbook =new XSSFWorkbook(realPath)){
            //拿到excel第一个表
            Sheet sheetAt = workbook.getSheetAt(0);

            //拿到报表数据
            Map<String, Object> data = reportService.getBusinessReport();

            //模板日期位置填充
            sheetAt.getRow(2).getCell(5).setCellValue(data.get("reportDate").toString());

            //新增会员数
            sheetAt.getRow(4).getCell(5).setCellValue(data.get("todayNewMember").toString());
            //总会员数
            sheetAt.getRow(4).getCell(7).setCellValue(data.get("totalMember").toString());
            //本周新增会员数
            sheetAt.getRow(5).getCell(5).setCellValue(data.get("thisWeekNewMember").toString());
            //本月新增会员数
            sheetAt.getRow(5).getCell(7).setCellValue(data.get("thisMonthNewMember").toString());

            //今日预约数
            sheetAt.getRow(7).getCell(5).setCellValue(data.get("todayOrderNumber").toString());
            //今日到诊数
            sheetAt.getRow(7).getCell(7).setCellValue(data.get("todayVisitsNumber").toString());
            //本周预约数
            sheetAt.getRow(8).getCell(5).setCellValue(data.get("thisWeekOrderNumber").toString());
            //本周到诊数
            sheetAt.getRow(8).getCell(7).setCellValue(data.get("thisWeekVisitsNumber").toString());
            //本月预约数
            sheetAt.getRow(9).getCell(5).setCellValue(data.get("thisMonthOrderNumber").toString());
            //本月到诊数
            sheetAt.getRow(9).getCell(7).setCellValue(data.get("thisMonthVisitsNumber").toString());

            //热门套餐
            List<Map<String,Object>> hotSetmeal = (List<Map<String, Object>>) data.get("hotSetmeal");
            //循环取套餐,递增行数
            int rowIndex=12;
            for (Map<String, Object> setmealMap : hotSetmeal) {
                int cellIndex=4;
                //套餐名称
                sheetAt.getRow(rowIndex).getCell(cellIndex).setCellValue(setmealMap.get("name").toString());
                cellIndex++;
                //预约数量
                sheetAt.getRow(rowIndex).getCell(cellIndex).setCellValue(setmealMap.get("setmeal_count").toString());
                cellIndex++;
                //占比
                sheetAt.getRow(rowIndex).getCell(cellIndex).setCellValue(setmealMap.get("proportion").toString());
                cellIndex++;
                //备注
                //sheetAt.getRow(rowIndex).getCell(cellIndex).setCellValue(setmealMap.get("name").toString());
                rowIndex++;
            }
            //告诉浏览器是excel文件
            resp.setContentType("application/vnd.ms-excel");
            //设置文件名
            String filename = "运营数据"+data.get("reportDate")+".xlsx";
            //直接使用中文名的话会乱码,使用ISO-8859-1编码传输
            filename = new String(filename.getBytes(),"ISO-8859-1");

            //告诉浏览器这是要下载文件
            resp.setHeader("Content-Disposition","attachement;filename=" + filename);

            //输出
            workbook.write(resp.getOutputStream());
            resp.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
