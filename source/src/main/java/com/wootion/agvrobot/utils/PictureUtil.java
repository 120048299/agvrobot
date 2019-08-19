package com.wootion.agvrobot.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

public class PictureUtil {

    /*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    public static int zoomImage(String src,String dest,int w,int h) {
        try {
            double wr=0,hr=0;
            File srcFile = new File(src);
            File destFile = new File(dest);

            BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
            Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

            wr=w*1.0/bufImg.getWidth();     //获取缩放比例
            hr=h*1.0 / bufImg.getHeight();

            AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
            Itemp = ato.filter(bufImg, null);

            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    /*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    public static BufferedImage zoomImage(String src,int w,int h) throws Exception {
        double wr=0,hr=0;
        File srcFile = new File(src);

        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        Image tempImage = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        tempImage = ato.filter(bufImg, null);
        tempImage.flush();
        return (BufferedImage)tempImage;
    }

    public static void main(String args[]){
        try {


            zoomImage("/home/zhoufei/bak_lj/preset_1/fc729d0cb8ae48688918994c9b570356/20181210_164103/zoom2.jpg", "/home/zhoufei/temp/1.jpg", 960, 540);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
