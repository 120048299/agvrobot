package com.wootion.utiles;

import com.wootion.commons.Constans;
import com.wootion.commons.Result;
import com.wootion.model.RunMark;
import com.wootion.protocols.robot.msg.RobotInfo;
import com.wootion.robot.MemRobot;
import io.netty.util.internal.MathUtil;

import java.util.List;

public class RunMarkUtil {

    public static RunMark findTooCloseRunMark(double x, double y, String siteId) {
        return findTooCloseRunMark(x, y, siteId, "");
    }

    /**
     * 是否存在小于最小距离的点位。避免点位之间距离太小
     * @param x
     * @param y
     * @return
     */
    public static RunMark findTooCloseRunMark(double x,double y,String siteId, String uid) {
        List<RunMark> runMarkList = DataCache.getRunMarkList(siteId);
        if(runMarkList==null){
            return null;
        }
        for (RunMark exitRunMark : runMarkList) {
            double distance = RunMarkUtil.distance(exitRunMark.getLon(),exitRunMark.getLat(),x,y);
            if (distance < Constans.nearestDistance && !exitRunMark.getUid().equals(uid)) {
                return exitRunMark;
            }
        }
        return  null;
    }


    // 计算两点之间的距离
    public static double distance(double x1, double y1, double x2, double y2) {
        double lineLength = 0;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)* (y1 - y2));
        return lineLength;
    }


    /**
     * 得到距离最近的点位
     * @param position
     * @return
     */
    public static RunMark getNearestRunMark(String siteId, Float[] position) {
        List<RunMark> runMarkList= DataCache.getFiltedRunMarkList(siteId);
        if(runMarkList==null){
            return null;
        }

        RunMark runMark = new RunMark();
        double minLenth = 99999;

        if (position == null) {
            runMark.setLon(0d);
            runMark.setLat(0d);
            runMark.setUid("0");

            return runMark;
        }
        for (RunMark pt:runMarkList) {
            double curLen = RunMarkUtil.distance(position[0], position[1],pt.getLon(), pt.getLat());
            if (curLen < minLenth) {
                minLenth = curLen;
                runMark.setLon(pt.getLon());
                runMark.setLat(pt.getLat());
                runMark.setUid(pt.getUid());
                runMark.setSiteId(pt.getSiteId());
                runMark.setMarkName(pt.getMarkName());
                runMark.setStatus(pt.getStatus());
            }
        }
        return runMark;
    }
}
