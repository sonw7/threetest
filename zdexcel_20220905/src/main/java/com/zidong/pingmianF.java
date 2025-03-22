package com.zidong;

import com.zidong.classdesign.Fxishu;
import com.zidong.classdesign.Points;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class pingmianF {

    public static void main(String[] args) throws IOException {
        System.out.println("选择文件并读入");
        JFileChooser jfchooser = new JFileChooser();
        FileNameExtensionFilter supported = new FileNameExtensionFilter("Supported files", "xlsx");
        jfchooser.setFileFilter(supported);
        int option = jfchooser.showOpenDialog(null);
        String adress = null;
        if (option == JFileChooser.APPROVE_OPTION) {       //说明选定了一个文件
            adress = jfchooser.getSelectedFile().getPath();
        }
        String filename = adress;//读取离散点文件路径
        System.out.println("文件已经读入");

        List<Points> points1 = new ArrayList();

        List<Points> points2 = new ArrayList();

        Scanner scanner = new Scanner(System.in);
        //String filename="C:\\Users\\86195\\Desktop\\计算1.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filename));
        XSSFSheet sheet = workbook.getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        System.out.println(firstRowNum);
        System.out.println(lastRowNum);

        double Q=sheet.getRow(0).getCell(0).getNumericCellValue();//读取固定的Q
        Points cspoint=new Points();//初始下盘数据
        cspoint.x= Double.parseDouble(String.format("%.2f",sheet.getRow(1).getCell(5).getNumericCellValue()));
        cspoint.y=Double.parseDouble(String.format("%.2f",sheet.getRow(1).getCell(6).getNumericCellValue()));
        cspoint.z=Double.parseDouble(String.format("%.2f",sheet.getRow(1).getCell(7).getNumericCellValue()));
//        cspoint.y=sheet.getRow(1).getCell(6).getNumericCellValue();
//        cspoint.z=sheet.getRow(1).getCell(7).getNumericCellValue();
        points2.add(cspoint);

        for(int rowNum=1;rowNum<lastRowNum+2;rowNum++){
            Row rowData = sheet.getRow(rowNum);
            if(rowData!=null){
            Points point = new Points();
                point.x= Double.parseDouble(String.format("%.2f",rowData.getCell(1).getNumericCellValue()));
                point.y=Double.parseDouble(String.format("%.2f",rowData.getCell(2).getNumericCellValue()));
                point.z=Double.parseDouble(String.format("%.2f",rowData.getCell(3).getNumericCellValue()));
//            point.x=rowData.getCell(1).getNumericCellValue();
//            point.y=rowData.getCell(2).getNumericCellValue();
//            point.z=rowData.getCell(3).getNumericCellValue();
            points1.add(point);
            }
        }
        int count=points1.size();//记录多少行数据
        Points pointcount;
        double tQ=0;

        for(int i=0;i<count-1;i++){
             pointcount=get_panel(points1.get(i), points2.get(i), points1.get(i + 1),tQ);
             points2.add(pointcount);
        }
        String path =function3();
        writeTest(points2,path);




//        Row rowTitle = sheet.getRow(0);
//        if(rowTitle!=null){
//            int cellCout = rowTitle.getPhysicalNumberOfCells();
//                    for(int cellNum=0;cellNum<cellCout;cellNum++){
//                        Cell cell = rowTitle.getCell(cellNum);
//                        if(cell!=null){
//                            String cellValue =cell.getStringCellValue();
//                            System.out.printf(cellValue+"|");
//                        }
//                        //获取表中内容
//                       int rowCount=sheet.getLastRowNum();
//                        for(int rowNum = 1;rowNum<rowCount;rowNum++){
//                            Row rowData = sheet.getRow(rowNum);
//                            if(rowData!=null){
//                                //读取列
//                                int cellCount = rowTitle.getPhysicalNumberOfCells();
//                                for(int cellNum1=0;cellNum<cellCout;cellNum1++){
//                                    Cell cell1 = rowTitle.getCell(cellNum1);
//                                    if(cell1!=null){
//                                    System.out.print("["+(rowNum+1)+"-"+(cellNum1+1)+"]");}
//                                }
//                            }
//                        }
//
//                    }
//        }



////
//        Points p1 = new Points();
//        Points p2 = new Points();
//        Points p3 = new Points();
//        //p1.id=0;
//        p1.x=4095046.01;
//        p1.y=38537480.91;
//        p1.z=-127.78;
//
//      //  p2.id=1;
//        p2.x=4095096.10;
//        p2.y=38537389.41;
//        p2.z=-151.78;
//
//       // p3.id=2;
//        p3.x=4094986.62;
//        p3.y= 38537382.29;
//        p3.z= -124.22 ;
//        Points p;
//
//        p=get_panel(p1,p2,p3,24);
//        System.out.printf("X:"+p.x+"  Y:"+String.format("%.2f",p.y)+"  Z:"+p.z);
//        //String.format("%.2f",Fx.d)


    }

    private static void writeTest(List<Points> points, String path) throws IOException {

        //1、创建Workbook
        Workbook workbook = new XSSFWorkbook();
        //2、创建sheet,默认名字是sheet1，sheet2...
        Sheet sheet = workbook.createSheet("Sheel2");
        Row row0,row1,row2,row3;
        Cell cell0,cell1,cell2,cell3;
        for(int i=0;i<points.size();i++) {
            row0 = sheet.createRow(i+1);
            cell0 = row0.createCell(5);
            cell0.setCellValue(points.get(i).x);

            cell1 = row0.createCell(6);
            cell1.setCellValue(points.get(i).y);

            cell2 = row0.createCell(7);
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
    private static Points get_panel(Points p1, Points p2, Points p3, Double Q)
    {
        Fxishu Fx=new Fxishu();
        Points p=new Points();
        double M,N,K,G,P;
        //求平面方程，求平面ax+by+cz+d=0。
        //d=-a*x1-b*y1-c*z1。
        Fx.ax = ( (p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y) );

        Fx.by = ( (p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z) );

        Fx.cz = ( (p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x) );

        Fx.d= ( 0-(Fx.ax*p1.x+ Fx.by*p1.y+ Fx.cz*p1.z) );
        p.z=p3.z+Q;



        M=p3.x-p1.x;
        N=p3.y-p1.y;
        K=Fx.cz*p.z+Fx.d;
        G=(p3.z-p1.z)*(p3.z-p.z);
        P=G+M*p3.x+N*p3.y;
        p.x=(Fx.by*P+N*K)/(Fx.by*M-N* Fx.ax);
        p.y=(-K- Fx.ax*p.x)/ Fx.by;

        return p;

    }

    public void testread() throws Exception{
        Workbook workbook= new HSSFWorkbook();
      //  /new FileInputStream()

        Sheet sheet=workbook.createSheet("Summary");

    }
    private static String function3() {
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
