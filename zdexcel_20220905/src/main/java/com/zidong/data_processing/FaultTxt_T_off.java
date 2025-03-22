package com.zidong.data_processing;

import com.zidong.drillsdate.triangle;
import com.zidong.drillsdate.vertex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FaultTxt_T_off {

    public static void main(String[] args) throws IOException {

        List<List<vertex>> allvertexlist = new ArrayList<>();//保存每个mesh的点list
        List<List<triangle>> alltrianglelist = new ArrayList<>();//保存每个mesh的triangleList

        int Faultnum=0;
        int titleLine=1;//读第2行
        int readLine=2;

        //String Faultpath="C:\\Users\\86195\\Desktop\\duqu\\Fault\\Fault_zone.txt";
        String Faultpath="C:\\Users\\86195\\Desktop\\kjw\\data21.msh";
        String content = readPointLIne(Faultpath, titleLine);
        int layercount = Integer.parseInt(content);//记录mesh个数
        System.out.println("共有"+layercount+"个mesh");
       // String[] OFFmeshname=new String[layercount];//依次记录每个mesh名字

        String[] layersname=new String[layercount];//依次记录每个mesh名字
        int[] layervnum=new int[layercount];//分别记录每个的点个数
        int[] layertnum=new  int[layercount];//分别记录每个的三角形个数

        int i = 0;
        for(int ln=2;ln<=layercount*3+1;ln=ln+3){
            String layername = readPointLIne(Faultpath, ln);
            layersname[i]=layername;
            i++;

        }
        System.out.println("保存mesh文件名称");

        i=0;
        int vnum,vn;
        for(int ln=3;ln<=layercount*3+1;ln+=3){
            String layervertex = readPointLIne(Faultpath, ln);
            String[] svn = layervertex.split(" ");
             vn=Integer.parseInt(svn[0]);//记录点个数
            layervnum[i]=vn;
            List<vertex> vertexlist = new ArrayList<>();
            for(vnum=1;vnum<vn*3+1;vnum+=3){
                vertex v= new vertex(Double.parseDouble(svn[vnum+1]),Double.parseDouble(svn[vnum]),Double.parseDouble(svn[vnum+2]));
                vertexlist.add(v);
            }
            i++;
            allvertexlist.add(vertexlist);
        }
        System.out.println("保存每个mesh点数据");
        i=0;
        int tnum,tn;
        for( int ln=4;ln<=layercount*3+1;ln+=3){
            String layervertex = readPointLIne(Faultpath, ln);
            String[] stn = layervertex.split(" ");
            tn=Integer.parseInt(stn[0]);//记录三角形数
            layertnum[i]=tn;
            List<triangle> triangleList=new ArrayList<>();
            for(tnum=1;tnum<tn*4+1;tnum+=4){
                triangle t = new triangle(Integer.parseInt(stn[tnum+1]),Integer.parseInt(stn[tnum+2]),Integer.parseInt(stn[tnum+3]));
                triangleList.add(t);
            }
            i++;
            alltrianglelist.add(triangleList);
        }
        System.out.println("保存每个mesh的三角形数据");

        //获取文件路径文件夹下的全部文件列表
        System.out.println("准备生成off文件");
        for(int a=0;a<layercount;a++)
            writeOFFl(layersname[a], allvertexlist.get(a), alltrianglelist.get(a));

            //表示一个文件路径
//        File file1 = new File("C:\\Users\\86195\\Desktop\\duqu\\最终数据\\缓冲带\\缓冲带数据结构处理\\");
//        //用数组把文件夹下的文件存起来
//        File[] files = file1.listFiles();
//        Faultnum=files.length;
//        System.out.printf("总共有"+Faultnum+"个断层面文件");
//        //foreach遍历数组
//        String[] OFFfilesname=new String[file1.listFiles().length];
//        String[] filespath=new String[file1.listFiles().length];
//        int im=0;
//        for (File file2 : files) {
//            //打印文件列表：只读取名称使用getName();
//            System.out.println("路径："+file2.getPath());
//            System.out.println("文件夹/文件名："+file2.getName());
//            filespath[im]=file2.getPath();//文件路径
//
//            OFFfilesname[im]=file2.getName().replace(".off","");//文件名
//            im++;
//        }
//
//
//        int Vnum=0;
//        int Tnum=0;
//        int [][]VTnum=new int[filespath.length][2];
//        for(int f=1;f<filespath.length;f++) {
//            List<vertex> vertexlist = new ArrayList<>();
//            List<triangle> triangleList=new ArrayList<>();
//
//            content = readPointLIne(filespath[f], readLine);
//            System.out.println(content);
//            String[] s = content.split(" ");
//            Vnum= Integer.parseInt(s[0]);
//            Tnum= Integer.parseInt(s[1]);
//            System.out.println("点个数："+Vnum+",三角形个数："+Tnum);
//            VTnum[f][0]=Vnum;
//            VTnum[f][1]=Tnum;
//
//
//            File file = new File(filespath[f]);
//            if(file.isFile() && file.exists())
//            {
//
//                try
//                {
//                    FileInputStream fileInputStream = new FileInputStream(file);
//                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String text = null;
//                    int i1=2;
//                    while((text = bufferedReader.readLine()) != null)
//                    {
//                        if(i!=0){
//                            i--;
//                            continue;
//                        }else if(Vnum!=0){
//                            String row = "";//获取读取的行字符串
//
//                            for(int j=0;j<text.length();j++)
//                                if (text.charAt(j) != '\0') {
//
//                                    row += text.charAt(j);
//                                }
//
//                            String[] sv = row.split(" ");
//                            vertex v= new vertex(Double.parseDouble(sv[0]),Double.parseDouble(sv[1]),Double.parseDouble(sv[2]));
//                            vertexlist.add(v);
//                            Vnum--;
//
//                        }else if(Tnum!=0){
//                            String row = "";//获取读取的行字符串
//
//                            for(int j=0;j<text.length();j++)
//                                if (text.charAt(j) != '\0') {
//
//                                    row += text.charAt(j);
//                                }
//
//                            String[] st = row.split(" ");
//                            triangle t = new triangle(Integer.parseInt(st[0]),Integer.parseInt(st[1]),Integer.parseInt(st[2]));
//                            triangleList.add(t);
//                            Tnum--;
//
//                        }
//
//
//                    }
//                    bufferedReader.close();
//                } catch (Exception e)
//                {
//
//                    e.printStackTrace();
//                }
//            }
//            allvertexlist.add(vertexlist);
//            alltrianglelist.add(triangleList);
//
//
//        }
//
//
//
//
//

        System.out.println("已经进行加减，开始写入原文件");
        //writeFaultTxt(filespath.length,OFFfilesname,VTnum,allvertexlist,alltrianglelist);
    }

    private static void writeOFFl(String s, List<vertex> vertices, List<triangle> triangles) throws IOException {


        String filepath="C:\\Users\\86195\\Desktop\\duqu\\Fault_T_OFF\\"+s+".off";
        System.out.printf(filepath);
        List<vertex> points = vertices;
        List<triangle> triaglevetex=triangles;
        writeLayerOFF(points,triaglevetex,filepath);
    }

    public static String readPointLIne(String fileName,int readLine) throws IOException {
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
    public static Boolean writeLayerOFF(List<vertex> vertices,List<triangle> triangles,String offPath) throws IOException {

        BufferedWriter out = new BufferedWriter(new FileWriter(offPath));
        Iterable<vertex> itvTop=vertices;

//        int vsize=0;
//        int tsize=0;
//        HashMap<Integer,Integer> vmap = new HashMap<>();
        // HashMap<Integer,Integer> vmapBottom = new HashMap<>();
//        for(vertex v :itvTop)
//        {
//
//            vmap.put(v.id,vsize);
//            vsize++;
//        }



//        int [][]tripointid=new int [triangles.size()][3];
//        for (int j = 0; j < triangles.size(); ++j) {
//
//
//            String[] ts1 = triangles.get(j).split(" ");
//
//            tripointid[j][0]=Integer.parseInt(ts1[0]);
//            tripointid[j][1]=Integer.parseInt(ts1[1]);
//            tripointid[j][2]=Integer.parseInt(ts1[2]);
//
//        }

        out.write("OFF\n");
        out.write(vertices.size()+" "+triangles.size()+" "+0+ "\n");
        double x=0,y=0,z=0;
        itvTop=vertices;

        int i=0;
        for(vertex v :itvTop)
        {
            x=(v.x+5000)/100;
            y=v.y/100;
            z=v.z/100;

            out.write(x + " " + y + " " + z + "\n");
            i++;

        }

        //out.write("^\n");
        for(int m0=0;m0<triangles.size();m0++){


            out.write(3+" "+triangles.get(m0).v1+" "+triangles.get(m0).v2+" "+triangles.get(m0).v3+"\n");


        }

        out.close();
        return true;
    }
}
