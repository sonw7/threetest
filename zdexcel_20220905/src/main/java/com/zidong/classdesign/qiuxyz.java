package com.zidong.classdesign;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class qiuxyz {

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
        String filename = adress;
        System.out.println("文件已经读入"+filename);


        //String filename="C:\\Users\\86195\\Desktop\\计算1.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filename));
        XSSFSheet sheet = workbook.getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        System.out.println(firstRowNum);
        System.out.println(lastRowNum);

        int rows = sheet.getLastRowNum();    //得到该sheet的行数
//           System.out.println(rows);
        Row row = sheet.getRow(0);
        Cell cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
        double sxyz[][] = new double[rows][4];
        for (int i = 1; i <= rows; i++) {

            row = sheet.getRow(i);
            if (row == null)
                break;
            cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
            if (cell1 == null)
                break;

            cell1 = row.getCell(1);    //表示获取第i行第2列的单元格

            sxyz[i - 1][0] = cell1.getNumericCellValue();
            cell1 = row.getCell(2);    //表示获取第i行第3列的单元格
            sxyz[i - 1][1] = cell1.getNumericCellValue();
            cell1 = row.getCell(3);     //表示获取第i行第4列的单元格
            sxyz[i - 1][2] = cell1.getNumericCellValue();
            cell1 = row.getCell(4);     //表示获取第i行第5列的单元格
            sxyz[i - 1][3] = cell1.getNumericCellValue();
        }


        int count1 = 0;
        int i1 = 0;//记录有效行数
        while (sxyz[i1][0] != 0) {
            i1++;
            count1++;
            if (i1 >= sxyz.length)
                break;
        }
        int ban = count1 / 2;

        for (int i = 0; i < count1; i++)//第一个for循环控制着外面{}元素的个数即a.length
        {
            for (int j = 0; j < sxyz[i].length; j++)//第二个for循环控制着二维数组里面{}每个元素的个数，即a[i].length
            {
                System.out.print(sxyz[i][j] + " ");
            }
            System.out.println();//输出换行，能表示出二维数组有几行
        }
        double shangxyz[][] = new double[ban][3];
        double xiaxyz[][] = new double[ban][3];
        double yizhiz[][] = new double[ban][2];
        for (int i2 = 0; i2 < ban; i2++) {
            shangxyz[i2][0] = sxyz[i2][0];
            shangxyz[i2][1] = sxyz[i2][1];
            shangxyz[i2][2] = sxyz[i2][2];
//            xiaxyz[i2][0]=sxyz[i2][3];
//            xiaxyz[i2][1]=sxyz[i2][4];
//            xiaxyz[i2][2]=sxyz[i2][5];
            yizhiz[i2][0] = sxyz[i2][3];
//            yizhiz[i2][1]=sxyz[i2][7];
        }
        int m = 0;
        for (int i3 = count1 - 1; i3 >= ban; i3--) {
            xiaxyz[m][0] = sxyz[i3][0];
            xiaxyz[m][1] = sxyz[i3][1];
            xiaxyz[m][2] = sxyz[i3][2];
            yizhiz[m][1] = sxyz[i3][3];
            m++;

        }

        Double[][] ans = function(ban, shangxyz, xiaxyz);//返回参数值

        Double[][] jieguo = function2(ban, shangxyz, ans, yizhiz);
        System.out.printf("查看结果");

        //String path =function3();
        writeTest(jieguo, ban, filename);


    }

    public static void function(String filename) {

    }

    private static void writeTest(Double[][] jieguo, int count, String MyPath) throws IOException {

        FileInputStream fis = new FileInputStream(MyPath);

//2.创建一个工作簿
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet1 = workbook.getSheet("Sheet1");
        Sheet sheet = workbook.createSheet("第二页");
//3.通过行下标和列下标返回cell对象


//        //1、创建Workbook
//        Workbook workbook = new XSSFWorkbook();
//        //2、创建sheet,默认名字是sheet1，sheet2...
//        Sheet sheet = workbook.createSheet("Sheel2");
        Row row0, row1, row2, row3;
        Cell cell0, cell1, cell2, cell3;
        int jilu = 0;
        for (int i = 0; i < count; i++) {
            row0 = sheet.createRow(i + 1);
            cell0 = row0.createCell(5);
            cell0.setCellValue(jieguo[i][0]);

            // row0 = sheet.createRow(i+1);
            cell1 = row0.createCell(6);
            cell1.setCellValue(jieguo[i][1]);
            jilu++;

        }
        int i1 = count - 1;
        for (; i1 >= 0; i1--) {
            row0 = sheet.createRow(jilu + 1);
            cell0 = row0.createCell(5);
            cell0.setCellValue(jieguo[i1][2]);

            // row0 = sheet.createRow(i+1);
            cell1 = row0.createCell(6);
            cell1.setCellValue(jieguo[i1][3]);

            jilu++;

        }
        try {
            FileOutputStream fos = new FileOutputStream(MyPath);
            workbook.write(fos);
            fis.close();
//            FileOutputStream outputStream = new FileOutputStream(path);
//            //写入
//            workbook.write(outputStream);
//            //关闭输出流
//            outputStream.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static Double[][] function2(int count, double[][] a1, Double[][] b2, double[][] zz) {
        Double[][] c1 = new Double[count][4];

        int m = 0;
        for (; m < count; m++) {
            System.out.println("读入z值~");
            Double z1 = zz[m][0];
            Double z2 = zz[m][1];
            Double t1 = (z1 - a1[m][2]) / b2[m][2];
            int i;
            for (i = 0; i < 2; i++) {
                c1[m][i] = b2[m][i] * t1 + a1[m][i];
            }
            Double t2 = (z2 - a1[m][2]) / b2[m][2];
            for (int i1 = 0; i1 < 2; i1++) {
                c1[m][i] = b2[m][i1] * t2 + a1[m][i1];
                i++;
            }
            System.out.println("对应输入上z的x坐标为：");
            System.out.println(c1[m][0]);
            System.out.println("对应输入上z的y坐标为：");
            System.out.println(c1[m][1]);
            System.out.println("对应输入下z的x坐标为：");
            System.out.println(c1[m][2]);
            System.out.println("对应输入下z的y坐标为：");
            System.out.println(c1[m][3]);
        }
        return c1;

    }

    private static Double[][] function(int count, double[][] a, double[][] b) {
        Double[][] c = new Double[count][3];
        for (int m = 0; m < count; m++) {
            for (int i = 0; i < 3; i++) {
                c[m][i] = b[m][i] - a[m][i];
            }
            String[] ans = new String[3];
            for (int i = 0; i < 3; i++) {
                String op = a[m][i] > 0 ? "+" : "";
                if (i == 0) {
                    ans[i] = "x=" + c[m][i] + "t" + op + a[m][i];
                } else if (i == 1) {
                    ans[i] = "y=" + c[m][i] + "t" + op + a[m][i];
                } else {
                    ans[i] = "z=" + c[m][i] + "t" + op + a[m][i];
                }
            }
            for (int i = 0; i < 3; i++) {
                System.out.println(ans[i]);
            }
        }
        return c;
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
    public void getFile(String path, List<String> list) {
        File file = new File(path);
        //获取全部File
        //返回目录名加文件名
        //添加过滤器
//        String[] strings = file.list();
//        for (String string : strings) {
//            System.out.println(string);
//        }
        //这些路径名表示此抽象路径名所表示目录中的文件。
        File[] SWRTDATAfiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // TODO Auto-generated method stub
                // String s = pathname.getName().toLowerCase();
                String s = pathname.getName();
                //System.out.println(s);
                String reg = "(.XLSX|.xlsx|.XLS|.xls)$";
                Matcher matcher = Pattern.compile(reg).matcher(s);
                if(matcher.find()){
                    return true;
                }

                return false;
            }
        });
        for(File fs: SWRTDATAfiles){

            list.add(fs.getPath());
        }

    }
}