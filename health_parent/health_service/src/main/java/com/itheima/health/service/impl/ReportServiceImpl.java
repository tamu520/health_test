package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //今日日期
        String todayDate = sdf.format(calendar.getTime());
        System.out.println(todayDate);

        //设为星期1
        calendar.set(Calendar.DAY_OF_WEEK,1);
        //获取星期1的日期
        String monday = sdf.format(calendar.getTime());
        System.out.println(monday);

        //将日历类设置成现在的时间
        calendar=Calendar.getInstance();
        //获取本月1号
        calendar.set(Calendar.DAY_OF_MONTH,1);
        System.out.println(calendar.getTime());
        //获取本月最后一号
        //月份+1,日期-1天得到本月最后一天
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        System.out.println(calendar.getTime());
    }

    @Override
    public Map<String, Object> getBusinessReport() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //今日日期
        String todayDate = sdf.format(calendar.getTime());

        //设为星期1
        calendar.set(Calendar.DAY_OF_WEEK,1);
        //获取星期1的日期
        String monday = sdf.format(calendar.getTime());
        //获取这个星期的最后一天:星期天
        //星期1 加6天就是星期天
        calendar.add(Calendar.DAY_OF_MONTH,6);
        String sunday = sdf.format(calendar.getTime());

        //将日历类设置成现在的时间
        calendar=Calendar.getInstance();
        //获取本月1号
        calendar.set(Calendar.DAY_OF_MONTH,1);
        String monthFirstDay = sdf.format(calendar.getTime());

        //获取本月最后一号
        //月份+1,日期-1天得到本月最后一天
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String monthLastDay = sdf.format(calendar.getTime());


        /*          会员数据统计          */
        //今日新增会员数
        int todayNewMember=memberDao.findMemberCountByDate(todayDate);
        //会员总数量
        int totalMember=memberDao.findMemberTotalCount();
        //本周新增会员数
        int thisWeekNewMember=memberDao.findMemberCountAfterDate(monday);
        //本月新增会员数
        int thisMonthNewMember = memberDao.findMemberCountAfterDate(monthFirstDay);

        /*          预约到诊数据统计        */
        //今日预约数
        int todayOrderNumber=orderDao.findOrderCountByDate(todayDate);
        //今日到诊数
        int todayVisitsNumber=orderDao.findVisitsCountByDate(todayDate);
        //本周预约数,    预约可以在未来预约,可以在更久的日期预约,所以要限制本周的日期
        Map<String, String> weekMap = new HashMap<>();
        weekMap.put("startTime",monday);
        weekMap.put("endTime",sunday);
        int thisWeekOrderNumber=orderDao.findOrderCountBetweenDate(weekMap);
        //本周到诊数
        int thisWeekVisitsNumber=orderDao.findVisitsCountAfterDate(weekMap);
        //本月预约数
        Map<String, String> monthMap = new HashMap<>();
        weekMap.put("startTime",monthFirstDay);
        weekMap.put("endTime",monthLastDay);
        int thisMonthOrderNumber=orderDao.findOrderCountBetweenDate(monthMap);
        //本月到诊数
        int thisMonthVisitsNumber=orderDao.findVisitsCountAfterDate(monthMap);

        /*          热门套餐        */
        List<Map<String,Object>> hotSetmeal=orderDao.findHotSetmeal();

        //将所有结果装进map集合返回
        Map<String, Object> result = new HashMap<>();
        result.put("reportDate",todayDate);

        result.put("todayNewMember",todayNewMember);
        result.put("totalMember",totalMember);
        result.put("thisWeekNewMember",thisWeekNewMember);
        result.put("thisMonthNewMember",thisMonthNewMember);

        result.put("todayOrderNumber",todayOrderNumber);
        result.put("todayVisitsNumber",todayVisitsNumber);
        result.put("thisWeekOrderNumber",thisWeekOrderNumber);
        result.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        result.put("thisMonthOrderNumber",thisMonthOrderNumber);
        result.put("thisMonthVisitsNumber",thisMonthVisitsNumber);

        result.put("hotSetmeal",hotSetmeal);
        return result;
    }
}
