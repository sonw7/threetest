package com.zidong;

import com.zidong.classdesign.Fxishu;
import com.zidong.classdesign.Points;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class shangxiapan {

    public static void main(String[] args) throws IOException {


        System.out.println("选择文件并读入");


        File file1 = new File("C:\\Users\\86195\\Desktop\\上下盘");//所读入的文件夹
        //用数组把文件夹下的文件存起来
        File[] files = file1.listFiles();
        //foreach遍历数组
        String[] filesname=new String[file1.listFiles().length];
        int im=0;
        for (File file2 : files) {

            filesname[im]=file2.getPath();//读入文件夹内所有文件夹的对应路径
            im++;
        }
        int in=0;
        for(int i=0;i<filesname.length;i++){

            //依次存取所有子文件夹下的表格文件
            File file = new File(filesname[i]);
            //用数组把子文件夹下的表格文件存起来
            File[] files1 = file.listFiles();


            String[] filesnames=new String[file.listFiles().length];
            in=0;
            for (File file3 : files1) {
                //打印文件列表：只读取名称使用getName();
                function(file3.getPath());
                System.out.println("路径："+file3.getPath());
                System.out.println("文件夹/文件名："+file3.getName());
                filesnames[in]=file3.getPath();//保存所有子文件表格的路径
                in++;
            }
            System.out.println("文件已经读入");
        }

        System.out.println("文件已经读入");


    }
    public static void function(String filename) throws IOException {
        System.out.println("文件已经读入");

        List<Points> points1 = new ArrayList();

        List<Points> points2 = new ArrayList();

        Scanner scanner = new Scanner(System.in);
        //String filename="C:\\Users\\86195\\Desktop\\计算1.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filename));
        XSSFSheet sheet = workbook.getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
//        int lastRowNum = sheet.getLastRowNum();
        int lastRowNum = 0;
        for (int num = 0; num <= sheet.getLastRowNum(); num++) {//跳过第一行，看个人需求
            XSSFRow row = sheet.getRow(num);
            if (row != null)
                lastRowNum++;
        }
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
            pointcount=get_panel(points1.get(i), points2.get(i), points1.get(i + 1),Q);
            points2.add(pointcount);
        }
        //String path =function3();
        writeTest(points2,filename);

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

    private static void writeTest(List<Points> points, String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);

//2.创建一个工作簿
        Workbook workbook = readExcelAb(path);
        Sheet sheet1 = workbook.getSheet("Sheet1");
        Sheet sheet = workbook.createSheet("第二页");

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
