package com.wootion.utiles;


import com.wootion.alg.Point;

public class Quadrangle {
    /**
     * 点是否在四边形内
     *
     */
    public static boolean pInQuadrangle(Point a, Point b, Point c, Point d, Point p) {
        double dTriangle = triangleArea(a, b, p) + triangleArea(b, c, p) + triangleArea(c, d, p) + triangleArea(d, a, p);
        double dQuadrangle = triangleArea(a, b, c) + triangleArea(c, d, a);
        return dTriangle == dQuadrangle;
    }

    public static boolean pInQuadrangle(float[] px, float[] py, float x, float y) {
        Point a = new Point((int) px[0], (int) py[0]);
        Point b = new Point((int) px[1], (int) py[1]);
        Point c = new Point((int) px[2], (int) py[2]);
        Point d = new Point((int) px[3], (int) py[3]);
        Point p = new Point((int) x, (int) y);

        double dTriangle = triangleArea(a, b, p) + triangleArea(b, c, p) + triangleArea(c, d, p) + triangleArea(d, a, p);
        double dQuadrangle = triangleArea(a, b, c) + triangleArea(c, d, a);
        return dTriangle == dQuadrangle;
    }

    // 返回三个点组成三角形的面积
    private static double triangleArea(Point a, Point b, Point c) {
        double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y - c.x * b.y - a.x * c.y) / 2.0D);
        return result;
    }


    /**
     * 点在凸四边形中
     * @param A
     * @param B
     * @param C
     * @param D
     * @param P
     * @return
     */
    public static boolean isPointInRect(Point A, Point B, Point C, Point D, Point P) {
        double a = (B.x - A.x)*(P.y - A.y) - (B.y - A.y)*(P.x - A.x);
        double b = (C.x - B.x)*(P.y - B.y) - (C.y - B.y)*(P.x - B.x);
        double c = (D.x - C.x)*(P.y - C.y) - (D.y - C.y)*(P.x - C.x);
        double d = (A.x - D.x)*(P.y - D.y) - (A.y - D.y)*(P.x - D.x);
        if((a >= 0 && b >= 0 && c >= 0 && d >= 0) || (a <= 0 && b <= 0 && c <= 0 && d <= 0)) {
            return true;
        }
        //      AB X AP = (b.x - a.x, b.y - a.y) x (p.x - a.x, p.y - a.y) = (b.x - a.x) * (p.y - a.y) - (b.y - a.y) * (p.x - a.x);
        //      BC X BP = (c.x - b.x, c.y - b.y) x (p.x - b.x, p.y - b.y) = (c.x - b.x) * (p.y - b.y) - (c.y - b.y) * (p.x - b.x);
        return false;
    }


    public static void main(String args[]){
        Point p1=new Point(10,0);
        Point p2=new Point(0,10);
        Point p3=new Point(14,14);
        Point p4=new Point(15,0);

        Point p5=new Point(15,1);

        boolean ret= isPointInRect(p1,p2,p3,p4,p5);
        System.out.println(ret);

    }
}
