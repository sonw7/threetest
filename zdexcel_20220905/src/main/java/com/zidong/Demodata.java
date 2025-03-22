package com.zidong;

/**
 * 承载表格数据类
 */
public class Demodata {
    private double shangx;
    private double shangy;
    private double shangz;
    private double xiax;
    private double xiay;
    private double xiaz;
    private double yzshangz;
    private double yzxiaz;

    public Demodata() {
    }

    public Demodata(double shangx, double shangy, double shangz, double xiax, double xiay, double xiaz, double yzshangz, double yzxiaz) {
        this.shangx = shangx;
        this.shangy = shangy;
        this.shangz = shangz;
        this.xiax = xiax;
        this.xiay = xiay;
        this.xiaz = xiaz;
        this.yzshangz = yzshangz;
        this.yzxiaz = yzxiaz;
    }

    public double getShangx() {
        return shangx;
    }

    public void setShangx(double shangx) {
        this.shangx = shangx;
    }

    public double getShangy() {
        return shangy;
    }

    public void setShangy(double shangy) {
        this.shangy = shangy;
    }

    public double getShangz() {
        return shangz;
    }

    public void setShangz(double shangz) {
        this.shangz = shangz;
    }

    public double getXiax() {
        return xiax;
    }

    public void setXiax(double xiax) {
        this.xiax = xiax;
    }

    public double getXiay() {
        return xiay;
    }

    public void setXiay(double xiay) {
        this.xiay = xiay;
    }

    public double getXiaz() {
        return xiaz;
    }

    public void setXiaz(double xiaz) {
        this.xiaz = xiaz;
    }

    public double getYzshangz() {
        return yzshangz;
    }

    public void setYzshangz(double yzshangz) {
        this.yzshangz = yzshangz;
    }

    public double getYzxiaz() {
        return yzxiaz;
    }

    public void setYzxiaz(double yzxiaz) {
        this.yzxiaz = yzxiaz;
    }

    @Override
    public String toString() {
        return "Demodata{" +
                "shangx=" + shangx +
                ", shangy=" + shangy +
                ", shangz=" + shangz +
                ", xiax=" + xiax +
                ", xiay=" + xiay +
                ", xiaz=" + xiaz +
                ", yzshangz=" + yzshangz +
                ", yzxiaz=" + yzxiaz +
                '}';
    }
}
