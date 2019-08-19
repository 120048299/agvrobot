package com.wootion.commons;

public class Constans {
    public static final String CURRENT_USER = "user";
    public final static String ROBOT_TYPE = "机器人";
    public final static String ROBOT_THERMALIMAGER_TYPE = "机器人热像仪";
    public final static String SITE_TYPE = "站点";
    public final static String ROBOT_CARMERA_TYPE = "机器人摄像机";
    public final static String SESSION_ROBOT = "sessionRobot";
    public final static String SESSION_SITE = "site";
    public final static String NO_LOGIN = "no login";
    public final static String DEV_BASE_TYPE_SITE = "Site";
    public final static String DEV_BASE_TYPE_ROBOT = "Robot";
    public final static int MSG_TYPE_RESP = 10000;

    //public final static String ROS_BRIDGE_URL = "ws://192.168.100.173:9090";
    public final static String ROS_BRIDGE_URL = "ws://10.130.40.55:9090";

    public final static int TASK_PRIORITY_MAP = 10; // 紧急定位
    public final static int TASK_PRIORITY_AUTO = 8; // 定时任务
	public static int ROBOT_TIMEOUT_TIMES = 3;
	public static int TIMEOUT_SECONDS = 600;//60
	public static boolean isTraceMsg=true;
	public static final String TOKEN = "Authorization";
	public static double nearestDistance=0.1;


}
