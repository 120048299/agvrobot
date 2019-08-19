package com.wootion.alg;

public class Point {

    public String id;
    public double x;
    public double y;
    public Point(){

    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Point(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
