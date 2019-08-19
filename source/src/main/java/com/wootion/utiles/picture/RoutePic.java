package com.wootion.utiles.picture;

import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.model.MapIcon;
import com.wootion.model.RunMark;
import com.wootion.service.IMapService;
import com.wootion.task.TaskManage;
import com.wootion.task.map2.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成任务巡检时走过的线路
 * 显示背景地图
 * 显示所有设备
 * 显示runMark位置，连线箭头
 */
public class RoutePic {
    private static final Logger logger = LoggerFactory.getLogger(RoutePic.class);


    private String mapSourceFile; //站点地图文件 ，作为背景

    private double mapPixelDistance=1; //地图文件每个像素代表的实际距离
    private double pixelDistance=1; //地图文件每个像素代表的实际距离

    private String outFile; //生成文件名称
    private int width=1400;//生成文件宽度
    private int height=900;//生成文件高度 ,按地图的宽度放大

    private double xDiff=0;
    private double yDiff=0;
    private int direction=0;

    //added by btrmg for paint icon 2018.11.14
  //  private List<MapIcon> m_mapIcon;
    private Map<String,Icon> iconMap;  //巡检到的设备
    private List<RunMark> runMarkList; //机器人巡检走过的路线

    //added by btrmg for paint MaintainArea 2018.11.12
   // private List<Map> m_maintainArea;
    private List<IndiscriminateArea> m_allArea;


    public RoutePic(){

    }


    public RoutePic(String mapSourceFile,String outFile){
        this.mapSourceFile = mapSourceFile;
        this.outFile=outFile;

    }

    /**
     * 设置坐标原点偏移量：
     * @param x
     * @param y
     */
    public void setZeroXY(int x,int y){
        xDiff=x;
        yDiff=y;
    }


    /**
     * 把距离坐标转换未像素坐标
     *
     * @param xy
     * @return
     */

    private int transXY(double xy){
        return (int) (xy/pixelDistance);
    }


    public Map<String, Icon> getIconMap() {
        return iconMap;
    }

    public void setIconMap(Map<String, Icon> iconMap) {
        this.iconMap = iconMap;
    }
  //  public  void SetMaintainArea( List<Map> imaintianArea){ this.m_maintainArea = imaintianArea;}

    public List<RunMark> getRunMarkList() {
        return runMarkList;
    }

  //  public List<MapIcon> GetAllMapIcon( ){
  //      return m_mapIcon;
  //  }

  //  public void SetAllMapIcon( List<MapIcon> m_icon){
  //      this.m_mapIcon = m_icon;
  //  }

    public  void setAllArea(List<IndiscriminateArea> m_Area){
        this.m_allArea = m_Area;
    }

    public void setRunMarkList(List<RunMark> runMarkList) {
        this.runMarkList = runMarkList;
    }

    public double getPixelDistance() {
        return pixelDistance;
    }

    public void setPixelDistance(double pixelDistance) {
        this.pixelDistance = pixelDistance;
    }


    /**
     * 生成文件
     * @return
     */
    public boolean generatePic(Double scale){
        try{
            //把地图作为背景,地图的像素长宽作为生成的图形长宽
            BufferedImage background = ImageIO.read(new File(mapSourceFile));
            int width = background.getWidth();
            int height = background.getHeight();
            logger.debug("map resource:width="+width + ",height=" + height);
            //this.width=width;
            //this.height =height;
            double multi   = this.width*1.0/width;
            this.height = (int)(height*multi);
            this.pixelDistance = mapPixelDistance/multi;

            BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR);
            final File file = new File(outFile);
            try {
                if(file.exists()) {
                    file.delete();
                    file.createNewFile();
                }
            }catch(IOException e) {
                e.printStackTrace();
                logger.error(e.toString());
            }

            Graphics g = bi.getGraphics();
            Graphics2D g2d=(Graphics2D)g;
            g2d.drawImage(background.getScaledInstance(this.width, this.height,Image.SCALE_DEFAULT),0,0,null);
            //displayMatrix(g2d.getTransform());

            /*Line2D line= new Line2D.Double(0,0,100,200);
            g2d.draw(line);
*/
            //平移Y高度
            AffineTransform at1 = new AffineTransform();
            at1.setToTranslation(xDiff, this.height-yDiff); //翻转y和平移坐标原点x,y
            g2d.transform(at1);
            //displayMatrix(g2d.getTransform());

            //  g2d.drawString("(0,0)",0,0);
           //   paintDevs(g2d,iconMap);
            paintAllArea(g2d, scale);
            paintRunMark(g2d,runMarkList, scale);
            //added by btrmg for maintainArea 2018.11.12
            // paintMaintainArea(g2d,m_maintainArea);

            g.dispose();
            boolean val = false;
            try {
                val = ImageIO.write(bi, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("绘制路线图失败 "+outFile+" "+e.toString());
            }
            logger.info("绘制路线图 "+outFile);
            return val;

        }catch (Exception e){
            e.printStackTrace();
            logger.error("绘制路线图失败 "+outFile+" "+e.toString());
            return false;
        }

    }

    private  int paintDevs(Graphics2D g2d,Map<String,Icon> iconMap){
        Color co=new Color(255,0,0);
        g2d.setColor(co);
        Color c=g2d.getColor();

        try{
            for (Icon icon:iconMap.values()) {
                BufferedImage image = ImageIO.read(new File(FileUtil.getBasePath()+"jar/maps/"+icon.fileName));

               g2d.drawString(icon.text,(int)icon.x,-(int)icon.y);  //写文字时用Y轴向下，文字方向向上，传入负数坐标
               g2d.drawImage(image.getScaledInstance(icon.w,icon.h,Image.SCALE_DEFAULT),(int)icon.x,-(int)icon.y,null);
                //  g2d.transform(at3);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private  int paintRunMark(Graphics2D g2d,List<RunMark> list, Double scale){
        Color co=new Color(255,0,0);
        g2d.setColor(co);
        Color c=g2d.getColor();
        Stroke bs = new BasicStroke(2.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);

        g2d.setStroke(bs);

        RunMark firstRunMark=null;
        try{

            for (RunMark runMark : list) {

                AffineTransform at2 = new AffineTransform();
                at2.scale(1, -1);
                g2d.transform(at2);

                logger.debug("the name of runMark is: " + runMark.getMarkName());

                if(firstRunMark==null){
                    firstRunMark=runMark;
                }else{
                    //画箭头线段
                    double[] pt1 = Coordinate.nav2Web(firstRunMark.getLon(), firstRunMark.getLat(),scale);
                    double[] pt2 = Coordinate.nav2Web(runMark.getLon(),runMark.getLat(),scale);
                    ImageUtil.drawAL((int)pt1[0],(int)pt1[1],(int)pt2[0],(int)pt2[1],g2d);
                    logger.debug("the line is from :(" + pt1[0]+ ","+ pt1[1]+" ) to ("+ pt2[0]+","+pt2[1]+")");
                    //added by btrmg for paint 2018.11.05
                    firstRunMark = runMark;
                    //added end
                }
               AffineTransform at3 = new AffineTransform();
               at3.scale(1, -1);
               g2d.transform(at3);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

   /* private int paintMaintainArea( Graphics2D g2d, List<Map> m_maintainArea) {
        Color co = new Color(0, 0, 255);
        g2d.setColor(co);
        Stroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0,new   float[]{16,   4},0);
        g2d.setStroke(bs);

        if (m_maintainArea != null && m_maintainArea.size() > 0) {
            try{
                  for (Map map :m_maintainArea) {
                    String p1 = (String) map.get("point1");
                    String[] temp1 = p1.split(",");
                    int[] dp1 = Coordinate.transXY2Pix((int)Double.parseDouble(temp1[0]),(int)Double.parseDouble(temp1[1]));
                    String p2 = (String) map.get("point2");
                    String[] temp2 = p2.split(",");
                    int[] dp2 = Coordinate.transXY2Pix((int)Double.parseDouble(temp2[0]), (int)Double.parseDouble(temp2[1]));
                    String p3 = (String) map.get("point3");
                    String[] temp3 = p3.split(",");
                    int[] dp3 = Coordinate.transXY2Pix((int)Double.parseDouble(temp3[0]),(int) Double.parseDouble(temp3[1]));
                    String p4 = (String) map.get("point4");
                    String[] temp4 = p4.split(",");
                    int[] dp4 = Coordinate.transXY2Pix((int)Double.parseDouble(temp4[0]),(int) Double.parseDouble(temp4[1]));
                    String p5 = (String) map.get("point5");
                    String[] temp5 = p5.split(",");
                    int[] dp5 = Coordinate.transXY2Pix((int)Double.parseDouble(temp5[0]),(int) Double.parseDouble(temp5[1]));
                    logger.debug("the four point is: ("+dp1[0]+","+dp1[1]+"),("+dp2[0]+","+dp2[1]+"),("+dp3[0]+","+dp3[1]+"),("+dp4[0]+","+dp4[1]+")");

                    g2d.drawLine( dp1[0],  dp1[1], dp2[0], dp2[1]);
                    g2d.drawLine( dp2[0],  dp2[1], dp3[0], dp3[1]);
                    g2d.drawLine( dp3[0],  dp3[1], dp4[0], dp4[1]);
                    g2d.drawLine( dp4[0],  dp4[1], dp5[0], dp5[1]);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }
*/
    private int paintAllArea( Graphics2D g2d, Double scale) {
        Color co = new Color(0, 0, 255);
        g2d.setColor(co);
        Stroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0,new   float[]{16,   4},0);
        g2d.setStroke(bs);

        if (m_allArea != null && m_allArea.size() > 0) {
            try{
                for (IndiscriminateArea mArea :m_allArea) {
                    mArea.paintSingleArea(g2d, scale);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }

    //added by btrmg for draw all draw
  void displayMatrix(AffineTransform theTransform){
        double[] theMatrix = new double[6];
        theTransform.getMatrix(theMatrix);

        //Display first row of values by displaying every
        // other element in the array starting with element
        // zero.
        for(int cnt = 0; cnt < 6; cnt+=2){
            System.out.print(theMatrix[cnt] + " ");
        }//end for loop

        //Display second row of values displaying every
        // other element in the array starting with element
        // number one.
        System.out.println();//new line
        for(int cnt = 1; cnt < 6; cnt+=2){
            System.out.print(theMatrix[cnt] + " ");
        }//end for loop
        System.out.println();//end of line
        System.out.println();//blank line

    }//end displayMatrix

    public  void testpaintpolygons( )
    {
        try {
            BufferedImage background = ImageIO.read(new File(mapSourceFile));
            int width = background.getWidth();
            int height = background.getHeight();
             logger.debug("map resource:width="+width + ",height=" + height);
        //this.width=width;
        //this.height =height;
          double multi   = this.width*1.0/width;
          this.height = (int)(height*multi);
           this.pixelDistance = mapPixelDistance/multi;

           BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR);
           final File file = new File(outFile);
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }

        Graphics g = bi.getGraphics();
        Graphics2D g2d=(Graphics2D)g;
        g2d.drawImage(background.getScaledInstance(this.width, this.height,Image.SCALE_DEFAULT),0,0,null);


        Stroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,2,new   float[]{16,4},0);
        g2d.setStroke(bs);
        int[] pointx ={200,200,500,500,200};
        int[] pointy ={50,200,200,50,50};
        g2d.setColor(Color.RED);
        g2d.drawPolygon(pointx,pointy,4);
        g2d.setColor(Color.GRAY);
       // g2d.fillPolygon(pointx,pointy,4);
        g2d.drawString("testdraw",pointx[0],pointy[0]);
        Stroke bs1 = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2d.setStroke(bs1);
        g2d.drawLine(pointx[0],pointy[0],pointx[2],pointy[2]);
        g.dispose();
        ImageIO.write(bi, "jpg", file);
        }catch(IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }

    }
    public static void main(String[] args) {
        String mapFileName = "map.png";
        String outFileName = "taskRoute.jpg";

        RoutePic routePic=new RoutePic(FileUtil.getBasePath()+"jar/maps/"+mapFileName,FileUtil.getBasePath()+"/report/"+outFileName);
       //added by btrmg for debug 2018.11.05
       // RoutePic routePic=new RoutePic(FileUtil.getBasePath()+"map2.png",FileUtil.getBasePath()+outFileName);
        routePic.setZeroXY(10,10);
        routePic.setPixelDistance(1);
        routePic.setZeroXY(600,200);

        Map<String,Icon> iconMap=new HashMap<>();
        for (int i=0;i<5;i++){
            Icon icon=new Icon(String.valueOf(i),"meter"+i+".png","图像"+i,50,50,150*i,100*i);
            iconMap.put(icon.id,icon);
        }
        routePic.setIconMap(iconMap);

        List<RunMark> runMarkList=new ArrayList<>();
        for (int i=0;i<10;i++){
            RunMark runMark=new RunMark();
            runMark.setLat(i*10.0);
            runMark.setLon(i*10.0);
            runMarkList.add(runMark);
        }
        routePic.setRunMarkList(runMarkList);

       // boolean result = routePic.generatePic();

        routePic.testpaintpolygons();


    }


    }
