package com.wootion.utiles.picture;

import com.wootion.task.map2.Coordinate;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class IndiscriminateArea {
    private String uId;
    private String AreaName;
    private double point1x;
    private double point1y;
    private double point2x;
    private double point2y;
    private double point3x;
    private double point3y;
    private double point4x;
    private double point4y;
    private double point5x;
    private double point5y;
    private Color backcolor;
    private Color lineColor;
    private  boolean needbackcolor;
    private  int linetype;

    public boolean isNeedbackcolor() {
        return needbackcolor;
    }

    public void setNeedbackcolor(boolean needbackcolor) {
        this.needbackcolor = needbackcolor;
    }

    public int getLinetype() {
        return linetype;
    }

    public void setLinetype(int linetype) {
        this.linetype = linetype;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public double getPoint1x() {
        return point1x;
    }

    public void setPoint1x(double point1x) {
        this.point1x = point1x;
    }

    public double getPoint1y() {
        return point1y;
    }

    public void setPoint1y(double point1y) {
        this.point1y = point1y;
    }

    public double getPoint2x() {
        return point2x;
    }

    public void setPoint2x(double point2x) {
        this.point2x = point2x;
    }

    public double getPoint2y() {
        return point2y;
    }

    public void setPoint2y(double point2y) {
        this.point2y = point2y;
    }

    public double getPoint3x() {
        return point3x;
    }

    public void setPoint3x(double point3x) {
        this.point3x = point3x;
    }

    public double getPoint3y() {
        return point3y;
    }

    public void setPoint3y(double point3y) {
        this.point3y = point3y;
    }

    public double getPoint4x() {
        return point4x;
    }

    public void setPoint4x(double point4x) {
        this.point4x = point4x;
    }

    public double getPoint4y() {
        return point4y;
    }

    public void setPoint4y(double point4y) {
        this.point4y = point4y;
    }

    public double getPoint5x() {
        return point5x;
    }

    public void setPoint5x(double point5x) {
        this.point5x = point5x;
    }

    public double getPoint5y() {
        return point5y;
    }

    public void setPoint5y(double point5y) {
        this.point5y = point5y;
    }

    public Color getBackcolor() {
        return backcolor;
    }

    public void setBackcolor(Color backcolor) {
        this.backcolor = backcolor;
    }

    public IndiscriminateArea(){

    }

    public void AddAreaValue(String uid, String fileName, double point1x, double point1y, double point2x, double point2y, double point3x,
                              double point3y, double point4x, double point4y, double point5x, double point5y,Color color,Color linecolor,
                              boolean needbackcolor, int linetype) {
        this.uId= uid;
        this.AreaName = fileName;
        this.point1x  = point1x;
        this.point1y = point1y;
        this.point2x = point2x;
        this.point2y = point2y;
        this.point3x = point3x;
        this.point3y = point3y;
        this.point4x = point4x;
        this.point4y = point4y;
        this.point5x = point5x;
        this.point5y = point5y;
        this.backcolor = color;
        this.lineColor = linecolor;
        this.needbackcolor = needbackcolor;
        this.linetype = linetype;
    }

    public void paintSingleArea(Graphics2D g2d, Double scale){
        int [] pointx  = new int[5];
        int [] pointy  = new int[5];

        double[] pt1 = Coordinate.nav2Web(this.point1x,this.point1y,scale);
        double[] pt2 = Coordinate.nav2Web(this.point2x,this.point2y,scale);
        double[] pt3 = Coordinate.nav2Web(this.point3x,this.point3y,scale);
        double[] pt4 = Coordinate.nav2Web(this.point4x,this.point4y,scale);
        double[] pt5 = Coordinate.nav2Web(this.point5x,this.point5y,scale);
        pointx[0] = (int)pt1[0];
        pointy[0] = (int)pt1[1];
        pointx[1] = (int)pt2[0];
        pointy[1] = (int)pt2[1];
        pointx[2] = (int)pt3[0];
        pointy[2] = (int)pt3[1];
        pointx[3] = (int)pt4[0];
        pointy[3] = (int)pt4[1];
        pointx[4] = (int)pt5[0];
        pointy[4] = (int)pt5[1];
        if(null != this.AreaName) {
            g2d.setColor(Color.RED);
            g2d.drawString(AreaName,pointx[0],-pointy[0]);
        }
        AffineTransform at2 = new AffineTransform();
        at2.scale(1, -1);
        g2d.transform(at2);

        if(linetype == 1)
        {
            Stroke bs = new BasicStroke(2.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new   float[]{16,4},0);
            g2d.setStroke(bs);
        }
        else
        {
            Stroke bs = new BasicStroke(2.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
            g2d.setStroke(bs);
        }
        g2d.setColor(lineColor);
        g2d.drawPolygon(pointx,pointy,4);
        if(needbackcolor){
            g2d.setColor(backcolor);
            g2d.fillPolygon(pointx,pointy,4);
        }
        AffineTransform at3 = new AffineTransform();
        at3.scale(1, -1);
        g2d.transform(at3);

    }

}
