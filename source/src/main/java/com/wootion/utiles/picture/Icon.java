package com.wootion.utiles.picture;

public class Icon {
    public String id;
    public String fileName;
    public String text;
    public int w=20;
    public int h=20;
    public double x;
    public double y;

    public Icon(){

    }

    public Icon(String id,String fileName, String text, int w, int h, double x, double y) {
        this.id=id;
        this.fileName = fileName;
        this.text = text;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
    }

}
