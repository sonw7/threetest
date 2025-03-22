package com.kjw_Auto_HOB.Class;


import java.util.List;

public class Fault {

    private  static final int a=0;
    public boolean isMF; //0辅断层，1主断层
    public int TOttpe;//0不尖灭，1尖灭。
    public String name;
    public List<Vertex> vertexes;//默认逆时针排序离散点
    public List<Vertex> Cvertexes;//交点
    public int CVnumber;//交点个数
    public int ispinch;
    /*主断层：MF  =0;辅断层无尖灭点：AF=1;辅断层有一个尖灭点：AFS=2;辅断层有两个尖灭点：AFS=3*/
    public int GXnum;//交点在同一线段的个数


    public Fault() {
        this.GXnum=0;
    }

    public Fault(boolean isMF, String name, List<Vertex> vertexes, int ispinch) {
        this.isMF = isMF;
        this.name = name;
        this.vertexes = vertexes;
        this.ispinch = ispinch;
        this.GXnum=0;
    }

    @Override
    public String toString() {
        return "Fault{" +
                "isMF=" + isMF +
                ", TOttpe=" + TOttpe +
                ", name='" + name + '\'' +
                ", vertexes=" + vertexes +
                ", Cvertexes=" + Cvertexes +
                ", CVnumber=" + CVnumber +
                ", ispinch=" + ispinch +
                '}';
    }

    public boolean getType() {
        return isMF;
    }

    public void setType(boolean isMF) {
        this.isMF = isMF;
    }

    public int getTOttpe() {
        return TOttpe;
    }

    public void setTOttpe(int TOttpe) {
        this.TOttpe = TOttpe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public void setVertexes(List<Vertex> vertexes) {
        this.vertexes = vertexes;
    }

    public List<Vertex> getCvertexes() {
        return Cvertexes;
    }

    public void setCvertexes(List<Vertex> cvertexes) {
        Cvertexes = cvertexes;
        CVnumber=cvertexes.size();
    }

    public int getCVnumber() {
        return CVnumber;
    }

    public void setCVnumber(int CVnumber) {
        this.CVnumber = CVnumber;
    }

    public int getIspinch() {
        return ispinch;
    }

    public void setIspinch(int ispinch) {
        this.ispinch = ispinch;
    }
}
