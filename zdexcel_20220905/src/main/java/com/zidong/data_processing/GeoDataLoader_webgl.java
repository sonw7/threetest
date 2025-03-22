package com.zidong.data_processing;

import com.zidong.drillsdate.vertex;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoDataLoader_webgl {
    public GeoDataLoader_webgl() {
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
    public  Workbook readExcelAb(String path) throws IOException {
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

    public double getCellValue(Cell cell ){
        double value=Double.MAX_VALUE;

        switch (cell.getCellType()) {
            case NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case STRING:
                String s = cell.getStringCellValue();
                if(s!=" "){

                    value = Double.parseDouble(s);
                }

                break;
        }
        return value;
    }


    public  List<vertex>  readGeoVertices(String src, List<vertex> Vertices,List<String> layername)  {
        System.out.println(src);
        List<vertex> vertices = new ArrayList<>();
        File file = new File(src);
        try{
//           System.out.println(src);
            Workbook book = readExcelAb(src);
            Sheet sheet = book.getSheetAt(0);    //
            int rows = sheet.getLastRowNum();    //得到该sheet的行数
//           System.out.println(rows);
            Row row = sheet.getRow(0);
            Cell cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
            for (int i = 1; i <= rows; i++) {

                row = sheet.getRow(i);
                if(row==null)
                    break;
                if(row.getLastCellNum()<4)
                    break;
                cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
                if(cell1==null)
                    break;

                //System.out.println(i);
                layername.add(String.valueOf(row.getCell(0)));
                cell1 = row.getCell(1);    //表示获取第i行第1列的单元格
                double x = Double.parseDouble(String.format("%.2f",getCellValue(cell1)));
                cell1 = row.getCell(2);     //表示获取第i行第1列的单元格
                double y = Double.parseDouble(String.format("%.2f", getCellValue(cell1)));
                cell1 = row.getCell(3);     //表示获取第i行第1列的单元格
              // System.out.println(cell1.getCellStyle().getDataFormat());
                double z = getCellValue(cell1);
                int tindex = Vertices.size()+5000;
                double tx,ty,tz;
//
//                 x = (x-4090000)/100;
//                 y = (y-38530000)/100;
//                 z = z/100;

//                tx = (x-4090000)/100;
//                ty = (y-38530000)/100;
//                tz = z/100;
//
//                x=tz;
//                y=ty;
//                z=tx;

//                if(z==-1)
//                    continue;
//                if(z==0.0)
//                    z = -3.3;

                vertex v = new vertex(x,y,z);

                vertices.add(v);
                Vertices.add(v);

            }

            book.close();
        }catch (Exception e) {
            e.printStackTrace();

        }


        return vertices;
    }
    public  List<vertex>  readGeoVertices(String src,List<vertex> Vertices,int col)  {

        List<vertex> vertices = new ArrayList<>();
        File file = new File(src);
        try{
//           System.out.println(src);
            Workbook book = readExcelAb(src);
            Sheet sheet = book.getSheetAt(0);    //
            int rows = sheet.getLastRowNum();    //得到该sheet的行数
//           System.out.println(rows);
            Row row = sheet.getRow(0);
            Cell cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
            for (int i = 1; i <= rows; i++) {

                row = sheet.getRow(i);
                if(row==null)
                    break;
                if(row.getLastCellNum()<4)
                    break;
                cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
                if(cell1==null)
                    break;
                cell1 = row.getCell(1);    //表示获取第i行第1列的单元格
                double x =getCellValue(cell1);
                cell1 = row.getCell(2);     //表示获取第i行第1列的单元格
                double y = getCellValue(cell1);
                cell1 = row.getCell(col);     //表示获取第i行第1列的单元格
                double z = getCellValue(cell1);
                int tindex = Vertices.size()+5000;

                x = (x-4090000)/100;
                y = (y-38530000)/100;
                z = z/100;

//                if(z==-1)
//                    continue;
//                if(z==0.0)
//                    z = -3.3;

                vertex v = new vertex(x,y,z);
                vertices.add(v);
                Vertices.add(v);

            }

            book.close();
        }catch (Exception e) {
            e.printStackTrace();

        }


        return vertices;
    }
}
