package com.wootion.task.map2;

import com.wootion.utiles.DataCache;

public class Coordinate {

    /**
     * 导航的真实坐标转换为网页页面地图坐标
     */
    public static double[] nav2Web(double x, double y, double scale) {
        double[] pt = new double[2];
        pt[0] = x;
        pt[1] = y;


//        int mapAngle=DataCache.getSysParamInt("map.angle");
//
//        //坐标旋转，逆时针，角度angle
//        double angle=Math.PI*(mapAngle/180.0);
//        pt[0]=Math.cos(angle)*x-Math.sin(angle)*y;
//        pt[1]=Math.cos(angle)*y+Math.sin(angle)*x;

        //从XY坐标转换到像素坐标,距离对应像素
//        double unit = DataCache.getSysParamDouble("map.pixDistance");
        pt[0] = pt[0]/scale;
        pt[1] = pt[1]/scale;

//        //平移
//        int diffX = DataCache.getSysParamInt("map.diffX");
//        int diffY = DataCache.getSysParamInt("map.diffY");
//        pt[0] = pt[0]+diffX;
//        pt[1] = pt[1]+diffY;

        return pt;
    }


    /**
     * 像素坐标==》XY坐标
     * @param x  实际坐标，单位米
     * @param y   实际坐标，单位米
     * @return
     */
//    public static double[] transPix2XY(double x, double y) {
//        double[] pt = new double[2];
//
//       // double unit = 0.05;  //0.05m 对应一个像素
//        double unit = DataCache.getSysParamDouble("map.pixDistance");
//        pt[0] = x*unit;
//        pt[1] = y*unit;
//        return pt;
//    }

//added by btrmg for paint maintArea and house icon 2018.11.13
//    public static int[] transXY2Pix(int x, int y){
//        int[] pt= new int[2];
//      //  pt[0] = x + sysParam.transX;
//       // pt[1] = -sysParam.transY - y;
//        pt[0] =x;
//        pt[1]=y;
//        return pt;
//    }
   // added end





    /**
     * 网页页面地图坐标转换为导航的真实坐标
     */
    public static double[] web2Nav (double x, double y, double scale) {
        double[] pt = new double[2];
        pt[0] = x;
        pt[1] = y;

//        double[] pt1=new double[2];

//        //平移
//        int diffX = DataCache.getSysParamInt("map.diffX");
//        int diffY = DataCache.getSysParamInt("map.diffY");
//
//        pt[0] = x-diffX;
//        pt[1] = y-diffY;



        //从XY坐标转换到像素坐标,距离对应像素
//        double unit = DataCache.getSysParamDouble("map.pixDistance");
        pt[0] = pt[0]*scale;
        pt[1] = pt[1]*scale;

//        //坐标旋转，逆时针，角度angle
//        int mapAngle=DataCache.getSysParamInt("map.angle");
//        double angle=Math.PI*(mapAngle/180.0);
//        pt1[0]=Math.cos(angle)*pt[0]+Math.sin(angle)*pt[1];
//        pt1[1]=Math.cos(angle)*pt[1]-Math.sin(angle)*pt[0];



        return pt;
    }

    public static void main(String args[]){
    }
}
