package com.zidong.drillsdate;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//钻孔点转换OFF文件测试
public class readdrillexcel {
    public static void main(String[] args) throws IOException {
        List <drill> drills= new ArrayList<>();
        drills=get_geodrillsVertex();
        System.out.printf("读取成功");
        String path=savapath();
        writeExcel(drills,path);


    }
    private static void writeExcel(List<drill> dills ,  String path) throws IOException {

        //1、创建Workbook
        Workbook workbook = new XSSFWorkbook();
        //2、创建sheet,默认名字是sheet1，sheet2...
        Sheet sheet = workbook.createSheet("Sheel2");
        Row row0,row1;
        Cell cell0,cell1,cell2,cell3;
        int rowcount=0;
        int cell=24;
        int m=0;
        for(int i=0;i<dills.size();i++) {
            row0 = sheet.createRow(rowcount);
            cell0 = row0.createCell(0);
            cell0.setCellValue(dills.get(i).name);

            // row0 = sheet.createRow(i+1);
            cell1 = row0.createCell(1);
            cell1.setCellValue(dills.get(i).x);

            // row0 = sheet.createRow(i+1);
            cell2 = row0.createCell(2);
            cell2.setCellValue(dills.get(i).y);

            //row0 = sheet.createRow(i+1);
            cell3 = row0.createCell(3);
            cell3.setCellValue(dills.get(i).z);
            rowcount++;
            for( m=0;m<cell;m++) {
                row1 = sheet.createRow(rowcount);
                cell0 = row1.createCell(0);
                cell0.setCellValue(dills.get(i).name);

                // row0 = sheet.createRow(i+1);
                cell1 = row1.createCell(1);
                cell1.setCellValue(dills.get(i).x);

                // row0 = sheet.createRow(i+1);
                cell2 = row1.createCell(2);
                cell2.setCellValue(dills.get(i).y);

                //row0 = sheet.createRow(i+1);
                cell3 = row1.createCell(3);
                cell3.setCellValue(dills.get(m).layerTopValue.get(m));
                rowcount++;

                //FileOutputStream outputStream = new FileOutputStream(path);
            }
        }
        try {
            // FileOutputStream stream= FileUtils.openOutputStream(file);
            FileOutputStream outputStream = new FileOutputStream(path);
            //写入
            workbook.write(outputStream);
            //关闭输出流
            outputStream.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static List get_geodrillsVertex(){
        List <drill> drills= new ArrayList<>();
        GeoDataLoader g=new GeoDataLoader();
        String filepath="C:\\Users\\86195\\Desktop\\duqu\\钻孔数据测试.xlsx";
        drills=g.readGeoVertices(filepath);

        List list = new ArrayList();
        for(int i=0;i<drills.size();++i)
        {
//            JSONObject job = new JSONObject();
            Map job = new HashMap();
            job.put("name",drills.get(i).name);
            job.put("x",drills.get(i).x);
            job.put("y",drills.get(i).y);
            job.put("z",drills.get(i).z);
            job.put("layerz",drills.get(i).layerTopValue);
//            job.put("z",lines.get(i).vertices.get(0).z);
//            job.put("xx",lines.get(i).vertices.get(1).x);
//            job.put("yy",lines.get(i).vertices.get(1).y);
//            job.put("zz",lines.get(i).vertices.get(1).z);

            list.add(job);


        }
        return drills;

    }
    private static String savapath() {
        System.out.println("选择存储位置");

        JFileChooser chooser = new JFileChooser();
//        FileNameExtensionFilter supported = new FileNameExtensionFilter("Supported files", "xlsx");
//        chooser.setFileFilter(supported);
        //int option = chooser.showOpenDialog(null);
        //JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter supported1 = new FileNameExtensionFilter("xlsx", "xlsx");
        chooser.setFileFilter(supported1);
        //下面的方法将阻塞，直到【用户按下保存按钮且“文件名”文本框不为空】或【用户按下取消按钮】
        int option = chooser.showSaveDialog(null);
        String path = null;
        if (option == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
            File file = chooser.getSelectedFile();
            String F = file.getPath() + ".xlsx";
            path = F;
        }

        return path;
    }
}
