package com.kjw_Auto_HOB.Class;

@SuppressWarnings("all")
public class Vertex {
    public double x;
    public double y;
    public double z;



    public int id;
    public String Sid;
    public String name;

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String Faultname;

    public String getFaultname() {
        return Faultname;
    }

    public void setFaultname(String faultname) {
        Faultname = faultname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean Ctype;//false;离散点，true交点。
    public int type;//0离散点，1端点，2尖灭端点

    public CVertexList CVertexList;//主交点到次交点 之间所有的点
    public boolean isMainCV;//false true主交点 排在次交点前
//    public boolean isCvertex;
    public boolean isdelete;//判断是否需要剔除
    public int insertIndex;//应当插入点序号
    public int AssisinserIndexMain;//辅助断层首个交点id
    public int AssisinserIndexAssi;//辅助断层次交点id
    public Vertex Cvertex;//

    public double distance;//距离


    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public Vertex(double x, double y, double z,int id,int type) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public Vertex() {
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isCtype() {
        return Ctype;
    }

    public void setCtype(boolean ctype) {
        Ctype = ctype;
    }

    public com.kjw_Auto_HOB.Class.CVertexList getCVertexList() {
        return CVertexList;
    }

    public void setCVertexList(com.kjw_Auto_HOB.Class.CVertexList CVertexList) {
        this.CVertexList = CVertexList;
    }

    public boolean isCMtype() {
        return isMainCV;
    }

    public void setCMtype(boolean isMainCV) {
        this.isMainCV = isMainCV;
    }
}
