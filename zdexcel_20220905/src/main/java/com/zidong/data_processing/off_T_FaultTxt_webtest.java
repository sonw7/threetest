package com.zidong.data_processing;

import com.zidong.drillsdate.triangle;
import com.zidong.drillsdate.vertex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class off_T_FaultTxt_webtest {
    public static List<vertex> vertices = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        GeoDataLoader_webgl geoDataLoader = new GeoDataLoader_webgl();


        List<String> Filespath = new ArrayList<>();
        List<List<vertex>> allvertexlist = new ArrayList<>();//保存每个断层的点list
        List<List<triangle>> alltrianglelist = new ArrayList<>();//保存每个断层的triangleList

        int Faultnum=0;


        //获取文件路径文件夹下的全部文件列表
        System.out.println("文件有如下：");
        //表示一个文件路径
        File file1 = new File("C:\\Users\\86195\\Desktop\\duqu\\最终数据\\断层面\\断层面数据结构处理\\");
        //用数组把文件夹下的文件存起来
        File[] files = file1.listFiles();
        Faultnum=files.length;
        System.out.printf("总共有"+Faultnum+"个断层面文件");
        //foreach遍历数组
        String[] OFFfilesname=new String[file1.listFiles().length];
        String[] filespath=new String[file1.listFiles().length];
        int im=0;
        for (File file2 : files) {
            //打印文件列表：只读取名称使用getName();
            System.out.println("路径："+file2.getPath());
            System.out.println("文件夹/文件名："+file2.getName());
            filespath[im]=file2.getPath();//文件路径

            OFFfilesname[im]=file2.getName().replace(".off","");//文件名
            im++;
        }
        String content=null;
        int readLine=2;
        int Vnum=0;
        int Tnum=0;
        int [][]VTnum=new int[filespath.length][2];
        for(int f=0;f<filespath.length;f++) {
            List<vertex> vertexlist = new ArrayList<>();
            List<triangle> triangleList=new ArrayList<>();

             content = readPointLIne(filespath[f], readLine);
            System.out.println(content);
            String[] s = content.split(" ");
            Vnum= Integer.parseInt(s[0]);
            Tnum= Integer.parseInt(s[1]);
            System.out.println("点个数："+Vnum+",三角形个数："+Tnum);
            VTnum[f][0]=Vnum;
            VTnum[f][1]=Tnum;
            File file = new File(filespath[f]);
            if(file.isFile() && file.exists())
            {

                try
                {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String text = null;
                    int i=2;
                    while((text = bufferedReader.readLine()) != null)
                    {
                        if(i!=0){
                            i--;
                            continue;
                        }else if(Vnum!=0){
                            String row = "";//获取读取的行字符串

                            for(int j=0;j<text.length();j++)
                                if (text.charAt(j) != '\0') {

                                    row += text.charAt(j);
                                }

                            String[] sv = row.split(" ");
                            vertex v= new vertex(Double.parseDouble(sv[0]),Double.parseDouble(sv[1]),Double.parseDouble(sv[2]));
                            vertexlist.add(v);
                            Vnum--;

                        }else if(Tnum!=0){
                            String row = "";//获取读取的行字符串

                            for(int j=0;j<text.length();j++)
                                if (text.charAt(j) != '\0') {

                                    row += text.charAt(j);
                                }

                            String[] st = row.split(" ");
                            triangle t = new triangle(Integer.parseInt(st[0]),Integer.parseInt(st[1]),Integer.parseInt(st[2]));
                            triangleList.add(t);
                            Tnum--;

                        }


                    }
                    bufferedReader.close();
                } catch (Exception e)
                {

                    e.printStackTrace();
                }
            }
            allvertexlist.add(vertexlist);
            alltrianglelist.add(triangleList);


        }


        System.out.println("已经进行加减，开始写入原文件");
        writeFaultTxt(filespath.length,OFFfilesname,VTnum,allvertexlist,alltrianglelist);
    }
    public static String readPointLIne(String fileName,int readLine) throws IOException{
        String line;//读取每行的内容
        try (
                BufferedReader br = Files.newBufferedReader(Paths.get(fileName))){
            int i=0;
            //每次读取一行，一行一行的读取 br.readLine()
            while ((line = br.readLine()) != null) {
                i++;
                if(i==readLine){
                    return line;
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Boolean writeFaultTxt(int faultnum,String Faultname[],int VTnum [][],List<List<vertex>> vertices,List<List<triangle>> triangles) throws IOException {
        String offPath = "C:\\fakepath\\Fault.txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(offPath));


       double x=0,y=0,z=0;
        HashMap<Integer, Integer> vmap = new HashMap<>();
        // HashMap<Integer,Integer> vmapBottom = new HashMap<>();


        out.write(faultnum + "\n");

        for(int i=0;i<faultnum;i++){
            Iterable<vertex> itvTop = vertices.get(i);
            Iterable<triangle> itTop = triangles.get(i);
            out.write(Faultname[i]+"\n");
            out.write(VTnum[i][0]+ " ");
        for (vertex v : itvTop) {

            x=v.y*100+5000;
            y=v.x*100;
            z=v.z*100;
//            out.write(v.id+" "+v.x + " " + v.y + " " + z + "\n");
            out.write(x + " " + y + " " + z + " ");

        }
        out.write("\n");
            out.write(VTnum[i][1]+ " ");
            for (triangle t : itTop) {
            out.write("-1 "+t.v1+" "+t.v2 + " " + t.v3 + " ");

            }
            out.write("\n");


    }
        out.close();
        return true;
    }
}
