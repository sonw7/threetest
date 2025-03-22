package com.kjw_Auto_HOB;

import com.kjw_Auto_HOB.Class.*;
import com.kjw_Auto_HOB.Layer_spacingclass.vertex;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.poi.ss.usermodel.*;

@SuppressWarnings("all")
public class FaultIntersect {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\86195\\Desktop\\断层求交逆时针化");//所读入的文件夹
        //用数组把文件夹下的文件存起来
        File[] files = file.listFiles();//记录了所给文件夹下的所有子文件夹，和子文件夹里的所有文件
        //foreach遍历数组
        String[] filespathname=new String[file.listFiles().length];//记录了每个子文件夹的路径
        int im=0;//
        for (File file2 : files) {
            //打印文件列表：只读取名称使用getName();
            //读入文件夹内所有文件夹的对应路径
            filespathname[im]=file2.getPath();
            im++;
        }
        int in=0;

        //循环每一个文件夹,循环内部执行处理方法
        for(int i=0;i<filespathname.length;i++){
            List<Fault> faults=new ArrayList<>();
            Fault MainFault=new Fault();//存储主断层
            List<Vertex> counterclockwiseV=new ArrayList<>();
            List<Vertex> counterMFdelV=new ArrayList<>();


            //依次存取所有子文件夹下的表格文件
            File file1 = new File(filespathname[i]);
            //用数组把子文件夹下的表格文件存起来
            File[] files1 = file1.listFiles();


            String[] Excelfilespath=new String[file1.listFiles().length];
            String[] Excelfilesname=new String[file1.listFiles().length];
            in=0;
            for (File file3 : files1) {
                Fault caFault;

                Excelfilespath[in]=file3.getPath();
                Excelfilesname[in]=file3.getName().replace(".xlsx","");//保存所有子文件表格的路径

                caFault=SaveFaultinfor(file3.getPath(),getExtensionName(file3.getName()));
                if(caFault.isMF==false)
                    faults.add(caFault);
                else
                    MainFault=caFault;
                in++;
            }


            List<CVertexList> cVertexLists=new ArrayList<>();//存储交点及其逆序离散点，用于最后结果的整合
            List<CVertexList> cVertex;
            //处理每一个辅断层，
            int num;
            for (int j = 0; j < faults.size(); j++) {
                num=CountIntersectionNum(MainFault,faults.get(j));//num= 4 四个交点 十字形 2 两个交点分支形  1个单交点 0 无交点
                MainFault.vertexes=deleteMFvertex(MainFault,faults.get(j),counterclockwiseV,num);//记录需要删除的点

                //函数返回交点和其逆序离散点
               cVertex= handleintersect(MainFault,faults.get(j),num);
                for (CVertexList vertex : cVertex) {
                    cVertexLists.add(vertex);
                    //把每个返回的交点list 统一存入cVertexLists
                }



            }
            counterclockwiseV=MainFault.vertexes;
            //此处处理cVertexLists和counterclockwiseV的合并
            handleLineIntersect(counterclockwiseV,cVertexLists,filespathname[i]);//一个是主断层逆序及其应删除点，一个是记录了主次交点和辅断层逆序点

            //输出结果表格
            System.out.println("文件已经读入");
        }



    }
    public static List<CVertexList> handleLineIntersect(List<Vertex> counterclockwiseV, List<CVertexList> cVertexLists, String s) throws IOException {


        System.out.println("进行到了集成结果这一步");

        List<Vertex>vertices=new ArrayList<>();
        for (int i = 0; i < counterclockwiseV.size(); i++) {
            if(counterclockwiseV.get(i).isdelete!=true)
                vertices.add(counterclockwiseV.get(i));
            for (CVertexList cVertexList : cVertexLists) {
                if(cVertexList.Cvertex.insertIndex==i){
                    vertices.add(cVertexList.Cvertex);
                    for (Vertex vertex : cVertexList.vertices) {
                        vertices.add(vertex);
                    }
                }
            }

        }

        System.out.println("准备写入表格");
        writeTest(vertices, s);

        return null;
    }
    public static int CountIntersectionNum(Fault MainFault, Fault AssitFault){
        int num=0;
        for (int m = 0; m < MainFault.vertexes.size()-1; m++) {

            //主断层的一段线段开始循环，对每个辅断层线段进行计算
            for (int i = 0; i < AssitFault.vertexes.size()-1; i++) {

                //主断层线段
                double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};

                //辅断层线段
                double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                double[] end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};


                double[] res = intersection(start1, end1, start2, end2);

                //如果计算出有交点，则执行
                if(res.length!=0||res==null)
                    num++;//交点数加一
                System.out.println(Arrays.toString(res));

            }


        }

        return num;
    }
    public static List<Vertex> deleteMFvertex(Fault MainFault, Fault AssitFault, List<Vertex> counterclockwiseV, int num){
        int flag=0,zhuci=0;
        //此函数计算交点并保存应插入点之后的序号，以及主次交点间的离散点，返回CVertexList
        if(num==4||num==2){
            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size()-1; i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行
                    if(res.length!=0||res==null) {
                        if(zhuci==0)//出现主交点
                        {
                            zhuci = 1;
                            flag = m;//记录当前线段
                            continue;
                        }
                        if(zhuci==1&&flag==m){
                            //存在主交点且又出现交点，主交点所属的线段m与记录的flag一致，在同一线段无需要舍弃的
                            zhuci=0;
                            continue;
                        }
                        if(zhuci==1&&flag!=m)
                        {
                            //存在主交点，且与flag不一致则舍弃
                            MainFault.vertexes.get(m).isdelete = true;
                            zhuci=0;
                            continue;
                        }
                    }

                }
                if(zhuci==1&&flag!=m)//存在主交点了 且该线段不是主交点线段，且循环完都没有交点
                    MainFault.vertexes.get(m).isdelete = true;

            }}

        if(num==0){
            //如果无交点 只计算端点的距离
            Vertex [] distance=new Vertex[2];
            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                if(AssitFault.vertexes.get(i).type==2) {
                    AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);
                    if(distance[0]==null&&distance[1]==null)
                        distance[0]=AssitFault.vertexes.get(i);
                    else if (distance[0]!=null&&distance[1]==null)
                        distance[1]=AssitFault.vertexes.get(i);
                    else {
                        if (AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[0].distance<distance[1].distance)
                            distance[1]=AssitFault.vertexes.get(i);
                        else if(AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[0].distance>distance[1].distance)
                            distance[0]=AssitFault.vertexes.get(i);
                        else if(AssitFault.vertexes.get(i).distance<distance[1].distance)
                            distance[1]=AssitFault.vertexes.get(i);
                        else if(AssitFault.vertexes.get(i).distance>distance[1].distance)
                            continue;
                    }

                }
            }
            int [] countindex=new int[2];
            int zhuci1=0,flag1=0;//flag记录一个直线交到线段个数
            for (Vertex vertex : distance) {
                //先计算线段两端点是否在直线的两侧
                int i=vertex.id;
                int j;


                if(i+1==AssitFault.vertexes.size())
                    j=i-1;
                else
                    j=i+1;
                for (int k = 0; k < MainFault.vertexes.size()-1; k++) {
                    double[] start1 = new double[]{MainFault.vertexes.get(k).x, MainFault.vertexes.get(k).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(k+1).x, MainFault.vertexes.get(k+1).y};
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(j).x, AssitFault.vertexes.get(j).y};
                    double[] res = intersection(start1, end1, start2, end2,1);
                    if(res.length!=0||res==null) {
                        if(zhuci1==0&&flag1==0)
                        {
                            countindex[0]=k;
                            flag1=1;
                            continue;
                        }
                        if(zhuci1==0&&flag1==1){
                            zhuci1=1;
                            flag1=0;
                            countindex[0]=comparedis(vertex,MainFault.vertexes.get(countindex[0]),MainFault.vertexes.get(k+1));
                            continue;
                        }
                        if(zhuci1==1&&flag1==0){
                            countindex[1]=k;
                            flag1=1;
                            continue;
                        }
                        if(zhuci1==1&&flag1==1){
                            zhuci1=0;
                            flag1=0;
                            countindex[1]=comparedis(vertex,MainFault.vertexes.get(countindex[1]),MainFault.vertexes.get(k+1));
                            continue;
                        }
                        System.out.println("计算出交点");
                    }
                }


                System.out.println("111");
            }
            int [] indexs=new int[2];
            if(countindex[0]<countindex[1])
            {
                indexs[0]=countindex[0];
                indexs[1]=countindex[1];
            }else {
                indexs[0]=countindex[1];
                indexs[1]=countindex[0];
            }
            if(indexs[0]==indexs[1]){
                return MainFault.vertexes;
            }else {
                for (int i = indexs[0]+1; i <indexs[1]+1 ; i++) {
                    MainFault.vertexes.get(i).isdelete=true;
                }
            }

            System.out.println("无交点的处理");
        }



        if(num==1){
            Vertex [] distance=new Vertex[2];
            //int countindex;
            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size()-1; i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行
                    if(res.length!=0||res==null) {
                        Vertex vertex0=new Vertex();
                        vertex0.id=i;//该交点是由辅断层第i个和第i+1个求出的。
                        //存储交点
                        vertex0.x=res[0];
                        vertex0.y=res[1];
                        vertex0.z=0;
                        vertex0.insertIndex=m;//该交点是由主断层第m个和第m+1个求出的。
                        distance[0]=vertex0;
                        //因为只有一个交点
                        break;
                    }

                }
            }
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    if(AssitFault.vertexes.get(i).type==2&&AssitFault.vertexes.get(i).id!=distance[0].id) {
                        AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);
                        if(distance[1]==null)
                            distance[1]=AssitFault.vertexes.get(i);
                        else {
                            if (AssitFault.vertexes.get(i).distance<distance[1].distance)
                                distance[1]=AssitFault.vertexes.get(i);
                            else
                            {
                                System.out.println("22");
                                continue;
                            }
                        }
                    }
                }
                //上一步已经找出那个未相交距离最近的端点，存在distance【1】
                int [] countindex=new int[2];
                countindex[0]=distance[0].insertIndex;
                int i=distance[1].id,j;
                if(i+1==AssitFault.vertexes.size())
                    j=i-1;
                else
                    j=i+1;

                int flag1=0;//flag记录一个直线交到线段个数

                    for (int k = 0; k < MainFault.vertexes.size()-1; k++) {
                        double[] start1 = new double[]{MainFault.vertexes.get(k).x, MainFault.vertexes.get(k).y};
                        double[] end1 = new double[]{MainFault.vertexes.get(k+1).x, MainFault.vertexes.get(k+1).y};
                        //辅断层线段
                        double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                        double[] end2 = new double[]{AssitFault.vertexes.get(j).x, AssitFault.vertexes.get(j).y};
                        double[] res = intersection(start1, end1, start2, end2,1);//第五个参数：函数计算是否交点在线段间
                        if(res.length!=0||res==null) {
                            if(flag1==0)
                            {
                                countindex[1]=k;
                                flag1=1;
                                continue;
                            }
                            if(flag1==1){
                                flag1=0;
                                countindex[1]=comparedis(distance[1],MainFault.vertexes.get(countindex[1]),MainFault.vertexes.get(k+1));
                                break;
                            }

                        }
                    }
                /*------------------------------------------------------*/

                int [] indexs=new int[2];
                if(countindex[0]<countindex[1])
                {
                    indexs[0]=countindex[0];
                    indexs[1]=countindex[1];
                }else {
                    indexs[0]=countindex[1];
                    indexs[1]=countindex[0];
                }

                if(indexs[0]==indexs[1]){
                    return MainFault.vertexes;
                }else {
                    for (int i0 = indexs[0]+1; i0 <indexs[1]+1 ; i0++) {
                        MainFault.vertexes.get(i0).isdelete=true;
                    }
                }

                System.out.println("无交点的处理");

            }



        System.out.println("计算需要删除的点");
        return MainFault.vertexes;
    }
    public static int comparedis(Vertex vertex,Vertex vertex1,Vertex vertex2){
        int index;
        Double X,Y,X1,Y1,X2,Y2;
        X=vertex.x;
        Y=vertex.y;
        X1=vertex1.x;
        Y1=vertex1.y;
        X2=vertex2.x;
        Y2=vertex2.y;
        double dis1=Math.sqrt((X-X1)*(X-X1)+(Y-Y1)*(Y-Y1));
        double dis2=Math.sqrt((X-X2)*(X-X2)+(Y-Y2)*(Y-Y2));
        if(dis1>dis2)
            index=vertex2.id-1;
        else
            index=vertex1.id;
        return index;
    }
    public static boolean compare(Vertex vertex,Vertex vertex1,Vertex vertex2){
        int index;
        Double X,Y,X1,Y1,X2,Y2;
        X=vertex.x;
        Y=vertex.y;
        X1=vertex1.x;
        Y1=vertex1.y;
        X2=vertex2.x;
        Y2=vertex2.y;
        double dis1=Math.sqrt((X-X1)*(X-X1)+(Y-Y1)*(Y-Y1));
        double dis2=Math.sqrt((X-X2)*(X-X2)+(Y-Y2)*(Y-Y2));
        if(dis1>dis2)
            return true;
        else
            return false;//原有点胜利

    }
    public static double comdistance(Vertex vertex,List<Vertex> vertexList){
        double mindistance=0;
        for (int i = 0; i < vertexList.size()-1; i++) {
            if(mindistance==0){
                if(vertexList.get(i).type!=2||vertexList.get(i+1).type!=2){
                mindistance=comditance(vertex,vertexList.get(i),vertexList.get(i+1));
                }
            }else
            {
                if(vertexList.get(i).type==2&&vertexList.get(i+1).type==2){
                    continue;
                }else
                {
                    if(mindistance>comditance(vertex,vertexList.get(i),vertexList.get(i+1)))
                    {
                        mindistance=comditance(vertex,vertexList.get(i),vertexList.get(i+1));
                    }else
                        continue;
                }
            }
        }

        return mindistance;
    }
    public static double comditance(Vertex vertex,Vertex vertex1,Vertex vertex2){
        double distance;
        Double X,Y,X1,Y1,X2,Y2;
        X=vertex.x;
        Y=vertex.y;
        X1=vertex1.x;
        Y1=vertex1.y;
        X2=vertex2.x;
        Y2=vertex2.y;

        double A = Y2 - Y1;
        double B = X1 - X2;
        double C = X2*Y1 - X1*Y2;
        distance=Math.abs(A*X+B*Y+C)/Math.sqrt(A*A+B*B);
        return distance;
    }
    public static List<CVertexList> handleintersect(Fault MainFault, Fault AssitFault,  int num){
        //此函数计算交点并保存应插入点之后的序号，以及主次交点间的离散点，返回CVertexList
        List<Vertex> AssitVertex=AssitFault.vertexes;//辅断层的所有点

        int zhuci=0;//flag记录主断层同一线段的交点个数，主次记录主次点赋予

        List<CVertexList> cVertexLists=new ArrayList<>();//记录交点集合
        CVertexList Cvertex = null;
        Vertex vertex;
        if(num==4||num==2){
            //如果为十字型四个交点

            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                if(zhuci==0){
                    Cvertex = null;
                    Cvertex=new CVertexList();//如果没主交点出现则新设交点集变量
                    Cvertex.vertices=new ArrayList<>();
                     }
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size()-1; i++) {

                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};

                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};


                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行

                    if(res.length!=0||res==null) {
                        if(zhuci==0){
                            Vertex vertex1=new Vertex();
                            vertex1.x=res[0];
                            vertex1.y=res[1];
                            vertex1.z=0;
                            vertex1.isMainCV=true;
                            vertex1.insertIndex=m;
                            vertex1.Ctype=true;
                            vertex1.AssisinserIndexMain=i+1;
                            //如果是主交点，则开始赋值主交点
                             Cvertex.Cvertex=vertex1;//分别赋值xy

                             zhuci++;

                        }else if(zhuci==1)
                        {
                            //次交点存储
                            vertex=new Vertex();
                            //交点设置为次交点
                            vertex.isMainCV=false;
                            vertex.x=res[0];
                            vertex.y=res[1];
                            vertex.Ctype=true;
                            vertex.AssisinserIndexAssi=i;
                            vertex.z=0;
                            if(Cvertex.Cvertex.AssisinserIndexMain<i+1){
                            for (int j = Cvertex.Cvertex.AssisinserIndexMain; j <i+1 ; j++) {
                                //存入主次交点间的离散点
                                Cvertex.vertices.add(AssitFault.vertexes.get(j));

                            }}else
                            {
                                for (int j = Cvertex.Cvertex.AssisinserIndexMain; j <AssitFault.vertexes.size() ; j++) {
                                    //存入主次交点间的离散点
                                    Cvertex.vertices.add(AssitFault.vertexes.get(j));

                                }
                                for (int j = 0; j <=i; j++) {
                                    Cvertex.vertices.add(AssitFault.vertexes.get(j));
                                }
                            }
                            Cvertex.vertices.add(vertex);
                            cVertexLists.add(Cvertex);
                            zhuci=0;
                        }
                        //0为主交点，1为次交点
                    }
//                    if(zhuci==1)
//                        Cvertex.vertices.add(AssitFault.vertexes.get(i+1));

                    System.out.println(Arrays.toString(res));


                }


            }}
        if(num==0){
            Cvertex = null;
            Cvertex=new CVertexList();//如果没主交点出现则新设交点集变量
            Cvertex.Cvertex=new Vertex();
            Cvertex.vertices=new ArrayList<>();
            //如果无交点 只计算端点的距离
            Vertex [] distance=new Vertex[2];
            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                if(AssitFault.vertexes.get(i).type==2) {
                    AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);
                    if(distance[0]==null&&distance[1]==null)
                        distance[0]=AssitFault.vertexes.get(i);
                    else if (distance[0]!=null&&distance[1]==null)
                        distance[1]=AssitFault.vertexes.get(i);
                    else {
                        if (AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[0].distance<distance[1].distance)
                            distance[1]=AssitFault.vertexes.get(i);
                        else if(AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[0].distance>distance[1].distance)
                            distance[0]=AssitFault.vertexes.get(i);
                        else if(AssitFault.vertexes.get(i).distance<distance[1].distance)
                            distance[1]=AssitFault.vertexes.get(i);
                        else if(AssitFault.vertexes.get(i).distance>distance[1].distance)
                            continue;
                    }

                }
            }
            int [] countindex=new int[2];

            int zhuci1=0,flag1=0;//flag记录一个直线交到线段个数
            for (Vertex vertex1 : distance) {
                //先计算线段两端点是否在直线的两侧
                int i=vertex1.id;
                int j;
                vertex1.Cvertex=new Vertex();


                if(i+1==AssitFault.vertexes.size())
                    j=i-1;
                else
                    j=i+1;
                for (int k = 0; k < MainFault.vertexes.size()-1; k++) {
                    double[] start1 = new double[]{MainFault.vertexes.get(k).x, MainFault.vertexes.get(k).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(k+1).x, MainFault.vertexes.get(k+1).y};
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(j).x, AssitFault.vertexes.get(j).y};
                    double[] res = intersection(start1, end1, start2, end2,1);
                    if(res.length!=0||res==null) {
                        if(zhuci1==0&&flag1==0)
                        {
                            vertex1.insertIndex=k;
                            vertex1.Cvertex.x=res[0];
                            vertex1.Cvertex.y=res[1];
                            vertex1.Cvertex.z=0;
                            flag1=1;
                            continue;
                        }
                        if(zhuci1==0&&flag1==1){
                            zhuci1=1;
                            flag1=0;
                            if(compare(vertex1,MainFault.vertexes.get(vertex1.insertIndex),MainFault.vertexes.get(k+1))){
                                vertex1.insertIndex=k;
                                vertex1.Cvertex.x=res[0];
                                vertex1.Cvertex.y=res[1];
                            }
                            continue;
                        }
                        if(zhuci1==1&&flag1==0){
                            vertex1.insertIndex=k;
                            vertex1.Cvertex.x=res[0];
                            vertex1.Cvertex.y=res[1];
                            vertex1.Cvertex.z=0;
                            flag1=1;
                            continue;
                        }
                        if(zhuci1==1&&flag1==1){
                            zhuci1=0;
                            flag1=0;
                            if(compare(vertex1,MainFault.vertexes.get(vertex1.insertIndex),MainFault.vertexes.get(k+1))){
                                vertex1.insertIndex=k;
                                vertex1.Cvertex.x=res[0];
                                vertex1.Cvertex.y=res[1];
                            }
                            continue;
                        }
                        System.out.println("计算出交点");
                    }
                }

            }

            if(distance[0].insertIndex<distance[1].insertIndex){
                distance[0].Cvertex.isMainCV=true;
                distance[0].Cvertex.insertIndex=distance[0].insertIndex;
                Cvertex.Cvertex=distance[0].Cvertex;
                Cvertex.vertices.add(distance[0]);
                if(distance[0].id<distance[1].id){
                    for (int i = distance[0].id+1; i < distance[1].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[1].Cvertex);
                }else {
                    for (int i = distance[0].id+1; i <AssitFault.vertexes.size() ; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    for (int i = 0; i < distance[1].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[1].Cvertex);
                }

            }else {
                distance[1].Cvertex.isMainCV=true;
                distance[1].Cvertex.insertIndex=distance[1].insertIndex;
                Cvertex.Cvertex=distance[1].Cvertex;
                Cvertex.vertices.add(distance[1]);
                if(distance[1].id<distance[0].id){
                    for (int i = distance[1].id+1; i < distance[0].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[0].Cvertex);
                }else {
                    for (int i = distance[1].id+1; i <AssitFault.vertexes.size() ; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    for (int i = 0; i < distance[0].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[0].Cvertex);
                }
            }
            System.out.println("无交点的处理");
            cVertexLists.add(Cvertex);
        }
        if(num==1){
            Cvertex = null;
            Cvertex=new CVertexList();//如果没主交点出现则新设交点集变量
            Cvertex.Cvertex=new Vertex();
            Cvertex.vertices=new ArrayList<>();
            //如果无交点 只计算端点的距离
            Vertex [] distance=new Vertex[2];
            //int countindex;
            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size()-1; i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行
                    if(res.length!=0||res==null) {
                        Vertex vertex0=new Vertex();
                        vertex0.id=i;//该交点是由辅断层第i个和第i+1个求出的。
                        //存储交点
                        vertex0.x=res[0];
                        vertex0.y=res[1];
                        vertex0.z=0;
                        vertex0.insertIndex=m;//该交点是由主断层第m个和第m+1个求出的。
                        distance[0]=AssitFault.vertexes.get(i);
                        distance[0].insertIndex=m;
                        distance[0].Cvertex=vertex0;
                        //因为只有一个交点
                        break;
                    }

                }
            }
            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                if(AssitFault.vertexes.get(i).type==2&&AssitFault.vertexes.get(i).id!=distance[0].id) {
                    AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);
                    if(distance[1]==null){
                        distance[1]=AssitFault.vertexes.get(i);
                        distance[1].Cvertex=new Vertex();
                    }
                    else {
                        if (AssitFault.vertexes.get(i).distance<distance[1].distance){
                            distance[1]=AssitFault.vertexes.get(i);
                            distance[1].Cvertex=new Vertex();
                        }
                        else
                        {
                            System.out.println("22");
                            continue;
                        }
                    }
                }
            }
            //上一步已经找出那个未相交距离最近的端点，存在distance【1】
            int [] countindex=new int[2];
            countindex[0]=distance[0].insertIndex;
            int i0=distance[1].id,j0;
            if(i0+1==AssitFault.vertexes.size())
                j0=i0-1;
            else
                j0=i0+1;

            int flag1=0;//flag记录一个直线交到线段个数

            for (int k = 0; k < MainFault.vertexes.size()-1; k++) {
                double[] start1 = new double[]{MainFault.vertexes.get(k).x, MainFault.vertexes.get(k).y};
                double[] end1 = new double[]{MainFault.vertexes.get(k+1).x, MainFault.vertexes.get(k+1).y};
                //辅断层线段
                double[] start2 = new double[]{AssitFault.vertexes.get(i0).x, AssitFault.vertexes.get(i0).y};
                double[] end2 = new double[]{AssitFault.vertexes.get(j0).x, AssitFault.vertexes.get(j0).y};
                double[] res = intersection(start1, end1, start2, end2,1);//第五个参数：函数计算是否交点在线段间
                if(res.length!=0||res==null) {
                    if(flag1==0)
                    {
                        distance[1].insertIndex=countindex[1]=k;
                        distance[1].Cvertex=new Vertex();
                        distance[1].Cvertex.x=res[0];
                        distance[1].Cvertex.y=res[1];
                        distance[1].Cvertex.z=0;

                        flag1=1;
                        continue;
                    }
                    if(flag1==1){
                        distance[1].Cvertex=new Vertex();
                        distance[1].insertIndex=countindex[1]=comparedis(distance[1],MainFault.vertexes.get(countindex[1]),MainFault.vertexes.get(k+1));
                        distance[1].Cvertex.x=res[0];
                        distance[1].Cvertex.y=res[1];


                        break;
                    }

                }
            }
            if(distance[0].insertIndex<distance[1].insertIndex){
                distance[0].Cvertex.isMainCV=true;
                distance[0].Cvertex.insertIndex=distance[0].insertIndex;
                Cvertex.Cvertex=distance[0].Cvertex;
                Cvertex.vertices.add(distance[0]);
                if(distance[0].id<distance[1].id){
                    for (int i = distance[0].id+1; i < distance[1].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[1].Cvertex);
                }else {
                    for (int i = distance[0].id+1; i <AssitFault.vertexes.size() ; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    for (int i = 0; i < distance[1].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[1].Cvertex);
                }

            }else {
                distance[1].Cvertex.isMainCV=true;
                distance[1].Cvertex.insertIndex=distance[1].insertIndex;
                Cvertex.Cvertex=distance[1].Cvertex;
                Cvertex.vertices.add(distance[1]);
                if(distance[1].id<distance[0].id){
                    for (int i = distance[1].id+1; i < distance[0].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[0].Cvertex);
                }else {
                    for (int i = distance[1].id+1; i <AssitFault.vertexes.size() ; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    for (int i = 0; i < distance[0].id+1; i++) {
                        Cvertex.vertices.add(AssitFault.vertexes.get(i));
                    }
                    Cvertex.vertices.add(distance[0].Cvertex);
                }
            }
            System.out.println("无交点的处理");
            cVertexLists.add(Cvertex);

        }
        return cVertexLists;
    }
    public static String getExtensionName(String filename) {

        if ((filename != null) && (filename.length() > 0)) {

            int dot = filename.lastIndexOf('.');

            if ((dot >-1) && (dot < (filename.length()))) {

                return filename.substring(0, dot);

            }

        }


        return filename;

    }

    public static Fault SaveFaultinfor(String path, String name) throws IOException {
        int ispinch;
        boolean MFtype;
        List<Vertex> vertices = new ArrayList<>();//记录离散点数据


        Workbook workbook = readExcelAb(path);

        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        int realRowNum = getExcelRealRow(sheet);
        System.out.println("lastRowNum"+lastRowNum+"realRowNum"+realRowNum);

        Row row = sheet.getRow(0);
        Cell cell =row.getCell(0) ;
        ispinch=pinchname(cell.getStringCellValue());
        MFtype=isMF(cell.getStringCellValue());
        Cell cell1;

        for (int i = 1; i <=realRowNum; i++) {
            Vertex vertex = new Vertex();
            row=sheet.getRow(i);
            cell = row.getCell(1);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setX(cell.getNumericCellValue());

            cell=row.getCell(2);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setY(cell.getNumericCellValue());
            cell=row.getCell(3);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setZ(cell.getNumericCellValue());
            cell=row.getCell(4);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setType ((int) cell.getNumericCellValue());
            vertex.id=i-1;
            vertices.add(vertex);
        }

//        cell1=row.getCell(s);
//        ctitle[s-1]=cell1.getStringCellValue();


        Fault fault = new Fault(MFtype,name,vertices,ispinch);


        return fault;
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

    public static int pinchname(String ispinchname){
        int ispinch;
        if(ispinchname.equals("MF")) {
            System.out.println("等于");
            return 0;
        }
        if(ispinchname.equals("AF")) {
            System.out.println("等于");
            return 1;
        }
        if(ispinchname.equals("AFS")) {
            System.out.println("等于");
            return 2;
        }
        if(ispinchname.equals("AFD")) {
            System.out.println("等于");
            return 3;
        }

//        switch (ispinchname){
//            case "MF":
//                ispinch=0;
//                break;
//            case "AF":
//                ispinch= 1;
//                break;
//            case "AFS":
//                ispinch= 2;
//                break;
//            case "AFD":
//                ispinch= 3;
//                break;
//            default:
//                System.out.println("文件尖灭符号填写有误");
//                ispinch= 99;
//                break;
//        }

            /*
            * 主断层：MF  =0
            * 辅断层无尖灭点：AF=1
            * 辅断层有一个尖灭点：AFS=2
            * 辅断层有两个尖灭点：AFD=3
            * */


      return 0;
    }
    public static boolean isMF(String ispinchname) {
        boolean isMF;
        String MF="MF";
        if(ispinchname.equals(MF)) {
            System.out.println("等于");
            return true;
        }
        else
            return false;

    }
    public static double[] intersection(double[] start1_, double[] end1_, double[] start2_, double[] end2_) {

        // 为了避免给后面使用造成不便，修改了入参声明

        Point[] points = checkAndConvertIntoPoint(start1_, end1_, start2_, end2_);
        Point start1 = points[0];
        Point end1 = points[1];
        Point start2 = points[2];
        Point end2 = points[3];

        // 封装成直线类，以便计算斜率和截距
        Line line1 = new Line(start1, end1);
        Line line2 = new Line(start2, end2);

        // 误差精度
        double epslion = 1e-6f;
        // 两条直线的交点（如果存在的话）
        Point intersection;

        // 情况 1：（特殊情况）两条直线有一条斜率为正无穷
        if (line1.k == Integer.MAX_VALUE || line2.k == Integer.MAX_VALUE) {

            // 两条直线斜率都不存在时
            if (line1.k == Integer.MAX_VALUE && line2.k == Integer.MAX_VALUE) {

                // 这里讨论两条直线重合的情况， b 不是截距的意思，而是表示 x = a 这条线段
                if (Math.abs(line1.b - line2.b) <= epslion && (isBetween(start1, start2, end1)) || isBetween(start2, start1, end2)) {
                    if (isBetween(start1, start2, end1)) {
                        return new double[]{start2.x, start2.y};
                    } else {
                        return new double[]{start1.x, start1.y};
                    }
                }
            }

            // 其中一条直线斜率不存在
            if (line1.k == Integer.MAX_VALUE) {
                intersection = new Point(line1.b, line1.b * line2.k + line2.b);
            } else {
                intersection = new Point(line2.b, line2.b * line1.k + line1.b);
            }

        } else if (Math.abs(line1.k - line2.k) <= epslion) {
            // 情况 2：（特殊情况）斜率相等的情况下，如果在 y 轴上的截距相等，就表示两条直线重合
            if (Math.abs(line1.b - line2.b) <= epslion && isBetween(start1, start2, end1)) {
                return new double[]{start2.x, start2.y};
            }
            return new double[0];
        } else {
            // 情况 3：（一般情况）使用公式计算交点的坐标
            double x = (line2.b - line1.b) / (line1.k - line2.k);
            double y = x * line1.k + line1.b;

            intersection = new Point(x, y);
        }

        // 检测所在直线的交点是否在两条线段的横纵坐标范围之内
        if (isBetween(start1, intersection, end1) && isBetween(start2, intersection, end2)) {
            return new double[]{intersection.x, intersection.y};
        }
        return new double[0];
    }

    public static double[] intersection(double[] start1_, double[] end1_, double[] start2_, double[] end2_,int num) {

        //求直线与线段的交点，前两个是线段端点，后两个是直线两点
        // 为了避免给后面使用造成不便，修改了入参声明

        Point[] points = checkAndConvertIntoPoint(start1_, end1_, start2_, end2_);
        Point start1 = points[0];
        Point end1 = points[1];
        Point start2 = points[2];
        Point end2 = points[3];

        // 封装成直线类，以便计算斜率和截距
        Line line1 = new Line(start1, end1);
        Line line2 = new Line(start2, end2);

        // 误差精度
        double epslion = 1e-6f;
        // 两条直线的交点（如果存在的话）
        Point intersection;

        // 情况 1：（特殊情况）两条直线有一条斜率为正无穷
        if (line1.k == Integer.MAX_VALUE || line2.k == Integer.MAX_VALUE) {

            // 两条直线斜率都不存在时
            if (line1.k == Integer.MAX_VALUE && line2.k == Integer.MAX_VALUE) {

                // 这里讨论两条直线重合的情况， b 不是截距的意思，而是表示 x = a 这条线段
                if (Math.abs(line1.b - line2.b) <= epslion && (isBetween(start1, start2, end1)) || isBetween(start2, start1, end2)) {
                    if (isBetween(start1, start2, end1)) {
                        return new double[]{start2.x, start2.y};
                    } else {
                        return new double[]{start1.x, start1.y};
                    }
                }
            }

            // 其中一条直线斜率不存在
            if (line1.k == Integer.MAX_VALUE) {
                intersection = new Point(line1.b, line1.b * line2.k + line2.b);
            } else {
                intersection = new Point(line2.b, line2.b * line1.k + line1.b);
            }

        } else if (Math.abs(line1.k - line2.k) <= epslion) {
            // 情况 2：（特殊情况）斜率相等的情况下，如果在 y 轴上的截距相等，就表示两条直线重合
            if (Math.abs(line1.b - line2.b) <= epslion && isBetween(start1, start2, end1)) {
                return new double[]{start2.x, start2.y};
            }
            return new double[0];
        } else {
            // 情况 3：（一般情况）使用公式计算交点的坐标
            double x = (line2.b - line1.b) / (line1.k - line2.k);
            double y = x * line1.k + line1.b;

            intersection = new Point(x, y);
        }

        // 检测所在直线的交点是否在两条线段的横纵坐标范围之内
        if ( isBetween(start1, intersection, end1)) {
            return new double[]{intersection.x, intersection.y};
        }
        return new double[0];
    }
    private static Point[] checkAndConvertIntoPoint(double[] start1_, double[] end1_, double[] start2_, double[] end2_) {
        // 封装成 Point 类，将 int 转换成 double 类型，以便于计算
        Point start1 = new Point(start1_[0], start1_[1]);
        Point end1 = new Point(end1_[0], end1_[1]);
        Point start2 = new Point(start2_[0], start2_[1]);
        Point end2 = new Point(end2_[0], end2_[1]);

        // 参数校验：保证横坐标符合约定
        // 对于单条线段而言，起点坐标总是横坐标较小的那一个
        if (start1.x > end1.x) {
            swap(start1, end1);
        }
        if (start2.x > end2.x) {
            swap(start2, end2);
        }

        // 对于两条线段而言，线段 1 的横坐标小于等于线段 2 的横坐标
        if (start1.x > start2.x) {
            // 两条线段交换
            swap(start1, start2);
            swap(end1, end2);
        }
        return new Point[]{start1, end1, start2, end2};
    }

    private static boolean isBetween(double start, double middle, double end) {
        if (start > end) {
            // 逆序
            return end <= middle && middle <= start;
        } else {
            // 顺序
            return start <= middle && middle <= end;
        }
    }

    private static boolean isBetween(Point start, Point middle, Point end) {
        return isBetween(start.x, middle.x, end.x) && isBetween(start.y, middle.y, end.y);
    }


    /**
     * 交换两个点坐标的数值
     *
     * @param point1
     * @param point2
     */
    private static void swap(Point point1, Point point2) {
        double tempX = point1.x;
        double tempY = point1.y;

        point1.x = point2.x;
        point1.y = point2.y;

        point2.x = tempX;
        point2.y = tempY;
    }


//    private class Line {
//
//    }

    /**
     * 将输入封装成 Point，以便把 int 类型转换成 double 类型，便于计算
     */
//    private class Point {
//
//    }
    private static void writeTest(List<Vertex> jieguo, String MyPath) throws IOException {

//        File file  = new File(MyPath+"//结果.xlsx");  //目标Excel文件
//        File parent = new File(file.getParent());  //Excel的父文件夹
//        if (!parent.exists()){  //判断父文件夹是否存在
//            parent.mkdirs();  //建立父文件夹
//        }
//
//
//        Workbook wb = null;
//        if(file.exists()){  //如果文件存在
//            wb = WorkbookFactory.create(file);  //打开文件
//        }else {  //如果目标文件不存在
//            if (MyPath.endsWith(".xls")) {
//                /*操作Excel2003以前（包括2003）的版本，扩展名是.xls */
//                wb = new HSSFWorkbook();
//            } else if (MyPath.endsWith(".xlsx")) {
//                /*XSSFWorkbook:是操作Excel2007的版本，扩展名是.xlsx */
//                wb = new XSSFWorkbook();
//            } else {
//                new Exception(MyPath + "后缀名错误!");
//            }
//        }
        //创建工作薄对象
//        HSSFWorkbook workbook=new HSSFWorkbook();//这里也可以设置sheet的Name
//        //创建工作表对象
//        HSSFSheet sheet = workbook.createSheet();

//2.创建一个工作簿
//        Workbook workbook = readExcelAb(MyPath+"\\fayang.xlsx");
        Workbook workbook=new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("结果");

        Row row0;
        Cell  cell;
        row0=sheet.createRow(0);
        //第一行前五列
        cell=row0.createCell(0);
        cell.setCellValue("id");
        cell=row0.createCell(1);
        cell.setCellValue("X");
        cell=row0.createCell(2);
        cell.setCellValue("Y");
        cell=row0.createCell(3);
        cell.setCellValue("Z");

        //1 2 3 4列
        for (int i = 0; i < jieguo.size(); i++) {
            row0=sheet.createRow(i+1);
            cell=row0.createCell(0,CellType.STRING);
            cell.setCellValue(i+1);
            cell=row0.createCell(1,CellType.NUMERIC);
            cell.setCellValue(jieguo.get(i).x);
            cell=row0.createCell(2,CellType.NUMERIC);
            cell.setCellValue(jieguo.get(i).y);
            cell=row0.createCell(3,CellType.NUMERIC);
            cell.setCellValue(jieguo.get(i).z);

        }


        try {
            FileOutputStream fos = new FileOutputStream(MyPath+"\\结果.xlsx");
            workbook.write(fos);
            workbook.close();
            fos.close();
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
}
