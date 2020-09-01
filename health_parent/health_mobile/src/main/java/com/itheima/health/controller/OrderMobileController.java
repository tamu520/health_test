package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.SetmealService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderMobileController {

    @Reference
    private OrderService orderService;
    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo){
        //首先判断验证码是否正确
        Jedis jedis = jedisPool.getResource();
        String code = jedis.get(RedisMessageConstant.SENDTYPE_ORDER + ":" + orderInfo.get("telephone"));
        if(code==null){
            return new Result(false,"请先获取验证码");
        }
        //验证码错误
        if(!code.equals(orderInfo.get("validateCode"))){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //正确就移除验证码
        //jedis.del(RedisMessageConstant.SENDTYPE_ORDER + ":" + orderInfo.get("telephone"));

        //预约的类型,前端没有传,因为这里是微信中直接预约,所以可以直接赋值
        orderInfo.put("orderType",Order.ORDERTYPE_WEIXIN);
        //进入业务层处理预约
        try {
            Order order = orderService.submitOrder(orderInfo);
            return new Result(true,MessageConstant.ORDER_SUCCESS,order);
        }catch (HealthException e){
            return new Result(false,e.getMessage());
        }
    }

    @PostMapping("/findById")
    public Result findById(int id){
        Map<String,Object> orderServiceById=orderService.findById(id);
        return new Result(true,null,orderServiceById);
    }

    @GetMapping("/exportSetmealInfo")
    public Result exportSetmealInfo(int id,HttpServletResponse resp){
        Map<String, Object> orderServiceById = orderService.findById(id);
        //套餐的id
        Integer setmeal_id =(Integer) orderServiceById.get("setmeal_id");
        //用id查找套餐详细信息
        Setmeal setmeal = setmealService.findDetailById(setmeal_id);

        Document doc = new Document();

        try {
            //告诉浏览器以pdf显示
            resp.setContentType("application/pdf");
            //设置头
            resp.setHeader("Context-Disposition","attachement;filename=setmealInfo.pdf");
            PdfWriter.getInstance(doc,resp.getOutputStream());

            //打开文档
            doc.open();

            //=====写入内容
            //设置字体
            BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            Font font = new Font(cn,10,Font.NORMAL,Color.BLUE);

            doc.add(new Paragraph("体检人:"+(String)orderServiceById.get("member"),font));
            doc.add(new Paragraph("体检套餐:"+(String)orderServiceById.get("setmeal"),font));

            Date orderDate=(Date)orderServiceById.get("orderDate");
            doc.add(new Paragraph("体检日期:"+new SimpleDateFormat("yyyy-MM-dd").format(orderDate),font));
            doc.add(new Paragraph("预约类型:"+(String)orderServiceById.get("orderType"),font));

            //====向document生成表格

            //表头 3列
            Table table = new Table(3);
            table.setWidth(80);
            table.setBorder(1);
            //水平对齐
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            //垂直对齐
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);

            //表格属性
            //边框
            table.setBorderColor(new Color(0,0,255));
            //表格与字体的间距
            table.setPadding(5);
            //表格上下间距
            table.setSpacing(5);
            //字体显示居中样式
            table.setAlignment(Element.ALIGN_CENTER);

            //往单元格添加数据
            table.addCell(buildCell("项目名称",font));
            table.addCell(buildCell("项目内容",font));
            table.addCell(new Cell(new Phrase("项目解读",font)));

            //填充检查组
            List<CheckGroup> checkGroups = setmeal.getCheckGroups();
            for (CheckGroup checkGroup : checkGroups) {
                //项目组名称
                table.addCell(buildCell(checkGroup.getName(),font));

                //项目的内容,都拼接在一起
                String checkItemStr = "";
                List<CheckItem> checkItems = checkGroup.getCheckItems();
                if(null!=checkItems){
                    StringBuffer sb = new StringBuffer();
                    for (CheckItem checkItem : checkItems) {
                        sb.append(checkItem.getName()).append(" ");//空格分开
                    }
                    //去除最后一个空格
                    sb.setLength(sb.length()-1);
                    checkItemStr = sb.toString();
                }
                table.addCell(buildCell(checkItemStr,font));
                //项目组的解释 remark
                table.addCell(buildCell(checkGroup.getRemark(),font));
            }

            //表格添加进文档
            doc.add(table);
            //关闭文档
            doc.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,null);
    }

    // 传递内容和字体样式，生成单元格
    private Cell buildCell(String content, Font font) throws BadElementException {
        Phrase phrase = new Phrase(content, font);
        return new Cell(phrase);
    }
}
