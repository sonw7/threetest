package com.zidong.drillsdate;

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


public class GeoDataLoader {

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
    public  List<drill>  readGeoVertices(String src)  {

        List<drill> drills = new ArrayList<>();
        File file = new File(src);
        try{
//           System.out.println(src);
            Workbook book = readExcelAb(src);
            Sheet sheet = book.getSheetAt(0);    //
            int rows = sheet.getLastRowNum();    //得到该sheet的行数
//           System.out.println(rows);
            Row row = sheet.getRow(0);
            Cell cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
            for (int i = 2; i <= rows; i++) {
                drill d= new  drill();

                row = sheet.getRow(i);
                if(row==null)
                    break;
//                if(row.getLastCellNum()<4)
//                    break;
                cell1 = row.getCell(0);    //表示获取第i行第1列的单元格
                if(cell1==null)
                    break;
                cell1 = row.getCell(1);    //表示获取第i行第2列的单元格
                d.name = cell1.getStringCellValue();
                cell1 = row.getCell(2);    //表示获取第i行第3列的单元格
                 d.x = cell1.getNumericCellValue();
                cell1 = row.getCell(3);     //表示获取第i行第4列的单元格
                d.y = cell1.getNumericCellValue();
                cell1 = row.getCell(4);     //表示获取第i行第5列的单元格
                d.z = cell1.getNumericCellValue();
                for(int m=5;m<29;m++){
                    int index=m-6;
                    cell1 = row.getCell(m);
                    d.layerTopValue.add(cell1.getNumericCellValue()/100);

                }

                //int tindex = Vertices.size()+5000;
                 d.x = (d.x-4090000)/100;
                 d.y = (d.y-38530000)/100;
                 d.z = d.z/100;
                 drills.add(d);

//                if(z==-1)
//                    continue;
//                if(z==0.0)
//                    z = -3.3;

            }

            book.close();
        }catch (Exception e) {
            e.printStackTrace();

        }


        return drills;
    }
}
