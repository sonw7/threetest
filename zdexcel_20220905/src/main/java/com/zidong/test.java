package com.zidong;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class test {

    public static void read(String fileName)
    {
        String[][] t1Array = new String[835][4];
        try{
            InputStream input=new FileInputStream(fileName);  //建立输入流
            HSSFWorkbook wb=new HSSFWorkbook(input);//初始化
            HSSFSheet sheet=wb.getSheetAt(0);//获得第一个表单
            //总行数
            int rowLength=sheet.getLastRowNum()+1;
            HSSFRow hssfRow=sheet.getRow(0);//得到Excel工作表的行
            int colLength=hssfRow.getLastCellNum();//总列数
            HSSFCell hssfCell=hssfRow.getCell(0);//得到Excel指定单元格中的内容
            CellStyle cellStyle=hssfCell.getCellStyle();//得到单元格样式
            for(int i=1;i<rowLength;i++)  //去掉表头
            {
                HSSFRow row=sheet.getRow(i);//获取Excel工作表的每行
                for(int j=0;j<colLength;j++)
                {
                    HSSFCell cell=row.getCell(j);//获取指定单元格
                    if(cell!=null)//将所有的需要读的Cell表格设置为String格式
                    {
                        //cell.setCellType(CellType.STRING);
                    }
                    if(j>0)
                    {
                        t1Array[i-1][j-1]=cell.getStringCellValue();
                    }
                    //System.out.println(cell.getStringCellValue());  //打印每行单元格中的数据
                }
                System.out.println(t1Array[i-1][0]+" "+t1Array[i-1][1]+" "+t1Array[i-1][2]+" "+t1Array[i-1][3]);//打印数组中的值
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
         read("C:\\Users\\86195\\Desktop\\计算.xlsx");
    }
}