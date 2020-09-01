package com.itheima.health;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class TestItext {
    public static void main(String[] args) throws Exception {

        //创建文档对象
        Document document = new Document();

        //打开文件存储对象
        PdfWriter.getInstance(document, new FileOutputStream(new File("E:\\java\\test.pdf")));

        //打开文档
        document.open();
        //添加一句话
        document.add(new Paragraph("Hello World"));

        //关闭文档对象
        document.close();
    }
}
