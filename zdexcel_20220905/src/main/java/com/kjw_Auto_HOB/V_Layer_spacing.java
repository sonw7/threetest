package com.kjw_Auto_HOB;

import com.kjw_Auto_HOB.Class.GeoStructure;
import com.kjw_Auto_HOB.Layer_spacingclass.comheight;
import com.kjw_Auto_HOB.Layer_spacingclass.vertex;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
@SuppressWarnings("all")
public class V_Layer_spacing {
    static double globleScale = 100;
    private static double wgl_minx = 0.0;
    private static double wgl_miny = 0.0;
    private static double wgl_maxx = 0.0;
    private static double wgl_maxy = 0.0;
    private static double wgl_zScale = 1.0;

    public static void main(String[] args) throws IOException {
        File file1 = new File("C:\\Users\\86195\\Desktop\\层间距");//所读入的文件夹
        //用数组把文件夹下的文件存起来
        File[] files = file1.listFiles();
        //foreach遍历数组
        String[] filesname=new String[file1.listFiles().length];
        int im=0;
        for (File file : files) {
            //打印文件列表：只读取名称使用getName();
            //读入文件夹内所有文件夹的对应路径
            function(file.getPath());
            filesname[im]=file.getPath();
            im++;
        }

       // System.out.println("sssss");
    }
    public static List<vertex> function2(double[][] Originxyz, int layer1, int layer2, int rows) throws IOException {
//        double [] height= new double[rows];
//        int i=0;
//        for (double[] doubles : Originxyz) {
//            height[i]=get_geoModelVertexDIS(doubles[1],doubles[2],doubles[3],layer1,layer2);
//            i++;
//        }

        List<vertex> heightL =get_geoModelVertexDIS(Originxyz,layer1,layer2);



        System.out.println("sss");
        return heightL;


    }
    public static void function(String filename) throws IOException {
        //XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filename));
        Workbook workbook =  readExcelAb(filename);
        Sheet sheet = workbook.getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = getExcelRealRow(sheet)+1;

        int s;
        System.out.println(firstRowNum);
        System.out.println(lastRowNum);

        int rows = lastRowNum;
        String ctitle[] = new String[5];//存第一行的信息
        String rtitle[][] = new String[rows][2];//存前两列的信息
        int [] layer= new int[2];

        double Originxyz[][] = new double[rows-1][4];
        List<vertex> spacing = new ArrayList<>();

        Row row = sheet.getRow(0);
        Cell cell1 ;
        layer[0]= (int) row.getCell(5).getNumericCellValue();
        layer[1]= (int) row.getCell(6).getNumericCellValue();

        for( s=0;s<5;s++){
            cell1=row.getCell(s);
            ctitle[s]=cell1.getStringCellValue();
        }
        System.out.println("第一行信息保留");

        for( s=1; s<rows; s++){

            row=sheet.getRow(s);//获取行
            cell1=row.getCell(0);//获取列
            String cellValue = new DataFormatter().formatCellValue(row.getCell(0));
            rtitle[s-1][0]=cellValue;

            cell1=row.getCell(1);//获取列
            cellValue = new DataFormatter().formatCellValue(row.getCell(1));
            rtitle[s-1][1]=cellValue;
        }
        System.out.println("前两列信息保留");

        System.out.println("ctitle"+ctitle);
        System.out.println("rtitle"+rtitle);

//        for (rows = 0;  rows<rtitle.length ; rows++) {
//            if (rtitle[rows]=="")
//                break;
//        }
        for (int i = 1; i <rows; i++) {
            row=sheet.getRow(i);

            cell1 = row.getCell(1);    //表示获取第i行第2列的单元格
            Originxyz[i-1][0] = i;//当作id

            cell1 = row.getCell(2);    //表示获取第i行第3列的单元格
            Originxyz[i-1][1] = cell1.getNumericCellValue()-4090000;
//            X =(y-38530000)-5000;
//            Y = (x-4090000);
//            Z = z;
            cell1 = row.getCell(3);     //表示获取第i行第4列的单元格
//            Originxyz[i-1][2] = Double.parseDouble(String.format("%.2f",cell1.getNumericCellValue()));
            Originxyz[i-1][2] = (cell1.getNumericCellValue()-38530000)-5000;
            cell1 = row.getCell(4);     //表示获取第i行第5列的单元格
            Originxyz[i-1][3] = cell1.getNumericCellValue();
        }

        spacing=function2(Originxyz,layer[0],layer[1],rows);
        System.out.printf("查看结果");

        //String path =function3();
        writeTest(spacing, rows, filename,ctitle,rtitle,Originxyz,layer);
    }
    private static void writeTest(List<vertex> jieguo, int count, String MyPath, String[] ctitle, String[][] rtitle, double[][] Originxyz, int[] layer) throws IOException {

        FileInputStream fis = new FileInputStream(MyPath);

//2.创建一个工作簿
        Workbook workbook = readExcelAb(MyPath);
        Sheet sheet = workbook.getSheetAt(0);

        Row row0;
        Cell  cell;
        row0=sheet.createRow(0);
        //第一行前五列
        for(int c=0;c<ctitle.length;c++ ){
            cell=row0.createCell(c);
            cell.setCellValue(ctitle[c]);
        }

        //第一行5-7列
            cell=row0.createCell(5);
            cell.setCellType(CellType.NUMERIC);;
            cell.setCellValue(layer[0]);
            cell=row0.createCell(6);
            cell.setCellType(CellType.NUMERIC);;
            cell.setCellValue(layer[1]);

        //前两列
        for (int i = 0; i < count-1; i++) {
            row0 = sheet.createRow(i+1);
            cell=row0.createCell(0);//i+1行，1列
            cell.setCellValue(rtitle[i][0]);

            cell=row0.createCell(1);//i+1行，2列
            cell.setCellValue(rtitle[i][1]);
//            System.out.println("前两列信息填写完毕");

            cell=row0.createCell(2);//i+1行，3列
            cell.setCellValue(Originxyz[i][1]+4090000);
            cell=row0.createCell(3);//i+1行，4列
            cell.setCellValue(Originxyz[i][2]+38530000+5000);
            cell=row0.createCell(4);//i+1行，5列
            cell.setCellValue(Originxyz[i][3]);
//            System.out.println("前五列信息填写完毕");
            cell = row0.createCell(5);//i+1行，6列
            cell.setCellValue(jieguo.get(i).distance);


        }

        try {
            FileOutputStream fos = new FileOutputStream(MyPath);
            workbook.write(fos);
            workbook.close();
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
    public static List<vertex> get_geoModelVertexDIS(double[][] Originxyz, int layer1, int layer2) throws IOException {

        String srcLayer = "D:\\webgldocu\\zdexcel_20220905\\src\\main\\java\\com\\kjw_Auto_HOB\\OriginData\\Toplayer2.txt";
        String srctexture = "D:\\webgldocu\\zdexcel_20220905\\src\\main\\java\\com\\kjw_Auto_HOB\\OriginData\\texture.txt";
        List<GeoStructure> gss = initialLayers(srcLayer, srctexture);

        List<vertex> verdis = new ArrayList<>();
        int n;
        double X;
        double Y;
        double Z;
        GeoStructure colayer2 ;

        //判断顶底板
        if (layer1<layer2)
            colayer2 = gss.get(layer1 * 2 - 1);//取顶板
        else
            colayer2 = gss.get(layer2 * 2 - 2);//取底板
//        colayer2 = gss.get(layer1 * 2 - 5);


        comheight comheight = new comheight();
        List<vertex> vertices = new ArrayList<>();

//        X =(y-38530000)-5000;
//        Y = (x-4090000);
//        Z = z;
        for (int i = 0; i < Originxyz.length; i++) {
            X = Originxyz[i][2];//y
            Y = Originxyz[i][1];//x
            Z = Originxyz[i][3];//z
            vertices.add(dealvertex(X,Y,Z));
        }

//        double rX = X;
//        double rY = -Z;
//        double rZ = Y;
//        vertex v = new vertex(0, rX, rY, rZ);
//        System.out.println("x:"+X+"y:"+Y+"z:"+Z);
//        System.out.println("rx:"+rX+"ry:"+rY+"rz:"+rZ);
//            vertices.add(v);


        verdis = comheight.verifiedLayer(colayer2, vertices);

        for (vertex verdi : verdis) {
            verdi.distance = verdi.distance * 100;
        }



        return  verdis;


    }

    public static double get_geoModelVertexDIS1(double x, double y, double z, int layer1, int layer2) throws IOException {

        String srcLayer = "D:\\webgldocu\\zdexcel_20220905\\src\\main\\java\\com\\kjw_Auto_HOB\\OriginData\\Toplayer1.txt";
        String srctexture = "D:\\webgldocu\\zdexcel_20220905\\src\\main\\java\\com\\kjw_Auto_HOB\\OriginData\\texture.txt";
        List<GeoStructure> gss = initialLayers(srcLayer, srctexture);

        List<vertex> verdis = new ArrayList<>();
        int n;
        double X;
        double Y;
        double Z;
        GeoStructure colayer2 ;

        //判断顶底板
//        if (layer1<layer2)
//            colayer2 = gss.get(layer2 * 2 - 2);//取顶板
//        else
//            colayer2 = gss.get(layer2 * 2 - 1);//取底板
           colayer2 = gss.get(layer1 * 2 - 2);


        comheight comheight = new comheight();
        List<vertex> vertices = new ArrayList<>();

//        X =(y-38530000)-5000;
//        Y = (x-4090000);
//        Z = z;
        X =y;
        Y = x;
        Z = z;
        vertices.add(dealvertex(X,Y,Z));
//        double rX = X;
//        double rY = -Z;
//        double rZ = Y;
//        vertex v = new vertex(0, rX, rY, rZ);
//        System.out.println("x:"+X+"y:"+Y+"z:"+Z);
//        System.out.println("rx:"+rX+"ry:"+rY+"rz:"+rZ);
//            vertices.add(v);


        verdis = comheight.verifiedLayer(colayer2, vertices);



        return  verdis.get(0).distance*100;


    }
    public static List<GeoStructure> initialLayers(String src1, String src2) throws IOException {


//        double xlayer = 37436500.000000;
//        double ylayer = 4314000.000000;
//        double xroadway = 37436500.000000;
//        double yroadway = 4314000.000000;
//        double xltr = xlayer-xroadway;
//        double yltr = ylayer-yroadway;

        double xltr = 0;
        double yltr = 0 ;
        List<GeoStructure> rtn = new ArrayList<>();
        List<Double> rtvertices=new ArrayList<>();
        Scanner scanner2 = new Scanner(new File(src2));
        int  tnum = scanner2.nextInt();
        Map<Integer, String> texturePaths = new HashMap <Integer, String>();
        for(int j=0;j<tnum;++j)
        {
            int index = scanner2.nextInt();
            String texturePath = scanner2.next();
            texturePaths.put(index,texturePath);
        }


        Scanner scanner1 = new Scanner(new File(src1));
        int  num = scanner1.nextInt();
        for(int j=0; j<num;++j )
        {

            //input
            int pnum = scanner1.nextInt();
            int fnum = scanner1.nextInt();
            List<Double> tmpvertices = new ArrayList<Double>() ;//存点
            double value;
            for(int i=0;i<pnum*3;++i)
            {
                value = scanner1.nextDouble();
                if((i+1)%3==1)
                    value+=xltr;
                if((i+1)%3==2)
                    value+=yltr;
                tmpvertices.add(value);

            }

            List<Integer> tmpindices = new ArrayList<Integer>() ;//存三角形顶点序号
            for(int i=0;i<fnum;++i)
            {

                int tn = scanner1.nextInt();

                int flag0 = scanner1.nextInt();
                int flag1 = scanner1.nextInt();
                int flag2 = scanner1.nextInt();

                tmpindices.add(flag0);
                tmpindices.add(flag1);
                tmpindices.add(flag2);

            }

            //initial
            GeoStructure tmplayers = new  GeoStructure();

            tmplayers.name = "layer"+ String.valueOf(j+1);
            tmplayers.indices = tmpindices;

            double minx = Double.MAX_VALUE;
            double miny = Double.MAX_VALUE;
            double minz = Double.MAX_VALUE;
            double maxx = 0;
            double maxy = 0;
            double maxz = 0;

            for(int i=0;i<pnum;++i)
            {

                if (maxx < tmpvertices.get(i*3))
                {
                    maxx = tmpvertices.get(i*3);
                }
                if (maxy < tmpvertices.get(i*3+1))
                {
                    maxy = tmpvertices.get(i*3+1);
                }
                if (maxz < tmpvertices.get(i*3+2))
                {
                    maxz = tmpvertices.get(i*3+2);
                }
                if (minx > tmpvertices.get(i*3))
                {
                    minx = tmpvertices.get(i*3);
                }
                if (miny > tmpvertices.get(i*3+1))
                {
                    miny = tmpvertices.get(i*3+1);
                }
                if (minz > tmpvertices.get(i*3+2))
                {
                    minz = tmpvertices.get(i*3+2);
                }

            }

            double scale = globleScale;
            double xhorizon = maxx - minx;
            double yhorizon = maxy - miny;
            double zhorizon = maxz - minz;

            List<Integer> tmpnormals = new ArrayList<Integer>() ;
//            System.out.println(texturePaths.get(j+1));
            if( texturePaths.get(j+1)!=null)
            {
                tmplayers.texturePath = texturePaths.get(j+1);
                List<Double> tmpuvs = new ArrayList<Double>() ;
                for(int i=0;i<pnum;++i)
                {
                    tmpnormals.add(0);
                    tmpnormals.add(0);
                    tmpnormals.add(1);
                    double x =tmpvertices.get(i*3);
                    double y =tmpvertices.get(i*3+1);
                    double z =tmpvertices.get(i*3+2);
                    double u =(x-minx)/xhorizon;
                    double v =(y-miny)/yhorizon;
                    if (u > 1.0)
                    {
                        u = 1.0;
                    }
                    if (v > 1.0)
                    {
                        v = 1.0;
                    }

                    tmpuvs.add(u);
                    tmpuvs.add(v);

//                    double[] coorxy = MineModel.projectTransform(x+GlobalVariable.mm.getOriPoint().x,y+GlobalVariable.mm.getOriPoint().y,GlobalVariable.mm.getEpsg(),"EPSG:3857");
//                    x = coorxy[0];
//                    y = coorxy[1];
                    tmpvertices.set(i*3,(x - minx - (maxx-minx)/2)/scale);
                    tmpvertices.set(i*3+1,(z*wgl_zScale)/scale);
                    tmpvertices.set(i*3+2,-(y- miny - (maxy-miny)/2)/scale);
                }
                tmplayers.uvs = tmpuvs;
            }else{
                List<Double> tmpuvs = new ArrayList<Double>() ;
                tmplayers.uvs = tmpuvs;
                for(int i=0;i<pnum;++i)
                {
                    tmpnormals.add(0);
                    tmpnormals.add(0);
                    tmpnormals.add(1);
                    double x =tmpvertices.get(i*3);
                    double y =tmpvertices.get(i*3+1);
                    double z =tmpvertices.get(i*3+2);

//                    double[] coorxy = MineModel.projectTransform(x+GlobalVariable.mm.getOriPoint().x,y+GlobalVariable.mm.getOriPoint().y,GlobalVariable.mm.getEpsg(),"EPSG:3857");
//                    x = coorxy[0];
//                    y = coorxy[1];
                    tmpvertices.set(i*3,(x)/scale-40);
                    tmpvertices.set(i*3+1,z/scale);
                    tmpvertices.set(i*3+2,-(y )/scale+60);
                }

            }
            tmplayers.normals = tmpnormals;
            tmplayers.vertices = tmpvertices;
            for(int i=0;i<tmplayers.vertices.size()/3;i++){
                rtvertices.add(tmplayers.vertices.get(i*3+2));
                rtvertices.add(-tmplayers.vertices.get(i*3));
                rtvertices.add(tmplayers.vertices.get(i*3+1));
            }
            tmplayers.setRtvertices(rtvertices);
            rtn.add(tmplayers);
            rtvertices=new ArrayList<>();
        }

        return rtn;
    }
    private static vertex dealvertex(double x, double y, double z) {
        GeoStructure tmplayers = new  GeoStructure();

        int pnum=1;
        tmplayers.name = "layer";


        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double minz = Double.MAX_VALUE;
        double maxx = 0;
        double maxy = 0;
        double maxz = 0;

        List<Double> tmpvertices = new ArrayList<>();;
        List<Double> rtvertices=new ArrayList<>();
        tmpvertices.add(x);
        tmpvertices.add(y);
        tmpvertices.add(z);

        int i = 0;


        if (maxx < tmpvertices.get(i*3))
        {
            maxx = tmpvertices.get(i*3);
        }
        if (maxy < tmpvertices.get(i*3+1))
        {
            maxy = tmpvertices.get(i*3+1);
        }
        if (maxz < tmpvertices.get(i*3+2))
        {
            maxz = tmpvertices.get(i*3+2);
        }
        if (minx > tmpvertices.get(i*3))
        {
            minx = tmpvertices.get(i*3);
        }
        if (miny > tmpvertices.get(i*3+1))
        {
            miny = tmpvertices.get(i*3+1);
        }
        if (minz > tmpvertices.get(i*3+2))
        {
            minz = tmpvertices.get(i*3+2);
        }



        double scale = globleScale;
        double xhorizon = maxx - minx;
        double yhorizon = maxy - miny;
        double zhorizon = maxz - minz;

        List<Integer> tmpnormals = new ArrayList<Integer>() ;
//            System.out.println(texturePaths.get(j+1));
//        if( texturePaths.get(j+1)!=null)
        if( false)
        {
            tmplayers.texturePath = "texturePaths.get(j+1)";
            List<Double> tmpuvs = new ArrayList<Double>() ;
            for(int i1=0;i1<pnum;++i1)
            {
                tmpnormals.add(0);
                tmpnormals.add(0);
                tmpnormals.add(1);
                double x1 =tmpvertices.get(i1*3);
                double y1 =tmpvertices.get(i1*3+1);
                double z1 =tmpvertices.get(i1*3+2);
                double u =(x1-minx)/xhorizon;
                double v =(y1-miny)/yhorizon;
                if (u > 1.0)
                {
                    u = 1.0;
                }
                if (v > 1.0)
                {
                    v = 1.0;
                }

                tmpuvs.add(u);
                tmpuvs.add(v);

//                    double[] coorxy = MineModel.projectTransform(x+GlobalVariable.mm.getOriPoint().x,y+GlobalVariable.mm.getOriPoint().y,GlobalVariable.mm.getEpsg(),"EPSG:3857");
//                    x = coorxy[0];
//                    y = coorxy[1];
                tmpvertices.set(i1*3,(x1 - minx - (maxx-minx)/2)/scale);
                tmpvertices.set(i1*3+1,(z1*wgl_zScale)/scale);
                tmpvertices.set(i1*3+2,-(y1- miny - (maxy-miny)/2)/scale);
            }
            tmplayers.uvs = tmpuvs;
        }else{
            List<Double> tmpuvs = new ArrayList<Double>() ;
            tmplayers.uvs = tmpuvs;
            for(int i2=0;i2<pnum;++i2)
            {
                tmpnormals.add(0);
                tmpnormals.add(0);
                tmpnormals.add(1);
                double x1 =tmpvertices.get(i2*3);
                double y1 =tmpvertices.get(i2*3+1);
                double z1 =tmpvertices.get(i2*3+2);

//                    double[] coorxy = MineModel.projectTransform(x+GlobalVariable.mm.getOriPoint().x,y+GlobalVariable.mm.getOriPoint().y,GlobalVariable.mm.getEpsg(),"EPSG:3857");
//                    x = coorxy[0];
//                    y = coorxy[1];
                tmpvertices.set(i2*3,(x1)/scale-40);
                tmpvertices.set(i2*3+1,z1/scale);
                tmpvertices.set(i2*3+2,-(y1 )/scale+60);
            }

        }
        tmplayers.normals = tmpnormals;
        tmplayers.vertices = tmpvertices;
        for(int i3=0;i3<tmplayers.vertices.size()/3;i3++){
            rtvertices.add(tmplayers.vertices.get(i3*3+2));
            rtvertices.add(-tmplayers.vertices.get(i3*3));
            rtvertices.add(tmplayers.vertices.get(i3*3+1));
        }
        tmplayers.setRtvertices(rtvertices);

        //rtvertices=new ArrayList<>();

        vertex vertex = new vertex(0,rtvertices.get(0),rtvertices.get(1),rtvertices.get(2));
        List<vertex> vertices = new ArrayList<>();
        vertices.add(vertex);
        return vertex;
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
    public static int getExcelRealRow(Sheet sheet) {
        boolean flag = false;
        for (int i = 1; i <= sheet.getLastRowNum(); ) {
            Row r = sheet.getRow(i);
            if (r == null) {
                // 如果是空行（即没有任何数据、格式），直接把它以下的数据往上移动
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                continue;
            }
            flag = false;
            for (Cell c : r) {
                if (c.getCellType() != CellType.BLANK) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                i++;
                continue;
            } else {
                // 如果是空白行（即可能没有数据，但是有一定格式）
                if (i == sheet.getLastRowNum())// 如果到了最后一行，直接将那一行remove掉
                    sheet.removeRow(r);
                else//如果还没到最后一行，则数据往上移一行
                    sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
            }
        }
        return sheet.getLastRowNum();
    }
}
