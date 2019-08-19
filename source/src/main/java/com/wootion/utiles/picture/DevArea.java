package com.wootion.utiles.picture;

public class DevArea {
    public String devId;
    public String devName;
    public int point1x;
    public int point1y;
    public int point2x;
    public int point2y;
    public int point3x;
    public int point3y;
    public int point4x;
    public int point4y;
    public int point5x;
    public int point5y;

    public DevArea(){

    }

    public DevArea(String id, String fileName, int point1x, int point1y, int point2x, int point2y,int point3x, int point3y,int point4x, int point4y,int point5x, int point5y) {
        this.devId=id;
        this.devName = fileName;
        this.point1x  = point1x;
        this.point1y = point1y;
        this.point2x = point2x;
        this.point2y = point2y;
        this.point3x = point3x;
        this.point3y = point3y;
        this.point4x = point4y;
        this.point5x = point5x;
        this.point5y = point5y;
    }

}
