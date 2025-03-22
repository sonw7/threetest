package com.kjw_Auto_HOB.Layer_spacingclass;

public class drillVertex {

    double drillDepth; //m
    double drillDeviation; //
    double drillPosition;
    double drillVerticalDepth; //m
    double NSCoor;
    double EWCoor;
    Double GeodeticNorthCoor;
    Double GeodeticEastCoor;
    Double altitude;

    public double getDrillDepth() {
        return drillDepth;
    }

    public void setDrillDepth(double drillDepth) {
        this.drillDepth = drillDepth;
    }

    public double getDrillDeviation() {
        return drillDeviation;
    }

    public void setDrillDeviation(double drillDeviation) {
        this.drillDeviation = drillDeviation;
    }

    public double getDrillPosition() {
        return drillPosition;
    }

    public void setDrillPosition(double drillPosition) {
        this.drillPosition = drillPosition;
    }

    public double getDrillVerticalDepth() {
        return drillVerticalDepth;
    }

    public void setDrillVerticalDepth(double drillVerticalDepth) {
        this.drillVerticalDepth = drillVerticalDepth;
    }

    public double getNSCoor() {
        return NSCoor;
    }

    public void setNSCoor(double NSCoor) {
        this.NSCoor = NSCoor;
    }

    public double getEWCoor() {
        return EWCoor;
    }

    public void setEWCoor(double EWCoor) {
        this.EWCoor = EWCoor;
    }

    public Double getGeodeticNorthCoor() {
        return GeodeticNorthCoor;
    }

    public void setGeodeticNorthCoor(Double geodeticNorthCoor) {
        GeodeticNorthCoor = geodeticNorthCoor;
    }

    public Double getGeodeticEastCoor() {
        return GeodeticEastCoor;
    }

    public void setGeodeticEastCoor(Double geodeticEastCoor) {
        GeodeticEastCoor = geodeticEastCoor;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    float x;
    float y;
    float z;
//

    public drillVertex(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }
    public drillVertex(){};

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }



}


