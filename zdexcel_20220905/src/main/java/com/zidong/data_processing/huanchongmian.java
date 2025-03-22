package com.zidong.data_processing;
import com.zidong.drillsdate.vertex;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class huanchongmian {
    public static List<vertex> vertices = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        double X=0;
        double Y=0;
        double Angle=0;//角度
        double angle=0;//弧度

        double L=0;
        GeoDataLoader_webgl geoDataLoader = new GeoDataLoader_webgl();

        Scanner S=new Scanner(System.in);

        System.out.printf("请输入度数：");
        Angle=S.nextDouble();
        System.out.printf("请输入距离L：");
        L=S.nextDouble();

        //angle=Angle*Math.PI/180;
        List<String> Filespath = new ArrayList<>();
       // String PathTop ="C:\\Users\\86195\\Desktop\\fp-1";//批量读外边界文件，应是文件夹路径
       // geoDataLoader.getFile(PathTop,Filespath);


        //获取文件路径文件夹下的全部文件列表
        System.out.println("文件有如下：");
        //表示一个文件路径
        File file1 = new File("C:\\Users\\86195\\Desktop\\duqu\\缓冲带\\F4\\");
        //用数组把文件夹下的文件存起来
        File[] files = file1.listFiles();
        //foreach遍历数组
        String[] filesname=new String[file1.listFiles().length];
        String[] filespath=new String[file1.listFiles().length];
        int im=0;
        for (File file2 : files) {
            //打印文件列表：只读取名称使用getName();
            System.out.println("路径："+file2.getPath());
            System.out.println("文件夹/文件名："+file2.getName());
            filespath[im]=file2.getPath();
            filesname[im]=file2.getName();
            im++;
        }


        System.out.printf("请选择偏移方向：1为偏右上，2为偏右下，3为偏左上，4为偏左下：");
        int n=S.nextInt();

        for(int f=0;f<filespath.length;f++) {

            List<String> drillPathTopFilesTop = new ArrayList<>();
            List<String> layername = new ArrayList<>();
            String drillPathTop = filespath[f];//批量读外边界文件，应是文件夹路径
            geoDataLoader.getFile(drillPathTop, drillPathTopFilesTop);
            for (int i = 0; i < drillPathTopFilesTop.size(); ++i) {
                List<vertex> drillVertices = geoDataLoader.readGeoVertices(drillPathTopFilesTop.get(i), vertices,layername);
                System.out.println("11");
                drillVertices=chuliVertices( n,drillVertices,L,Angle);//对原始数据进行加减

                writeTest(drillVertices,layername,drillPathTopFilesTop.get(i));//写入原表格

                drillVertices.clear();
                System.out.println("已经进行加减，开始写入原文件");
            }
        }
    }

    public static List<vertex> chuliVertices(int n, List<vertex> vertices, double L,double Angle) {
        double x=0;
        double y=0;
        switch (n){

            //右上偏+y+x
            case 1:
                Angle=Angle-90;
                x=L*Math.cos(Angle*Math.PI/180);
                y=L*Math.sin(Angle*Math.PI/180);

                for(int i=0;i<vertices.size();i++)
                {
                    vertices.get(i).x+=x;
                    vertices.get(i).y+=y;

                }
                break;
            //右下偏-y+x
            case 2:
                Angle=90-Angle;
                x=L*Math.cos(Angle*Math.PI/180);
                y=L*Math.sin(Angle*Math.PI/180);
                for(int i=0;i<vertices.size();i++)
                {
                    vertices.get(i).x+=x;
                    vertices.get(i).y-=y;

                }
                break;
            //左上偏+y-x
            case 3:
                Angle=90-Angle;
                x=L*Math.cos(Angle*Math.PI/180);
                y=L*Math.sin(Angle*Math.PI/180);
                for(int i=0;i<vertices.size();i++)
                {
                    vertices.get(i).x-=x;
                    vertices.get(i).y+=y;

                }
                break;
            //左下偏-y-x
            case 4:
                Angle=Angle-90;
                x=L*Math.cos(Angle*Math.PI/180);
                y=L*Math.sin(Angle*Math.PI/180);
                for(int i=0;i<vertices.size();i++)
                {
                    vertices.get(i).x-=x;
                    vertices.get(i).y-=y;

                }
                break;
            default:
                System.out.printf("输入有误");
                break;
        }


        return vertices;
    }
    private static void writeTest(List<vertex> points, List<String>layername, String path) throws IOException {

        //1、创建Workbook
        //Workbook workbook = readExcelAb(path);;
        Workbook workbook =new XSSFWorkbook();
        //2、创建sheet,默认名字是sheet1，sheet2...
        Sheet sheet = workbook.createSheet("Sheel2");
        Row row0,row1,row2,row3;
        Cell cell0,cell1,cell2,cell3;
        row1=sheet.createRow(0);
        cell3=row1.createCell(1);
        cell3.setCellValue("X");
        cell3=row1.createCell(2);
        cell3.setCellValue("Y");
        cell3=row1.createCell(3);
        cell3.setCellValue("Z");

        for(int i=0;i<points.size();i++) {
            row0 = sheet.createRow(i+1);
            cell0 = row0.createCell(0);
            cell0.setCellValue(layername.get(i));

            cell0 = row0.createCell(1);
            cell0.setCellValue(points.get(i).x);

            cell1 = row0.createCell(2);
            cell1.setCellValue(points.get(i).y);

            cell2 = row0.createCell(3);
            cell2.setCellValue(points.get(i).z);



            //FileOutputStream outputStream = new FileOutputStream(path);

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
    public static Workbook readExcelAb(String path) throws IOException {
        // 获取后缀
        String suffix = path.substring(path.lastIndexOf("."));
        // 判断后缀为 xls 还是 xlsx
        if (path.matches("^.+\\.(?i)((xls)|(xlsx))$")){
            // 创建输入流
            FileInputStream fis = new FileInputStream(path);
            // 判断是否为以 .xls 结尾的 Excel 文件
            boolean isXlsExcel = path.matches("^.+\\.(?i)(xls)$");
            // 判断后缀生成 工作簿
            Workbook workbook = isXlsExcel ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);

            return workbook;
//            // 获取工作表
//            Sheet sheet = workbook.getSheetAt(0);
//            // 获取指定行
//            Row row = sheet.getRow(2);
//            // 获取指定列
//            Cell cell = row.getCell(2);
//            // 输出值
//            System.out.println(suffix + " :\t" + cell.getStringCellValue());

        }

        return null;
    }
}
