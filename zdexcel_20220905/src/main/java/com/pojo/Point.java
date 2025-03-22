package com.pojo;

public class Point {
    public int id;
    public double x;
    public double y;
    public double z;


    public Point() {

    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;

        this.z = z;
    }
}
