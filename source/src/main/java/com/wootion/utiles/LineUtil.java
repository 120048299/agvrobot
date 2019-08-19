package com.wootion.utiles;

import com.wootion.alg.Point;

/**
 * 判断两条线段是否相交
 */
public class LineUtil {



    public static void main(String args[]){
        Point p1=new Point(0,0);
        Point p2=new Point(1.01,1.1);
        Point p3=new Point(1,10.8);
        Point p4=new Point(10.5,0);
        boolean ret= LineUtil.intersect(p1,p2,p3,p4);
        System.out.println(ret);

    }


    public static double determinant(double v1, double v2, double v3, double v4)  // 行列式
    {
        return (v1*v3-v2*v4);
    }

    public static boolean intersect(Point aa, Point bb, Point cc, Point dd)
    {
        double delta = determinant(bb.x-aa.x,dd.x-cc.x, dd.y-cc.y, bb.y-aa.y);
        if ( delta<=(1e-6) && delta>=-(1e-6) )  // delta=0，表示两线段重合或平行
        {
            return false;
        }
        double namenda = determinant(dd.x-cc.x, aa.x-cc.x, aa.y-cc.y, dd.y-cc.y) / delta;
        if ( namenda>1 || namenda<0 )
        {
            return false;
        }
        double miu = determinant(bb.x-aa.x, aa.x-cc.x, aa.y-cc.y, bb.y-aa.y) / delta;
        if ( miu>1 || miu<0 )
        {
            return false;
        }
        return true;
    }



    /**
     *
     * @param p1
     * @param p2
     * @return
     */
    public static Point getFoot(Point p1,Point p2,Point p3){
       Point foot=new Point();
       double dx=p1.x-p2.x;
       double dy=p1.y-p2.y;
       double u=(p3.x-p1.x)*dx+(p3.y-p1.y)*dy;
       u/=dx*dx+dy*dy;
       foot.x=p1.x+u*dx;
       foot.y=p1.y+u*dy;
       return foot;
    }

    // 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
    private double pointToLine(int x1, int y1, int x2, int y2, int x0,

                               int y0) {

        double space = 0;

        double a, b, c;

        a = distance(x1, y1, x2, y2);// 线段的长度

        b = distance(x1, y1, x0, y0);// (x1,y1)到点的距离

        c = distance(x2, y2, x0, y0);// (x2,y2)到点的距离

        if (c <= 0.000001 || b <= 0.000001) {

            space = 0;

            return space;

        }

        if (a <= 0.000001) {

            space = b;

            return space;

        }

        if (c * c >= a * a + b * b) {

            space = b;

            return space;

        }

        if (b * b >= a * a + c * c) {

            space = c;

            return space;

        }

        double p = (a + b + c) / 2;// 半周长

        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积

        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）

        return space;

    }

    // 计算两点之间的距离
    private double distance(int x1, int y1, int x2, int y2) {

        double lineLength = 0;

        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)

                * (y1 - y2));

        return lineLength;

    }

    
}
