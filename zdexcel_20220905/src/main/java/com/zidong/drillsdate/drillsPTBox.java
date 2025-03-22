package com.zidong.drillsdate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//钻孔转正方体BOX OFF数据类型显示
public class drillsPTBox {
    

    public static void main(String[] args) throws IOException {
        List<drill> drills=new ArrayList<>();
        drills=get_geodrillsVertex();
        System.out.printf("读入钻孔数据准备进行处理");
        List<drillslayer> drillslayers=new ArrayList<>();
        for(int m0=0;m0<23;m0++){
            drillslayer dl=new drillslayer();
            for(int m1 = 0; m1< drills.size(); m1++){
                double x = 0;
                double y = 0;
                double z = 0;
                vertex v= new vertex(x, y, z);
                vertex v1= new vertex(x, y, z);
                if(drills.get(m1).layerTopValue.get(m0)!=99.99&& drills.get(m1).layerTopValue.get(m0 + 1) !=99.99){
                    v.x=drills.get(m1).x;
                    v.y=drills.get(m1).y;
                    v.z=drills.get(m1).layerTopValue.get(m0);
                    v1.x=drills.get(m1).x;
                    v1.y=drills.get(m1).y;
                    v1.z=drills.get(m1).layerTopValue.get(m0+1);
                    dl.ldP.add(v);
                    dl.ldP.add(v1);
                }
            }
            drillslayers.add(dl);

        }
        List<drillslayer> drillslayersOFF=new ArrayList<>();
        drillslayersOFF=transformOFF(drillslayers);
        System.out.printf("每层钻孔坐标数据读入并处理成BOX所需的形式，准备写OFF文件。");
        writeELayerOFF(drillslayersOFF);

    }
    public static List get_geodrillsVertex(){
        List <drill> drills= new ArrayList<>();
        GeoDataLoader g=new GeoDataLoader();
        String filepath="C:\\Users\\86195\\Desktop\\duqu\\钻孔meshlab\\hole.xlsx";
        drills=g.readGeoVertices(filepath);
        return drills;

    }
    public static List transformOFF(List<drillslayer> drillslayers){
        List<drillslayer> drillslayersoff=new ArrayList<>();
        for(int m0=0;m0<drillslayers.size();m0++){
            drillslayer dloff=new drillslayer();
            for(int m1 = 0; m1< drillslayers.get(m0).ldP.size(); m1=m1+2){
                double x = 0;
                double y = 0;
                double z = 0;
                vertex v0= new vertex(x, y, z);
                vertex v1= new vertex(x, y, z);
                vertex v2= new vertex(x, y, z);
                vertex v3= new vertex(x, y, z);
                vertex v4= new vertex(x, y, z);
                vertex v5= new vertex(x, y, z);
                vertex v6= new vertex(x, y, z);
                vertex v7= new vertex(x, y, z);
                v0.x=drillslayers.get(m0).ldP.get(m1).x-0.1;
                v0.y=drillslayers.get(m0).ldP.get(m1).y-0.1;
                v0.z=drillslayers.get(m0).ldP.get(m1).z;

                v1.x=drillslayers.get(m0).ldP.get(m1).x-0.1;
                v1.y=drillslayers.get(m0).ldP.get(m1).y+0.1;
                v1.z=drillslayers.get(m0).ldP.get(m1).z;

                v2.x=drillslayers.get(m0).ldP.get(m1).x+0.1;
                v2.y=drillslayers.get(m0).ldP.get(m1).y+0.1;
                v2.z=drillslayers.get(m0).ldP.get(m1).z;

                v3.x=drillslayers.get(m0).ldP.get(m1).x+0.1;
                v3.y=drillslayers.get(m0).ldP.get(m1).y-0.1;
                v3.z=drillslayers.get(m0).ldP.get(m1).z;

                v4.x=drillslayers.get(m0).ldP.get(m1+1).x-0.1;
                v4.y=drillslayers.get(m0).ldP.get(m1+1).y-0.1;
                v4.z=drillslayers.get(m0).ldP.get(m1+1).z;

                v5.x=drillslayers.get(m0).ldP.get(m1+1).x-0.1;
                v5.y=drillslayers.get(m0).ldP.get(m1+1).y+0.1;
                v5.z=drillslayers.get(m0).ldP.get(m1+1).z;

                v6.x=drillslayers.get(m0).ldP.get(m1+1).x+0.1;
                v6.y=drillslayers.get(m0).ldP.get(m1+1).y+0.1;
                v6.z=drillslayers.get(m0).ldP.get(m1+1).z;

                v7.x=drillslayers.get(m0).ldP.get(m1+1).x+0.1;
                v7.y=drillslayers.get(m0).ldP.get(m1+1).y-0.1;
                v7.z=drillslayers.get(m0).ldP.get(m1+1).z;
                dloff.ldP.add(v0);
                dloff.ldP.add(v1);
                dloff.ldP.add(v2);
                dloff.ldP.add(v3);
                dloff.ldP.add(v4);
                dloff.ldP.add(v5);
                dloff.ldP.add(v6);
                dloff.ldP.add(v7);
            }
            drillslayersoff.add(dloff);
        }

        return drillslayersoff;
    }
    public static Boolean writeLayerOFF(List<drillslayer> drillslayersOFF) throws IOException {


        int vsize=0;
        int tsize=0;
        HashMap<Integer,Integer> vmap = new HashMap<>();
        // HashMap<Integer,Integer> vmapBottom = new HashMap<>();

        for(int i=0;i<drillslayersOFF.size();i++)
        {
            vsize=vsize+drillslayersOFF.get(i).ldP.size();

        }
        tsize=(vsize/8)*6;

        String filename = null;
        String offPath = "C:\\Users\\86195\\Desktop\\duqu\\钻孔meshlab\\drilloff\\ceshi.off";
        BufferedWriter out = new BufferedWriter(new FileWriter(offPath));
        out.write("OFF\n");
        out.write(vsize+" "+tsize+" "+0+ "\n");




        for(int m=0;m<drillslayersOFF.size();m++)
        {
            for(int m0=0;m0<drillslayersOFF.get(m).ldP.size();m0++){
                out.write(drillslayersOFF.get(m).ldP.get(m0).x + " " + drillslayersOFF.get(m).ldP.get(m0).y + " " + drillslayersOFF.get(m).ldP.get(m0).z + "\n");
            }
        }


        int n0,n1,n2,n3,n4,n5,n6,n7;


        for(int n=0;n<vsize;n=n+8){

            n0=n;
            n1=n+1;
            n2=n+2;
            n3=n+3;
            n4=n+4;
            n5=n+5;
            n6=n+6;
            n7=n+7;
            out.write(4+" "+n0+" "+n1+" "+n2+" "+n3+"\n");
            out.write(4+" "+n4+" "+n5+" "+n6+" "+n7+"\n");
            out.write(4+" "+n0+" "+n1+" "+n5+" "+n4+"\n");
            out.write(4+" "+n1+" "+n2+" "+n6+" "+n5+"\n");
            out.write(4+" "+n2+" "+n3+" "+n7+" "+n6+"\n");
            out.write(4+" "+n3+" "+n0+" "+n4+" "+n7+"\n");



        }

        out.close();
        return true;
    }
    public static Boolean writeELayerOFF(List<drillslayer> drillslayersOFF) throws IOException {


        int vsize=0;
        int tsize=0;

        for(int l=0;l<drillslayersOFF.size();l++){
            String filename = String.valueOf(l);
            String offPath = "C:\\Users\\86195\\Desktop\\duqu\\钻孔meshlab\\drilloff\\"+filename+".off";
            BufferedWriter out = new BufferedWriter(new FileWriter(offPath));

            vsize=drillslayersOFF.get(l).ldP.size();
            tsize=(vsize/8)*6;

            out.write("OFF\n");
            out.write(vsize+" "+tsize+" "+0+ "\n");

            for(int m0=0;m0<drillslayersOFF.get(l).ldP.size();m0++){
                out.write(drillslayersOFF.get(l).ldP.get(m0).x + " " + drillslayersOFF.get(l).ldP.get(m0).y + " " + drillslayersOFF.get(l).ldP.get(m0).z + "\n");
            }
            int n0,n1,n2,n3,n4,n5,n6,n7;


            for(int n=0;n<vsize;n=n+8){

                n0=n;
                n1=n+1;
                n2=n+2;
                n3=n+3;
                n4=n+4;
                n5=n+5;
                n6=n+6;
                n7=n+7;
                out.write(4+" "+n0+" "+n1+" "+n2+" "+n3+" "+colorget(l)+"\n");
                out.write(4+" "+n4+" "+n5+" "+n6+" "+n7+" "+colorget(l)+"\n");
                out.write(4+" "+n0+" "+n1+" "+n5+" "+n4+" "+colorget(l)+"\n");
                out.write(4+" "+n1+" "+n2+" "+n6+" "+n5+" "+colorget(l)+"\n");
                out.write(4+" "+n2+" "+n3+" "+n7+" "+n6+" "+colorget(l)+"\n");
                out.write(4+" "+n3+" "+n0+" "+n4+" "+n7+" "+colorget(l)+"\n");

        }

            out.close();
        }

        return true;
    }

    public static  String colorget(int num) {
        String color;
        switch (num){
            case  0:
                color="160 102 211";
                break;
            case  1:
                color="255 255 0";
                break;
            case 2:
                color= "0 199 140";
                break;
            case  3:
                color="176 224 230";
                break;
            case  4:
                color="0 255 255";
                break;
            case  5:
                color="0 255 0";
                break;
            case  6:
                color="255 97 0";
                break;
            case  7:
                color="255 0 0";
                break;
            case  8:
                color="160 32 240";
                break;
            case  9:
                color="128 42 42";
                break;
            case  10:
                color="240 230 140";
                break;
            case  11:
                color="221 160 221";
                break;
            case  12:
                color="255 0 255";
                break;
            case  13:
                color="188 143 143";
                break;
            case  14:
                color="41 36 33";
                break;
            case  15:
                color="255 127 80";
                break;
            case  16:
                color="25 25 112";
                break;
            case  17:
                color="128 42 42";
                break;
            case  18:
                color="112 128 105";
                break;
            case  19:
                color="56 94 15";
                break;
            case 20:
                color="250 240 230";
                break;
            case 21:
                color="255 192 203";
                break;
            case 22:
                color="11 23 70";
                break;
            default:
                color="135 38 87";
                break;
        }

        return color ;

    }
}

