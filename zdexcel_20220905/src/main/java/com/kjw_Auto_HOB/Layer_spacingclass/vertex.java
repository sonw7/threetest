package com.kjw_Auto_HOB.Layer_spacingclass;

public class vertex {
    public int id;
    public double x;
    public double y;
    public double z;
    public float distance;

    public float x1;
    public float y1;
    public float z1;
    public vertex(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setId(int index) {
        this.id = index;
    }
    public void setDistance(float distance){
        this.distance=distance;
    }

}
