package com.zidong;

/**
 * 承载表格数据类
 */
public class Demodata_2 {
    private double x;
    private double y;
    private double z;
    private double daiqiuz;

    public Demodata_2() {
    }

    public Demodata_2(double x, double y, double z, double daiqiuz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.daiqiuz = daiqiuz;
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

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getDaiqiuz() {
        return daiqiuz;
    }

    public void setDaiqiuz(double daiqiuz) {
        this.daiqiuz = daiqiuz;
    }


    @Override
    public String toString() {
        return "Demodata_2{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", daiqiuz=" + daiqiuz +
                '}';
    }
}
