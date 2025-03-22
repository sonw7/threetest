package com.kjw_Auto_HOB;

import com.kjw_Auto_HOB.Class.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("all")
public class IntersectEXCELrename_zd {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\86195\\Desktop\\断层求交文件名重赋\\15_16");//所读入的文件夹\13_14
        //用数组把文件夹下的文件存起来
        File[] files = file.listFiles();//记录了所给文件夹下的所有子文件夹，和子文件夹里的所有文件

        //记录了每个子文件夹的路径
        String[] filespathname=new String[file.listFiles().length];
        int im=0;//
        for (File file2 : files) {
            filespathname[im]=file2.getPath();
            im++;
        }


        //循环每一个子文件夹,循环内部执行处理方法
        for(int i=0;i<filespathname.length;i++) {
            String txtpath = filespathname[i] + "\\断层关系.txt";//存储主辅关系
            //存储每一对的一个主和多个辅的文件名
            List<MFault> MFaults = new ArrayList<>();
            MFaults = readtxt(txtpath);
            String Opath=filespathname[i];
            //开始循环每一对主从断层
            for (int s=0;s<MFaults.size();s++){

                //主断层文件路径
                String MFpath=Opath+"\\"+MFaults.get(s).MFname+".xlsx";
                File file2 = new File(MFpath);
                if(!file2 .exists()) {
                    MFpath=Opath+"\\"+MFaults.get(s).MFname+".xls";

                }

                //存储所有辅断层
                List<Fault> faults = new ArrayList<>();//存储辅断层
                Fault MainFault = new Fault();//存储主断层
                List<Vertex> counterclockwiseV = new ArrayList<>();
//                List<Vertex> counterMFdelV = new ArrayList<>();

                //取主断层数据
                MainFault = SaveFaultinfor(MFpath, MFaults.get(s).MFname,1);//存储主断层
                MainFault.isMF=true;

                //取辅断层数据
                for (String s1 : MFaults.get(s).Fname) {
                    String Fpath=Opath+"\\"+s1+".xlsx";
                    File file3 = new File(Fpath);
                    if(!file3 .exists()) {
                        Fpath=Opath+"\\"+s1+".xls";
                        System.out.println("测试文件不存在");
                    }
                    faults.add(SaveFaultinfor(Fpath, s1));
                }



            List<CVertexList> cVertexLists = new ArrayList<>();//存储交点及其逆序离散点，用于最后结果的整合
//            List<CVertexList> cVertex;
            //处理每一个辅断层，
//            int num,GXnum;
                //循环比对每一个辅断层的点
                for (Vertex vertex : MainFault.vertexes) {

                      for (int j = 0; j < faults.size(); j++) {
                          //循环辅断层的点集
                          for (Vertex vertex1 : faults.get(j).vertexes) {
                              if(vertex.x==vertex1.x&&vertex.y== vertex1.y&&vertex.z== vertex1.z){
                                  vertex.name= vertex1.name;//点名
                                  vertex.setFaultname(faults.get(j).name);//断层名
                              }
                          }


                      }
                }
                //循环完开始写
            counterclockwiseV = MainFault.vertexes;
            //此处处理cVertexLists和counterclockwiseV的合并和输出
                writeTest(MainFault.vertexes, filespathname[i],MFaults.get(s).MFname);
           // handleLineIntersect(counterclockwiseV, cVertexLists, filespathname[i],MFaults.get(s).MFname);//一个是主断层逆序及其应删除点，一个是记录了主次交点和辅断层逆序点
        }
            //输出结果表格
            System.out.println("文件已经读入");
        }



    }
    public static List<CVertexList> handleLineIntersect(List<Vertex> counterclockwiseV, List<CVertexList> cVertexLists, String s, String MFname) throws IOException {


        System.out.println("进行到了集成结果这一步");

        List<Vertex>vertices=new ArrayList<>();
        List<CVertexList> cVertexListss=new ArrayList<>();
        double mindis=0;
        for (int i = 0; i < counterclockwiseV.size(); i++) {
            if(counterclockwiseV.get(i).isdelete!=true)//不用删
                vertices.add(counterclockwiseV.get(i));
            for (CVertexList cVertexList : cVertexLists) {
                if(cVertexList.Cvertex.insertIndex==i){
                    cVertexListss.add(cVertexList);
//                    vertices.add(cVertexList.Cvertex);
//                    for (Vertex vertex : cVertexList.vertices) {
//                        vertices.add(vertex);
//                    }
                }
            }
            if(cVertexListss.isEmpty()==true){
                continue;
            }else if(cVertexListss.size()==1){
                vertices.add(cVertexListss.get(0).Cvertex);
                for (Vertex vertex : cVertexListss.get(0).vertices) {
                    vertices.add(vertex);
                }
            }else {
                mindis=comditance(cVertexListss.get(0).Cvertex,counterclockwiseV.get(i),cVertexListss.get(0).Cvertex,0);
                if(mindis<comditance(cVertexListss.get(1).Cvertex,counterclockwiseV.get(i),cVertexListss.get(0).Cvertex,0)){
                    vertices.add(cVertexListss.get(0).Cvertex);
                    for (Vertex vertex : cVertexListss.get(0).vertices) {
                        vertices.add(vertex);
                    }
                    vertices.add(cVertexListss.get(1).Cvertex);
                    for (Vertex vertex : cVertexListss.get(1).vertices) {
                        vertices.add(vertex);
                    }
                }else {
                    vertices.add(cVertexListss.get(1).Cvertex);
                    for (Vertex vertex : cVertexListss.get(1).vertices) {
                        vertices.add(vertex);
                    }
                    vertices.add(cVertexListss.get(0).Cvertex);
                    for (Vertex vertex : cVertexListss.get(0).vertices) {
                        vertices.add(vertex);
                    }
                }

            }
            cVertexListss=new ArrayList<>();

        }

        System.out.println("准备写入表格");
        writeTest(vertices, s,MFname);

        return null;
    }
    public static int CountIntersectionNum(Fault MainFault, Fault AssitFault){
        int num=0;
        for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
            //主断层的一段线段开始循环，对每个辅断层线段进行计算
            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                //主断层线段
                double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                double[] end2;
                //辅断层线段
                double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                if(i!=AssitFault.vertexes.size()-1)
                     end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                else
                    end2 = new double[]{AssitFault.vertexes.get(0).x, AssitFault.vertexes.get(0).y};
                double[] res = intersection(start1, end1, start2, end2);

                //如果计算出有交点，则执行
                if(res.length!=0||res==null)
                    num++;//交点数加一
//                System.out.println(Arrays.toString(res));

            }


        }
        System.out.println("00");
        if(num!=4){
             num=0;
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size()-1; i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    double[] end2;
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                        end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行
                    if(res.length!=0||res==null)
                        num++;//交点数加一
                    System.out.println(Arrays.toString(res));

                }


            }

        }
        return num;
    }
    public static int CountGXNum(Fault MainFault, Fault AssitFault, int num){
        int GXnum=0;
        int flag=0,zhuci=0;
        //此函数计算交点并保存应插入点之后的序号，以及主次交点间的离散点，返回CVertexList
        if(num==4||num==2){
            //无问题
            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    double[] end2;
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    if(i!=AssitFault.vertexes.size()-1)
                        end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    else
                        end2 = new double[]{AssitFault.vertexes.get(0).x, AssitFault.vertexes.get(0).y};
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
                            GXnum++;
                            continue;
                        }
                        if(zhuci==1&&flag!=m)
                        {
                            zhuci=0;
                            continue;
                        }
                    }

                }

            }

            return GXnum;
        }

        if(num==0){

            //如果无交点 只计算端点的距离
            Vertex [] distance=new Vertex[2];
            //选择距离最近两端点
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
                int j=0;


                if(Math.abs(distance[0].id-distance[1].id)!=1){
                    if(i+1==AssitFault.vertexes.size())
                        j=i-1;
                    else
                        j=i+1;
                }else {
                    if(distance[0].id<distance[1].id&&distance[0].id==vertex.id){
                        j=i-1;
                    }else if(distance[0].id<distance[1].id&&distance[1].id==vertex.id){
                        j=i+1;
                    }else if(distance[0].id>distance[1].id&&distance[0].id==vertex.id){
                        j=i+1;
                    }else if(distance[0].id>distance[1].id&&distance[1].id==vertex.id){
                        j=i-1;
                    }
                }
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
                            countindex[0]=comparedis1_0(vertex,MainFault.vertexes.get(countindex[0]),MainFault.vertexes.get(countindex[0]),MainFault.vertexes.get(k),MainFault.vertexes.get(k+1));
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
                    }
                }
            }
            if(countindex[1]==countindex[0])
                GXnum++;
            return GXnum;


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
            //单交点内部不可能尖灭。
            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                if(AssitFault.vertexes.get(i).type==2&&AssitFault.vertexes.get(i).id!=distance[0].id) {
                    AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);
                    if(distance[1]==null){
                        distance[1]=AssitFault.vertexes.get(i);
                        distance[1].insertIndex=i;}
                    else {
                        if (AssitFault.vertexes.get(i).distance<distance[1].distance){
                            distance[1]=AssitFault.vertexes.get(i);
                            distance[1].insertIndex=i;
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
            int i=distance[1].id,j;
            if(Math.abs(distance[0].id-distance[1].id)!=1){
                if(i+1==AssitFault.vertexes.size())
                    j=i-1;
                else
                    j=i+1;
            }else {
                if(distance[0].id<distance[1].id){
                    j=i+1;
                }else
                    j=i-1;
            }

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
                        distance[1].insertIndex=countindex[1]=k;
                        flag1=1;
                        continue;
                    }
                    if(flag1==1){
                        flag1=0;
                        distance[1].insertIndex=countindex[1]=comparedis(distance[1],MainFault.vertexes.get(countindex[1]+1),MainFault.vertexes.get(k),1);
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
                return GXnum+1;
            }else
                return GXnum;

        }

        return GXnum;
    }
    public static List<Vertex> deleteMFvertex(Fault MainFault, Fault AssitFault, List<Vertex> counterclockwiseV, int num, int GXnum){
        int flag=0,zhuci=0;
        int[] flagg=new int[2];
        //此函数计算交点并保存应插入点之后的序号，以及主次交点间的离散点，返回CVertexList
        if((num==4||num==2)&&GXnum==0){
            //无问题
            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    double[] end2;
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    if(i!=AssitFault.vertexes.size()-1)
                        end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    else
                        end2 = new double[]{AssitFault.vertexes.get(0).x, AssitFault.vertexes.get(0).y};
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
            //选择距离最近两端点
            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                if(AssitFault.vertexes.get(i).type==2) {
                    //必须是端点
                    AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);//返回最小的距离
                    if(distance[0]==null&&distance[1]==null)//如果未存储点
                        distance[0]=AssitFault.vertexes.get(i);
                    else if (distance[0]!=null&&distance[1]==null)//如果存了一个
                        distance[1]=AssitFault.vertexes.get(i);
                    else {
                        if (AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[1].distance<distance[0].distance)
                            distance[0]=AssitFault.vertexes.get(i);//新的点比1小  但0也比1小 替换1  新 0《0
                        else if(AssitFault.vertexes.get(i).distance<distance[1].distance&&distance[0].distance<distance[1].distance)
                            distance[1]=AssitFault.vertexes.get(i);//0比新的点和1都大
                        else if(AssitFault.vertexes.get(i).distance>distance[1].distance&&AssitFault.vertexes.get(i).distance>distance[0].distance)
                            continue;
                    }

                }
            }
            System.out.println("..");
            if(Math.abs(distance[0].id-distance[1].id)!=1||(distance[0].id+distance[1].id+1)!=AssitFault.vertexes.size()){

                distance=new Vertex[2];
                //选择距离最近两端点
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    if(AssitFault.vertexes.get(i).type==2) {
                        //必须是端点
                        AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes,0);//返回最小的距离
                        if(distance[0]==null&&distance[1]==null)//如果未存储点
                            distance[0]=AssitFault.vertexes.get(i);
                        else if (distance[0]!=null&&distance[1]==null)//如果存了一个
                            distance[1]=AssitFault.vertexes.get(i);
                        else {
                            if (AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[1].distance<distance[0].distance)
                                distance[0]=AssitFault.vertexes.get(i);//新的点比1小  但0也比1小 替换1  新 0《0
                            else if(AssitFault.vertexes.get(i).distance<distance[1].distance&&distance[0].distance<distance[1].distance)
                                distance[1]=AssitFault.vertexes.get(i);//0比新的点和1都大
                            else if(AssitFault.vertexes.get(i).distance>distance[1].distance&&AssitFault.vertexes.get(i).distance>distance[0].distance)
                                continue;
                        }

                    }
                }
            }
            int [] countindex=new int[2];
            int zhuci1=0,flag1=0;//flag记录一个直线交到线段个数
            for (Vertex vertex : distance) {
                //先计算线段两端点是否在直线的两侧
                int i=vertex.id;
                int j=0;
                Vertex Cvertex=new Vertex();


                if(Math.abs(distance[0].id-distance[1].id)!=1){
                    if(i+1==AssitFault.vertexes.size())
                        j=i-1;
                    else
                        j=i+1;
                }else {
                    if(distance[0].id<distance[1].id&&distance[0].id==vertex.id){
                        j=i-1;
                    }else if(distance[0].id<distance[1].id&&distance[1].id==vertex.id){
                        j=i+1;
                    }else if(distance[0].id>distance[1].id&&distance[0].id==vertex.id){
                        j=i+1;
                    }else if(distance[0].id>distance[1].id&&distance[1].id==vertex.id){
                        j=i-1;
                    }
                }
                for (int k = 0; k < MainFault.vertexes.size()-1; k++) {
                    double[] start1 = new double[]{MainFault.vertexes.get(k).x, MainFault.vertexes.get(k).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(k+1).x, MainFault.vertexes.get(k+1).y};
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    double[] end2 = new double[]{AssitFault.vertexes.get(j).x, AssitFault.vertexes.get(j).y};
                    double[] res = intersection(start1, end1, start2, end2,1);
                    if(res.length!=0||res==null) {
                        //分支型交点为0，只有可能两个交点。序号独立处理，可能交2个以上主断层线段
                        if(zhuci1==0&&flag1==0)
                        {
                            Cvertex.x=res[0];
                            Cvertex.y=res[1];
                            flagg[0]=countindex[0]=k;
                            flag1++;
                            continue;
                        }
                        if(zhuci1==0&&flag1!=0){
                            flag1++;
                            double dis1=Math.sqrt((vertex.x-Cvertex.x)*(vertex.x-Cvertex.x)+(vertex.y-Cvertex.y)*(vertex.y-Cvertex.y));
                            double dis2=Math.sqrt((vertex.x-res[0])*(vertex.x-res[0])+(vertex.y-res[1])*(vertex.y-res[1]));
                            if(dis1>dis2){
                                flagg[0]=countindex[0]=k;
                                Cvertex.x=res[0];
                                Cvertex.y=res[1];
                            }
                            continue;
                        }
                        if(zhuci1==1&&flag1==0)
                        {
                            Cvertex.x=res[0];
                            Cvertex.y=res[1];
                            flagg[1]=countindex[1]=k;
                            flag1++;
                            continue;
                        }
                        if(zhuci1==1&&flag1!=0){
                            flag1++;
                            double dis1=Math.sqrt((vertex.x-Cvertex.x)*(vertex.x-Cvertex.x)+(vertex.y-Cvertex.y)*(vertex.y-Cvertex.y));
                            double dis2=Math.sqrt((vertex.x-res[0])*(vertex.x-res[0])+(vertex.y-res[1])*(vertex.y-res[1]));
                            if(dis1>dis2){
                                flagg[1]=countindex[1]=k;
                                Cvertex.x=res[0];
                                Cvertex.y=res[1];
                            }
                            continue;
                        }
                        System.out.println("计算出交点");
                    }
                }



                zhuci1=1;
                flag1=0;
                System.out.println("111");
            }
            int [] indexs=new int[2];
            if(flagg[0]<flagg[1])
            {
                indexs[0]=flagg[0];
                indexs[1]=flagg[1];
            }else {
                indexs[0]=flagg[1];
                indexs[1]=flagg[0];
            }
            for (int j = indexs[0]+1; j <indexs[1]+1 ; j++) {
                MainFault.vertexes.get(j).isdelete = true;
            }
//            if(countindex[0]<countindex[1])
//            {
//                indexs[0]=countindex[0];
//                indexs[1]=countindex[1];
//            }else {
//                indexs[0]=countindex[1];
//                indexs[1]=countindex[0];
//            }
//            if(indexs[0]==indexs[1]){
//                return MainFault.vertexes;
//            }else {
//                for (int i = indexs[0]+1; i <indexs[1]+1 ; i++) {
//                    MainFault.vertexes.get(i).isdelete=true;
//                }
//            }

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
                        distance[0]=AssitFault.vertexes.get(i);
                        distance[0].Cvertex=vertex0;
                        //因为只有一个交点
                        break;
                    }

                }
            }
            //求所有点
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    if(AssitFault.vertexes.get(i).type==2&&AssitFault.vertexes.get(i).id!=distance[0].id) {
                        AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes);
                        if(distance[1]==null){
                            distance[1]=AssitFault.vertexes.get(i);
                            distance[1].insertIndex=i;}
                        else {
                            if (AssitFault.vertexes.get(i).distance<distance[1].distance){
                                distance[1]=AssitFault.vertexes.get(i);
                                distance[1].insertIndex=i;
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
                countindex[0]=distance[0].Cvertex.insertIndex;
                int i=distance[1].id,j;
            if(Math.abs(distance[0].id-distance[1].id)!=1){
                if(distance[0].id<distance[1].id)
                    j=i-1;
                else
                    j=i+1;
            }else {
                if(distance[0].id<distance[1].id){
                    j=i+1;
                }else
                    j=i-1;
            }

                int flag1=0;//flag记录一个直线交到线段个数
                 Vertex Cvertex=new Vertex();

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
                                distance[1].insertIndex=countindex[1]=k;
                                Cvertex.x=res[0];
                                Cvertex.y=res[1];
                                flag1++;
                                continue;
                            }
                            if(flag1!=0){
                                double dis1=Math.sqrt(((distance[1].x-Cvertex.x)*(distance[1].x-Cvertex.x)+((distance[1].y-Cvertex.y)*(distance[1].y-Cvertex.y))));
                                double dis2=Math.sqrt(((distance[1].x-res[0])*(distance[1].x-res[0])+((distance[1].y-res[1])*(distance[1].y-res[1]))));
                                if(dis1>dis2){
                                    distance[1].insertIndex=countindex[1]=k;
                                    Cvertex.x=res[0];
                                    Cvertex.y=res[1];
                                }
                                continue;
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

        if((num==4||num==2)&&GXnum!=0){


            //无问题
            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};
                    double[] end2;
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    if(i!=AssitFault.vertexes.size()-1)
                        end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    else
                        end2 = new double[]{AssitFault.vertexes.get(0).x, AssitFault.vertexes.get(0).y};
                    double[] res = intersection(start1, end1, start2, end2);


                    //如果计算出有交点，则执行
                    if(res.length!=0||res==null) {
                        if(zhuci==0)//出现主交点
                        {
                            zhuci = 1;
                            flagg[0]=flag = m;//记录当前线段
                            continue;
                        }
                        if(zhuci==1&&flag==m){
                            //存在主交点且又出现交点，主交点所属的线段m与记录的flag一致，在同一线段无需要舍弃的
                            zhuci=0;
                            flagg[1]=m;
                            continue;
                        }
                        if(zhuci==1&&flag!=m)
                        {
                            flagg[1]=m;
                            int [] indexs=new int[2];
                            if(flagg[0]<flagg[1])
                            {
                                indexs[0]=flagg[0];
                                indexs[1]=flagg[1];
                            }else {
                                indexs[0]=flagg[1];
                                indexs[1]=flagg[0];
                            }
                            for (int j = indexs[0]+1; j <indexs[1]+1 ; j++) {
                                MainFault.vertexes.get(j).isdelete = true;
                            }
                            zhuci=0;
                            continue;
                        }
                    }

                }

            }}



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
    public static int comparedis1_0(Vertex vertex,Vertex vertex11,Vertex vertex12,Vertex verte21,Vertex verte22){

        double[] start1 = new double[]{vertex12.x, vertex12.y};
        double[] end1 = new double[]{vertex11.x, vertex11.y};
        //辅断层线段
        double[] start2 = new double[]{verte21.x, verte21.y};
        double[] end2 = new double[]{verte22.x, verte22.y};

        Point[] points = checkAndConvertIntoPoint(start1, end1, start2, end2);
        Point start1_ = points[0];
        Point end1_ = points[1];
        Point start2_ = points[2];
        Point end2_ = points[3];

        // 封装成直线类，以便计算斜率和截距
        Line line1 = new Line(start1_, end1_);
        Line line2 = new Line(start2_, end2_);

        double dist1,dist2;
        dist1=Math.abs(line1.k*vertex.x-vertex.y+line1.b)/Math.sqrt(line1.k*line1.k+1);
        dist2=Math.abs(line2.k*vertex.x-vertex.y+line2.b)/Math.sqrt(line2.k*line1.k+1);
        if(dist1<dist2)
            return vertex11.id;
        else
            return verte21.id;

    }
    public static int comparedis(Vertex vertex,Vertex vertex1,Vertex vertex2,int num){
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
            index=vertex2.id;
        else
            index=vertex1.id-1;
        return index;
    }
    public static int comparedisinsert(Vertex vertex,Vertex vertex1,Vertex vertex2){
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
            index=vertex2.id;
        else
            index=vertex1.id-1;
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
    public static Vertex[] compare(Vertex vertex, Vertex vertex1, Vertex vertex2, int num){
        int index;
        Vertex[] ver=new Vertex[2];
        Double X,Y,X1,Y1,X2,Y2;
        X=vertex.x;
        Y=vertex.y;
        X1=vertex1.Cvertex.x;
        Y1=vertex1.Cvertex.y;
        X2=vertex2.Cvertex.x;
        Y2=vertex2.Cvertex.y;
        double dis1=Math.sqrt((X-X1)*(X-X1)+(Y-Y1)*(Y-Y1));
        double dis2=Math.sqrt((X-X2)*(X-X2)+(Y-Y2)*(Y-Y2));
        if(dis1>dis2){
            vertex2.Cvertex.isMainCV=true;
            ver[0]=vertex2;
            ver[1]=vertex1;
            return ver;
        }
        else {
            vertex1.Cvertex.isMainCV=true;
            ver[0]=vertex1;
            ver[1]=vertex2;
            return ver;//原有点胜利
        }

    }
    public static double comdistance(Vertex vertex,List<Vertex> vertexList){
        double mindistance=0;
        for (int i = 0; i < vertexList.size()-1; i++) {
            if(mindistance==0){
                if(vertexList.get(i).type!=2&&vertexList.get(i+1).type!=2){
                mindistance=comditance(vertex,vertexList.get(i),vertexList.get(i+1));
                continue;
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
    public static double comdistance(Vertex vertex,List<Vertex> vertexList,int o){
        double mindistance=0;
        for (int i = 0; i < vertexList.size()-1; i++) {
            if(mindistance==0){

                    mindistance=comditance(vertex,vertexList.get(i),vertexList.get(i+1),o);
                    continue;

            }else
            {
                if(mindistance>comditance(vertex,vertexList.get(i),vertexList.get(i+1),o))
                {
                    mindistance=comditance(vertex,vertexList.get(i),vertexList.get(i+1),o);
                }else
                    continue;
//                }
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
    public static double comditance(Vertex vertex,Vertex vertex1,Vertex vertex2,int o){
        double distance;
        Double X,Y,X1,Y1,X2,Y2;
        X=vertex.x;
        Y=vertex.y;
        X1=vertex1.x;
        Y1=vertex1.y;

        distance=Math.sqrt((X-X1)*(X-X1)+(Y-Y1)*(Y-Y1));
        return distance;
    }
    public static List<CVertexList> handleintersect(Fault MainFault, Fault AssitFault, int num, int GXnum){
        //此函数计算交点并保存应插入点之后的序号，以及主次交点间的离散点，返回CVertexList
        List<Vertex> AssitVertex=AssitFault.vertexes;//辅断层的所有点

        int zhuci=0;//flag记录主断层同一线段的交点个数，主次记录主次点赋予

        List<CVertexList> cVertexLists=new ArrayList<>();//记录交点集合
        CVertexList Cvertex = null;
        Vertex vertex;
        if((num==4||num==2)&&GXnum==0){
            //如果为十字型四个交点

            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                if(zhuci==0){
                    Cvertex = null;
                    Cvertex=new CVertexList();//如果没主交点出现则新设交点集变量
                    Cvertex.vertices=new ArrayList<>();
                     }
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {

                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};

                    double[] end2;
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    if(i!=AssitFault.vertexes.size()-1)
                        end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    else
                        end2 = new double[]{AssitFault.vertexes.get(0).x, AssitFault.vertexes.get(0).y};



                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行

                     //如果在同一线段内,次交点会先交到。
                    if(res.length!=0||res==null) {
                        if(zhuci==0){
                            Vertex vertex1=new Vertex();
                            vertex1.x=res[0];
                            vertex1.y=res[1];
                            vertex1.z=0;
                            vertex1.isMainCV=true;
                            vertex1.insertIndex=m;
                            vertex1.Ctype=true;
                            if(i+1!=AssitFault.vertexes.size())//十字型，另一头只有尖灭点 且为起点
                                vertex1.AssisinserIndexMain=i+1;
                            else
                                vertex1.AssisinserIndexMain=0;
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
                            if(i+1!=AssitFault.vertexes.size())
                                vertex.AssisinserIndexAssi=i;
                            else
                                vertex.AssisinserIndexAssi=0;
                            vertex.z=0;
                            if(Cvertex.Cvertex.AssisinserIndexMain<i){
                            for (int j = Cvertex.Cvertex.AssisinserIndexMain; j <i+1 ; j++) {
                                //存入主次交点间的离散点
                                Cvertex.vertices.add(AssitFault.vertexes.get(j));

                            }}else if(Cvertex.Cvertex.AssisinserIndexMain>i)
                            {
                                for (int j = Cvertex.Cvertex.AssisinserIndexMain; j <AssitFault.vertexes.size() ; j++) {
                                    //存入主次交点间的离散点
                                    Cvertex.vertices.add(AssitFault.vertexes.get(j));

                                }
                                for (int j = 0; j <i+1; j++) {
                                    Cvertex.vertices.add(AssitFault.vertexes.get(j));
                                }
                            }else {
                                Cvertex.vertices.add(AssitFault.vertexes.get(Cvertex.Cvertex.AssisinserIndexMain));
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
                if(AssitFault.vertexes.get(i).type==2) {//只计算端点

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
            if(Math.abs(distance[0].id-distance[1].id)!=1&&(distance[0].id+distance[1].id+1)!=AssitFault.vertexes.size()){

                distance=new Vertex[2];
                //选择距离最近两端点
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                    if(AssitFault.vertexes.get(i).type==2) {
                        //必须是端点
                        AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes,0);//返回最小的距离
                        if(distance[0]==null&&distance[1]==null)//如果未存储点
                            distance[0]=AssitFault.vertexes.get(i);
                        else if (distance[0]!=null&&distance[1]==null)//如果存了一个
                            distance[1]=AssitFault.vertexes.get(i);
                        else {
                            if (AssitFault.vertexes.get(i).distance<distance[0].distance&&distance[1].distance<distance[0].distance)
                                distance[0]=AssitFault.vertexes.get(i);//新的点比1小  但0也比1小 替换1  新 0《0
                            else if(AssitFault.vertexes.get(i).distance<distance[1].distance&&distance[0].distance<distance[1].distance)
                                distance[1]=AssitFault.vertexes.get(i);//0比新的点和1都大
                            else if(AssitFault.vertexes.get(i).distance>distance[1].distance&&AssitFault.vertexes.get(i).distance>distance[0].distance)
                                continue;
                        }

                    }
                }
            }
            int [] countindex=new int[2];

            int zhuci1=0,flag1=0;//flag记录一个直线交到线段个数
            for (Vertex vertex1 : distance) {
                //先计算线段两端点是否在直线的两侧
                int i=vertex1.id;
                int j = 0;
                vertex1.Cvertex=new Vertex();


                if(Math.abs(distance[0].id-distance[1].id)!=1){
                if(i+1==AssitFault.vertexes.size())
                    j=i-1;
                else
                    j=i+1;
                }else {
                       if(distance[0].id<distance[1].id&&distance[0].id==vertex1.id){
                            j=i-1;
                        }else if(distance[0].id<distance[1].id&&distance[1].id==vertex1.id){
                            j=i+1;
                        }else if(distance[0].id>distance[1].id&&distance[0].id==vertex1.id){
                           j=i+1;
                       }else if(distance[0].id>distance[1].id&&distance[1].id==vertex1.id){
                           j=i-1;
                       }
                  }
                //ij确定完
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
                            vertex1.Cvertex.insertIndex=k;
                            vertex1.Cvertex.x=res[0];
                            vertex1.Cvertex.y=res[1];
                            vertex1.Cvertex.z=0;
                            flag1++;
                            continue;
                        }
                        if(zhuci1==0&&flag1!=0){
                            flag1++;
                            double dis1=Math.sqrt(((vertex1.x-vertex1.Cvertex.x)*(vertex1.x-vertex1.Cvertex.x)+((vertex1.y-vertex1.Cvertex.y)*(vertex1.y-vertex1.Cvertex.y))));
                            double dis2=Math.sqrt(((vertex1.x-res[0])*(vertex1.x-res[0])+((vertex1.y-res[1])*(vertex1.y-res[1]))));
                            if(dis1>dis2){
                                vertex1.Cvertex.insertIndex=countindex[1]=k;
                                vertex1.Cvertex.x=res[0];
                                vertex1.Cvertex.y=res[1];
                            }
                            continue;
                        }
                        if(zhuci1==1&&flag1==0){

                            vertex1.Cvertex.insertIndex=k;
                            vertex1.Cvertex.x=res[0];
                            vertex1.Cvertex.y=res[1];
                            vertex1.Cvertex.z=0;
                            flag1++;
                            continue;
                        }
                        if(zhuci1==1&&flag1!=0){
                            flag1++;
                            double dis1=Math.sqrt(((vertex1.x-vertex1.Cvertex.x)*(vertex1.x-vertex1.Cvertex.x)+((vertex1.y-vertex1.Cvertex.y)*(vertex1.y-vertex1.Cvertex.y))));
                            double dis2=Math.sqrt(((vertex1.x-res[0])*(vertex1.x-res[0])+((vertex1.y-res[1])*(vertex1.y-res[1]))));
                            if(dis1>dis2){

                                vertex1.Cvertex.insertIndex=countindex[1]=k;
                                vertex1.Cvertex.x=res[0];
                                vertex1.Cvertex.y=res[1];
                            }
                            continue;
                        }
                        System.out.println("计算出交点");
                    }
                }
                zhuci1++;
                flag1=0;

            }

            if(Math.abs(distance[0].id-distance[1].id)==1){
                if(distance[0].id<distance[1].id)
                distance=exchange(distance);

                Cvertex.Cvertex=distance[0].Cvertex;
                for (int i = distance[0].id; i <AssitFault.vertexes.size() ; i++) {
                    Cvertex.vertices.add(AssitFault.vertexes.get(i));
                }
                for (int i = 0; i < distance[1].id+1; i++) {
                    Cvertex.vertices.add(AssitFault.vertexes.get(i));
                }
                Cvertex.vertices.add(distance[1].Cvertex);
            }else {
                        if(distance[0].id>distance[1].id)
                            distance=exchange(distance);

                        Cvertex.Cvertex=distance[0].Cvertex;
                        for (int i = distance[0].id; i <AssitFault.vertexes.size() ; i++) {
                            Cvertex.vertices.add(AssitFault.vertexes.get(i));
                        }
                        Cvertex.vertices.add(distance[1].Cvertex);
//                      if(distance[0].Cvertex.insertIndex<=distance[1].Cvertex.insertIndex){
//                          distance[0].Cvertex.isMainCV=true;
//                          distance[0].Cvertex.insertIndex=distance[0].insertIndex;
//                          Cvertex.Cvertex=distance[0].Cvertex;
////                          Cvertex.vertices.add(distance[0]);
//                          if(distance[0].id<distance[1].id&&Math.abs(distance[0].id-distance[1].id)!=1){//如果true，前者一定是第一个点，否则逆序情况下不可能为true
//                              for (int i = distance[0].id; i < distance[1].id+1; i++) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//                              Cvertex.vertices.add(distance[1].Cvertex);
//                          }else if(distance[0].id>distance[1].id&&Math.abs(distance[0].id-distance[1].id)==1){
//                              for (int i = distance[0].id; i <AssitFault.vertexes.size() ; i++) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//                              for (int i = 0; i <distance[1].id+1 ; i++) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//
//                              Cvertex.vertices.add(distance[1].Cvertex);
//
//                          }else {
//                              for (int i = distance[0].id; i >distance[1].id-1 ; i--) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//
//                              Cvertex.vertices.add(distance[1].Cvertex);
//                          }
//
//                      }else {
//                          //distance[0].insertIndex》distance[1].insertIndex
//                          distance[1].Cvertex.isMainCV=true;
//                          distance[1].Cvertex.insertIndex=distance[1].insertIndex;
//                          Cvertex.Cvertex=distance[1].Cvertex;
//                          //Cvertex.vertices.add(distance[1]);
//                          if(distance[0].id<distance[1].id&&Math.abs(distance[0].id-distance[1].id)!=1){//如果true，前者一定是第一个点，否则逆序情况下不可能为true
//                              for (int i = distance[0].id; i < distance[1].id+1; i++) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//                              Cvertex.vertices.add(distance[1].Cvertex);
//                          }else if(distance[0].id>distance[1].id&&Math.abs(distance[0].id-distance[1].id)==1){
//                              for (int i = distance[0].id; i <AssitFault.vertexes.size() ; i++) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//                              for (int i = 0; i <distance[1].id+1 ; i++) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//
//                              Cvertex.vertices.add(distance[1].Cvertex);
//
//                          }else {
//                              for (int i = distance[0].id; i >distance[1].id-1 ; i--) {
//                                  Cvertex.vertices.add(AssitFault.vertexes.get(i));
//                              }
//
//                              Cvertex.vertices.add(distance[1].Cvertex);
//                          }
//                      }
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
                        //该交点是由辅断层第i个和第i+1个求出的。
                        //存储交点
                        vertex0.x=res[0];
                        vertex0.y=res[1];
                        vertex0.z=0;
                        vertex0.insertIndex=m;//该交点是由主断层第m个和第m+1个求出的。

                        AssitVertex.get(i).Cvertex=vertex0;
                        distance[0]=AssitFault.vertexes.get(i);
                        //因为只有一个交点
                        break;
                    }

                }
            }

            for (int i = 0; i < AssitFault.vertexes.size(); i++) {
                if(AssitFault.vertexes.get(i).type==2&&AssitFault.vertexes.get(i).id!=distance[0].id) {
                    AssitFault.vertexes.get(i).distance=comdistance(AssitFault.vertexes.get(i),MainFault.vertexes,0);
                    if(distance[1]==null){
                        distance[1]=AssitFault.vertexes.get(i);
                        distance[1].Cvertex=new Vertex();
                    }
                    else {
                        if (AssitFault.vertexes.get(i).distance<distance[1].distance){
                            distance[1]=AssitFault.vertexes.get(i);
                            distance[1].Cvertex=new Vertex(0.0,0.0,0.0,0,0);
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
            countindex[0]=distance[0].Cvertex.insertIndex;
            int i0=distance[1].id,j0;
            if(Math.abs(distance[0].id-distance[1].id)!=1){
                if(distance[0].id<distance[1].id)
                    j0=i0-1;
                else
                    j0=i0+1;
            }else {
                if(distance[0].id<distance[1].id){
                    j0=i0+1;
                }else
                    j0=i0-1;
            }

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

                        countindex[1]=k;
                        distance[1].Cvertex.insertIndex=countindex[1];
                        distance[1].Cvertex=new Vertex();
                        distance[1].Cvertex.x=res[0];
                        distance[1].Cvertex.y=res[1];
                        distance[1].Cvertex.z=0;

                        flag1++;
                        continue;
                    }
                    if(flag1!=0){
                        double dis1=Math.sqrt(((distance[1].x-distance[1].Cvertex.x)*(distance[1].x-distance[1].Cvertex.x)+((distance[1].y-distance[1].Cvertex.y)*(distance[1].y-distance[1].Cvertex.y))));
                        double dis2=Math.sqrt(((distance[1].x-res[0])*(distance[1].x-res[0])+((distance[1].y-res[1])*(distance[1].y-res[1]))));
                        if(dis1>dis2){

                            countindex[1]=k;
                            distance[1].Cvertex.insertIndex=countindex[1];
                            distance[1].Cvertex.x=res[0];
                            distance[1].Cvertex.y=res[1];
                        }
                        continue;
                    }

                }
            }
            distance[1].Cvertex.insertIndex=countindex[1];
            System.out.println("11");
            if(distance[0].Cvertex.insertIndex<distance[1].Cvertex.insertIndex){
                distance[0].Cvertex.isMainCV=true;
                //distance[0].Cvertex.insertIndex=distance[0].insertIndex;
                Cvertex.Cvertex=distance[0].Cvertex;
//                Cvertex.vertices.add(distance[0]);
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
                //distance[0].Cvertex.insertIndex=distance[0].insertIndex;
                Cvertex.Cvertex=distance[1].Cvertex;
//                Cvertex.vertices.add(distance[0]);
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

        if((num==4||num==2)&&GXnum!=0){
            //如果为十字型四个交点
            Vertex[] ver = new Vertex[2];

            //开始主断层线段遍历
            for (int m = 0; m < MainFault.vertexes.size()-1; m++) {
                if(zhuci==0){
                    ver=null;
                    ver=new Vertex[2];
                    Cvertex = null;
                    Cvertex=new CVertexList();//如果没主交点出现则新设交点集变量
                    Cvertex.vertices=new ArrayList<>();
                }
                //主断层的一段线段开始循环，对每个辅断层线段进行计算
                for (int i = 0; i < AssitFault.vertexes.size(); i++) {

                    //主断层线段
                    double[] start1 = new double[]{MainFault.vertexes.get(m).x, MainFault.vertexes.get(m).y};
                    double[] end1 = new double[]{MainFault.vertexes.get(m+1).x, MainFault.vertexes.get(m+1).y};

                    double[] end2;
                    //辅断层线段
                    double[] start2 = new double[]{AssitFault.vertexes.get(i).x, AssitFault.vertexes.get(i).y};
                    if(i!=AssitFault.vertexes.size()-1)
                        end2 = new double[]{AssitFault.vertexes.get(i+1).x, AssitFault.vertexes.get(i+1).y};
                    else
                        end2 = new double[]{AssitFault.vertexes.get(0).x, AssitFault.vertexes.get(0).y};



                    double[] res = intersection(start1, end1, start2, end2);

                    //如果计算出有交点，则执行

                    //此时同一线段可能有两个交点，无法判断主次，只能通过插入序号。
                    if(res.length!=0||res==null) {
                        if(zhuci==0){
                            Vertex vertex1=new Vertex();
                            vertex1.x=res[0];
                            vertex1.y=res[1];
                            vertex1.z=0;
                            vertex1.insertIndex=m;
                            vertex1.Ctype=true;
                            ver[0]=AssitFault.vertexes.get(i);
                            ver[0].Cvertex=vertex1;

                            zhuci++;

                        }else if(zhuci==1)
                        {
                            //次交点存储
                            vertex=new Vertex();
                            //交点设置为次交点
                            vertex.x=res[0];
                            vertex.y=res[1];
                            vertex.z=0;
                            vertex.insertIndex=m;
                            vertex.Ctype=true;
                            ver[1]=AssitFault.vertexes.get(i);
                            ver[1].Cvertex=vertex;
                            ver=compare(MainFault.vertexes.get(m),ver[0],ver[1],1);
                            if((ver[0].id+1)==AssitFault.vertexes.size())
                                ver[0].Cvertex.AssisinserIndexMain=0;
                            else
                                ver[0].Cvertex.AssisinserIndexMain=ver[0].id+1;
                            Cvertex.Cvertex=ver[0].Cvertex;

                            if(Cvertex.Cvertex.AssisinserIndexMain<ver[1].id){
                                for (int j = Cvertex.Cvertex.AssisinserIndexMain; j <ver[1].id+1 ; j++) {
                                    //存入主次交点间的离散点
                                    Cvertex.vertices.add(AssitFault.vertexes.get(j));

                                }}else if(Cvertex.Cvertex.AssisinserIndexMain>ver[1].id)
                                      {
                                          for (int j = Cvertex.Cvertex.AssisinserIndexMain; j <AssitFault.vertexes.size() ; j++) {
                                              //存入主次交点间的离散点
                                              Cvertex.vertices.add(AssitFault.vertexes.get(j));

                                          }
                                          for (int j = 0; j <ver[1].id+1; j++) {
                                              Cvertex.vertices.add(AssitFault.vertexes.get(j));
                                          }
                                      }else {
                                          Cvertex.vertices.add(AssitFault.vertexes.get(Cvertex.Cvertex.AssisinserIndexMain));
                                      }
                            Cvertex.vertices.add(ver[1].Cvertex);
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
    public static Fault SaveFaultinfor(String path, String name,int p) throws IOException {
        System.out.println(path);
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
//        ispinch=pinchname(cell.getStringCellValue());
        ispinch=0;
        MFtype=false;
        Cell cell1;


        for (int i = 1; i <=realRowNum; i++) {
            Vertex vertex = new Vertex();
            row=sheet.getRow(i);
            cell = row.getCell(0);
//            cell.setCellType(CellType.NUMERIC);
            if(cell.getCellTypeEnum()==CellType.NUMERIC)
                vertex.setSid(String.valueOf(cell.getNumericCellValue()));
            else if(cell.getCellTypeEnum()==CellType.STRING)
                vertex.setSid(cell.getStringCellValue());
//            else
//                vertex.setSid(String.valueOf(cell.getNumericCellValue()));
                //-4090000
            cell = row.getCell(1);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setX(cell.getNumericCellValue());//-4090000
//            vertex.setX(Double.parseDouble(String.format("%.2f", cell.getNumericCellValue())));
            cell=row.getCell(2);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setY(cell.getNumericCellValue());//-38530000
//            vertex.setY(Double.parseDouble(String.format("%.2f", cell.getNumericCellValue())));
            cell=row.getCell(3);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setZ(cell.getNumericCellValue());
            //cell=row.getCell(4);
//            cell.setCellType(CellType.NUMERIC);
           // vertex.setType ((int) cell.getNumericCellValue());
            vertex.id=i-1;
            vertices.add(vertex);
        }

//        cell1=row.getCell(s);
//        ctitle[s-1]=cell1.getStringCellValue();


        Fault fault = new Fault(MFtype,name,vertices,ispinch);


        return fault;
    }
    public static Fault SaveFaultinfor(String path, String name) throws IOException {
        System.out.println(path);
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
//        ispinch=pinchname(cell.getStringCellValue());
        ispinch=0;
        MFtype=false;
        Cell cell1;

        for (int i = 1; i <=realRowNum; i++) {
            Vertex vertex = new Vertex();
            row=sheet.getRow(i);
            cell = row.getCell(0);
            if(cell==null)
                continue;
            else
                if(cell.getCellType() == CellType.forInt(0))
                vertex.setName(String.valueOf(cell.getNumericCellValue()));
            else
                vertex.setName(cell.getStringCellValue());
            cell = row.getCell(1);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setX(cell.getNumericCellValue());//-4090000
//            vertex.setX(Double.parseDouble(String.format("%.2f", cell.getNumericCellValue())));
            cell=row.getCell(2);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setY(cell.getNumericCellValue());//-38530000
//            vertex.setY(Double.parseDouble(String.format("%.2f", cell.getNumericCellValue())));
            cell=row.getCell(3);
//            cell.setCellType(CellType.NUMERIC);
            vertex.setZ(cell.getNumericCellValue());
            cell=row.getCell(4);
//            cell.setCellType(CellType.NUMERIC);
            if(cell==null)
                continue;
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
//        if (start1.x > start2.x) {
//            // 两条线段交换
//            swap(start1, start2);
//            swap(end1, end2);
//        }
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
    private static void writeTest(List<Vertex> jieguo, String MyPath, String MFname) throws IOException {


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
        cell=row0.createCell(4);
        cell.setCellValue("源数据id名");
        cell=row0.createCell(5);
        cell.setCellValue("所属断层名");

        //1 2 3 4列
        for (int i = 0; i < jieguo.size(); i++) {
            row0=sheet.createRow(i+1);
            cell=row0.createCell(0,CellType.STRING);
            cell.setCellValue(jieguo.get(i).Sid);
            cell=row0.createCell(1,CellType.NUMERIC);
            cell.setCellValue(jieguo.get(i).x);
            cell=row0.createCell(2,CellType.NUMERIC);
            cell.setCellValue(jieguo.get(i).y);
            cell=row0.createCell(3,CellType.NUMERIC);
            cell.setCellValue(jieguo.get(i).z);
            cell=row0.createCell(4,CellType.STRING);
            cell.setCellValue(jieguo.get(i).name);
            cell=row0.createCell(5,CellType.STRING);
            cell.setCellValue(jieguo.get(i).Faultname);

        }


        try {
            FileOutputStream fos = new FileOutputStream(MyPath+"\\"+MFname+"_cq.xlsx");
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
    public static List<MFault> readtxt(String path) throws FileNotFoundException {
        List<MFault> faults=new ArrayList<>();


        Scanner scanner1 = new Scanner(new File(path));
        int  num = scanner1.nextInt();

        for (int i = 0; i < num; i++) {
            MFault fault=new MFault();
            String MF= scanner1.next();
            fault.MFname=MF;

            String F=scanner1.nextLine();
            String F1=scanner1.nextLine();
            String []arr=F1.split("\\s+");
            for (String s : arr) {
                fault.Fname.add(s);
            }

            faults.add(fault);
        }
        System.out.println("cehi");

        return faults;
    }
    public static Vertex [] exchange(Vertex [] distance){
        Vertex[] dis=new Vertex[2];
        dis[0]=distance[1];
        dis[1]=distance[0];
        return dis;
    }
}
