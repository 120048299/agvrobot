-- 系统基础数据
TRUNCATE TABLE `sys_param`;
INSERT INTO `sys_param` VALUES
('1000', '充电状态发起任务最小电量', 'robot.atLeastBattery', '60', 1, '百分比，充电状态发起任务最小电量'),
('1001', '机器人最低电量', 'robot.batteryMin', '3', 1, '百分比，低于最低电量将发起充电'),
('1002', '机器人最高电量', 'robot.batteryMax', '100', 1, '百分比，高于最高电量将结束充电'),
('1003', '服务器地址', 'ros.serverIp', '192.168.100.141', 1, '服务器ip地址'),
('1004', 'WebSocket服务器地址', 'ros.webSocketSvr', 'localhost:9000', 1, ''),
('1005', 'ros接口重连间隔时间', 'ros.reconnectRosTime', '20', 1, '单位秒,默认10'),
('1006', '机器人心跳消息超时时间', 'ros.heartBeatTimeout', '5', 1, '单位秒,(研发调试时用60秒)'),
('1007', 'Web会话有效期', 'ros.sessionMaxAge', '1800', 1, '单位秒'),
('1008', '重复告警间隔', 'ros.alarmRepeatInterval', '10', 1, '单位秒'),
('1009', '告警免打扰时段', 'ros.notSendTime', '23:00-07:00', 1, '格式 HH:mm-HH:mm'),
('1010', '机器人空闲时间', 'robot.robotIdleTimeout', '30', 1, '单位分钟,默认5分钟'),
('1011', '是否有气象站', 'ros.hasWeatherStation', '0', 1, '1-是 0-否'),
('1012', '是否有充电房', 'robot.hasChargeRoom', '1', 1, '1-是 0-否'),
('1013', '是否控制充电房门', 'chargeRoom.controlDoor', '0', 1, '1-是 0-否'),
('1014', '开关充电房门超时时间', 'chargeRoom.switchDoorTimeout', '180', 1, '单位秒'),
('1015', '远程控制机器人无操作超时时间', 'ros.remoteControlIdleTimeout', '5', 1, '单位分钟，,默认5分钟'),
('1016', '任务过程中是否控制防撞开关', 'task.taskControlCrashDefence', '0', 1, '1-是 0-否'),
-- ('1018', 'Web地图1个像素代表的实际距离', 'map.pixDistance', '0.15', 1, '单位米'),
-- ('1019', 'Web地图角度', 'map.angle', '0', 1, '单位度'),
-- ('1020', 'Web地图X偏移', 'map.diffX', '0', 1, ''),
-- ('1021', 'Web地图Y偏移', 'map.diffY', '0', 1, ''),
('1022', '任务报告路线图X偏移', 'runmark.transX', '200', 1, ''),
('1023', '任务报告路线图Y偏移', 'runmark.transY', '200', 1, ''),
('1024', '原地暂停时间', 'task.pauseInPlace', '30', 1, '分钟,默认30分钟'),
('1025', '原地终止时间', 'task.stopInPlace', '30', 1, '分钟,默认30分钟'),
('1026', '任务超时机制', 'task.overTimeStyle', '1', 1, '执行任务超时之后，1继续执行,2终止执行'),
('1027', '网络掉线执行方式', 'task.offLineExecute', '1', 1, '网络掉线执行方式，1原地待命,2自动返航3继续执行'),
('1029', '任务结束后机制', 'task.finishStyle', '1', 1, '任务结束后机制，1原地待命,2自动返航'),
('1031', '雷达报警距离', 'ros.radarDisAlarm', '2', 1, '告警距离'),
('1032', '媒体保存期限', 'ros.mediaRetentionPeriod', '365', 1, '保留期限'),
('1035', '找表超时时间', 'task.findScaleTimeout', '30', 1, '秒，默认30'),
('1036', '读表超时时间', 'task.readScaleTimeout', '30', 1, '秒，默认30'),
('1037', '检测电压超时时间', 'chargeRoom.detectVoltageTimeout', '10', 1, '秒，默认10'),
('1038', '重新开始充电间隔时间', 'task.reChargeTimeSpan', '3', 1, '分钟。如果充电失败，间隔这个时间之后重试。默认3'),
('1039', '最小移动距离', 'task.minMoveDistance', '0.2', 1, 'm,默认0.2 ，小于此距离不移动'),
('1040', '是否异物识别', 'task.foreignDetect', '0', 1, '0只读表,1只识别异物,2读表和识别异物'),
('1041', '是否开启围栏功能', 'robot.enableFence', '1', 1, '0-关闭 1-开启 默认1'),
('1042', '是否开启避障功能', 'robot.enableObstacle', '1', 1, '0-关闭 1-开启 默认1'),
('1043', '移动超时', 'task.moveTimeout', '20', 1, '单位秒 默认20秒'),
('1044', '充电超时', 'task.chargeTimeout', '180', 1, '单位秒 默认180秒'),
('1045', '出库超时', 'task.outRoomTimeout', '180', 1, '单位秒 默认180秒'),
('1046', '移动重试次数', 'task.moveRetryTimes', '2', 1, '默认2次'),
('1047', '充电重试次数', 'task.chargeRetryTimes', '2', 1, '默认2次'),
('1048', '出库重试次数', 'task.outRoomRetryTimes', '2', 1, '默认2次'),
('1049', '找表重试次数', 'task.findScaleTimes', '2', 1, '默认2次'),
('1050', '找表失败后操作', 'task.findScaleFailedStyle', '0', 1, '0-继续执行 1-结束任务 默认0'),
('1051', '等待机器人实际充电超时', 'chargeRoom.waitRobotStartChargeTimeout', '15', 1, '秒，默认15');

-- 权限
TRUNCATE TABLE resource;
INSERT INTO `resource` VALUES ('taskManage','root','任务管理',0,'taskManage',NULL,1,1);
INSERT INTO `resource` VALUES ('taskList','taskManage','任务列表',0,'taskManage:taskList',NULL,1,1);
INSERT INTO `resource` VALUES ('taskList1','taskList','任务列表',0,'taskManage:taskList','/taskList',1,1);
INSERT INTO `resource` VALUES ('taskResult','taskManage','任务结果',0,'taskManage:taskResult',NULL,1,2);
INSERT INTO `resource` VALUES ('taskResult1','taskResult','任务结果',0,'taskManage:taskResult','/taskResult',1,1);
INSERT INTO `resource` VALUES ('taskReport','taskManage','任务报告',0,'taskManage:taskReport',NULL,1,2);
INSERT INTO `resource` VALUES ('taskReport1','taskReport','任务报告',0,'taskManage:taskList','/taskReport',1,1);

INSERT INTO `resource` VALUES ('statistics','root','统计分析',0,'statistics',NULL,1,2);
INSERT INTO `resource` VALUES ('reportForm','statistics','统计报表',0,'statistics:reportForm',NULL,1,1);
INSERT INTO `resource` VALUES ('reportForm1','reportForm','统计报表',0,'statistics:reportForm','/reportForm',1,1);

INSERT INTO `resource` VALUES ('userMangage','root','用户管理',0,'userMangage',NULL,1,3);
INSERT INTO `resource` VALUES ('user','userMangage','用户管理',0,'userMangage:user',NULL,1,1);
INSERT INTO `resource` VALUES ('user1','user','用户管理',0,'userMangage:user','/Department',1,1);
INSERT INTO `resource` VALUES ('role','userMangage','权限管理',0,'userMangage:role',NULL,1,1);
INSERT INTO `resource` VALUES ('role1','role','权限管理',0,'userMangage:role','/roles',1,1);

INSERT INTO `resource` VALUES ('mediaQuery','root','媒体查询',0,'mediaQuery',NULL,1,4);
INSERT INTO `resource` VALUES ('snappedQuery','mediaQuery','图片快照查询',0,'mediaQuery:snappedQuery',NULL,1,1);
INSERT INTO `resource` VALUES ('snappedQuery1','snappedQuery','图片快照查询',0,'mediaQuery:snappedQuery','/historySnapped',1,1);

INSERT INTO `resource` VALUES ('systemMaintain','root','系统管理',0,'systemMaintain',NULL,1,5);
INSERT INTO `resource` VALUES ('mapEdit','systemMaintain','地图设置',0,'systemMaintain:mapEdit',NULL,1,1);
INSERT INTO `resource` VALUES ('mapEdit1','mapEdit','地图设置',0,'systemMaintain:mapEdit','/map',1,1);
INSERT INTO `resource` VALUES ('ptzSetManage','systemMaintain','驱鸟点管理',0,'systemMaintain:ptzSetManage',NULL,1,2);
INSERT INTO `resource` VALUES ('ptzSetManage1','ptzSetManage','驱鸟点管理',0,'systemMaintain:ptzSetManage','/ptzSetManage',1,1);
INSERT INTO `resource` VALUES ('sysParam','systemMaintain','系统参数设置',0,'systemMaintain:sysParam',NULL,1,3);
INSERT INTO `resource` VALUES ('sysParam1','sysParam','系统参数设置',0,'systemMaintain:sysParam','/SystemParameterSet',1,1);
INSERT INTO `resource` VALUES ('robotMangage','systemMaintain','机器人管理',0,'systemMaintain:robotMangage',NULL,1,4);
INSERT INTO `resource` VALUES ('robotMangage1','robotMangage','机器人管理',0,'systemMaintain:robotMangage','/robotSelect',1,1);
INSERT INTO `resource` VALUES ('debug','systemMaintain','调试',0,'systemMaintain:debug',NULL,1,5);
INSERT INTO `resource` VALUES ('debug1','debug','调试',0,'systemMaintain:debug','/debug',1,1);

TRUNCATE TABLE role;
-- 这三个角色固定
INSERT INTO `role` VALUES ('super_role', '超级管理员', '超级管理员',TRUE );
INSERT INTO `role` VALUES ('manage_role', '管理员', '管理员',TRUE );
INSERT INTO `role` VALUES ('normal_role', '普通用户', '普通用户',TRUE );

TRUNCATE TABLE role_resource;
insert into role_resource  select REPLACE(UUID(),'-','') ,'super_role',uid from resource;
insert into role_resource  select REPLACE(UUID(),'-','') ,'manage_role',uid from resource where permission like 'taskManage%'
or  permission like 'statistics%' or  permission like 'mediaQuery%' or  permission like 'userMangage%';
insert into role_resource  select REPLACE(UUID(),'-','') ,'normal_role',uid from resource where permission like 'taskManage%'
or  permission like 'statistics%' or  permission like 'mediaQuery%' ;



TRUNCATE TABLE id_table;
INSERT INTO `id_table` VALUES
('1','ptz_set',0),
('2','run_mark',0),
('3','dev',0),
('4','run_line',0),
('5','regz_spot',0),
('6','task',0),
('7','ptz_set_mark',20000),
('8','job',0),
('9','snapped_picture',0),
('10','maintain_area',0),
('11','charge_room',0),
('12','area',0),
('13','bay',0);



TRUNCATE TABLE dept;
insert into `dept` VALUES ('00000001', '测试部门', '001', '000', 0);

TRUNCATE TABLE user_info;
INSERT INTO `user_info` VALUES ('superAdmin2', 'superAdmin2','00000002','super_role', '888888', '2', '2', '', '男', '', '', '', '', '', null, null, '', null, '0');
INSERT INTO `user_info` VALUES ('super1', '超级管理员1','00000001','super_role', '888888', 'super1', 'super1', '', '男', '', '', '', '', '', null, null, null, null, '0');
INSERT INTO `user_info` VALUES ('super2', '超级管理员2','00000001','super_role', '888888', 'super2', 'super2', '', '男', '', '', '', '', '', null, null, '', null, '0');
INSERT INTO `user_info` VALUES ('super3', '超级管理员3','00000001','super_role', '888888', 'super3', 'super3', '', '男', '', '', '', '', '', null, null, '', null, '0');
INSERT INTO `user_info` VALUES ('admin1', '管理员1','00000001','manage_role', '888888', 'admin1', 'admin1', '', '男', '', '', '', '', '', null, null, null, null, '0');
INSERT INTO `user_info` VALUES ('admin2', '管理员2','00000001','manage_role', '888888', 'admin2', 'admin2', '', '男', '', '', '', '', '', null, null, null, null, '0');
INSERT INTO `user_info` VALUES ('admin3', '管理员3','00000001','manage_role', '888888', 'admin3', 'admin3', '', '男', '', '', '', '', '', null, null, null, null, '0');
INSERT INTO `user_info` VALUES ('user1', '普通用户1','00000001','normal_role', '888888', 'user1', 'user1', '', '男', '', '', '', '', '', null, null, null, null, '0');
INSERT INTO `user_info` VALUES ('user2', '普通用户2','00000001','normal_role', '888888', 'user2', 'user2', '', '男', '', '', '', '', '', null, null, null, null, '0');
INSERT INTO `user_info` VALUES ('user3', '普通用户3','00000001','normal_role', '888888', 'user3', 'user3', '', '男', '', '', '', '', '', null, null, null, null, '0');


truncate table dict;
insert into dict values
('102','SYS_ALARML_TYPE','机器人离线','0002',2,null),
('103','SYS_ALARML_TYPE','摄像机离线','0003',3,null),
('104','SYS_ALARML_TYPE','热像仪离线','0004',4,null),

('107','SYS_ALARML_TYPE','电池线路异常','0101',1,null),
('108','SYS_ALARML_TYPE','电池电量过低','0102',2,null),
('109','SYS_ALARML_TYPE','电池欠压','0103',3,null),
('110','SYS_ALARML_TYPE','电池过压','0104',4,null),
('111','SYS_ALARML_TYPE','电池欠压','0105',5,null),
('112','SYS_ALARML_TYPE','电池过压','0106',6,null),
('113','SYS_ALARML_TYPE','电池低温','0107',7,null),
('114','SYS_ALARML_TYPE','电池高温','0108',8,null),
('115','SYS_ALARML_TYPE','电池BMS板温度过高','0109',9,null),
('116','SYS_ALARML_TYPE','左前电缸故障','0201',1,null),
('117','SYS_ALARML_TYPE','右前电缸故障','0202',2,null),
('118','SYS_ALARML_TYPE','右后电缸故障','0203',4,null),
('119','SYS_ALARML_TYPE','左后电缸故障','0204',3,null),
('120','SYS_ALARML_TYPE','左前电机故障','0301',1,null),
('121','SYS_ALARML_TYPE','右前电机故障','0302',2,null),
('122','SYS_ALARML_TYPE','右后电机故障','0303',4,null),
('123','SYS_ALARML_TYPE','左后电机故障','0304',3,null),
('124','SYS_ALARML_TYPE','左前电缸温度过高','0305',5,null),
('125','SYS_ALARML_TYPE','右前电缸温度过高','0306',6,null),
('126','SYS_ALARML_TYPE','右后电缸温度过高','0307',8,null),
('127','SYS_ALARML_TYPE','左后电缸温度过高','0308',7,null),
('128','SYS_ALARML_TYPE','左前方有障碍','0401',1,null),
('129','SYS_ALARML_TYPE','右前方有障碍','0402',2,null),
('130','SYS_ALARML_TYPE','右后方有障碍','0403',4,null),
('131','SYS_ALARML_TYPE','左后方有障碍','0404',3,null),
('132','SYS_ALARML_TYPE','防撞条被撞','0405',5,null),
('133','SYS_ALARML_TYPE','按下急停开关','0406',6,null),
('134','SYS_ALARML_TYPE','左侧跌落','0407',7,null),
('135','SYS_ALARML_TYPE','右侧跌落','0408',8,null),
('136','SYS_ALARML_TYPE','运动控制主板CPU温度过高','0501',1,null),
('137','SYS_ALARML_TYPE','工控机CPU温度过高','0601',1,null),
('138','SYS_ALARML_TYPE','网络信号强度过低','0701',1,null),
('139','SYS_ALARML_TYPE','行进路线偏轨','0801',1,null),
('140','SYS_ALARML_TYPE','定位失效','0802',2,null),
('141','SYS_ALARML_TYPE','障碍物','0901',1,null);

TRUNCATE TABLE `site`;
INSERT INTO `site` VALUES
('site1', '1001', '实验室', 1, 'files/map1.png', 5321, 7651, 0.01, -3.38, -39.3, 0.1, -90, null),
('site2', '1002', '大门外', 1, 'files/map2.png', 608, 1248, 0.05, -21.2, -37.2, 0.06, -90, null);

TRUNCATE TABLE `robot`;
INSERT INTO `robot` VALUES
('robot1','1001','1#机器人',1,'192.168.100.93',9090,'1.1.1.1:1','1.1.1.1:1', 0,'site1', '192.168.100.61:8081');

TRUNCATE TABLE `robot_param`;
INSERT INTO `robot_param` VALUES
('1000','robot1','机器人运行线速度', 'speed_x', '0.8', 1, 'm/s'),
('1001','robot1','云台初始位置X', 'terraceX', '0', 1, ''),
('1002','robot1','云台初始位置Y', 'terraceY', '0', 1, ''),
('1003','robot1','云台水平偏移量', 'terraceDisX', '0', 1, ''),
('1004','robot1','云台垂直偏移量', 'terraceDisY', '0', 1, ''),
('1005','robot1','控制模式', 'controlMode', '1', 1, '1：全自动控制模式 2 远程遥控模式 3 手柄模式'),
('1006','robot1','红外功能', 'infraredUsed', '1', 1, '0：不启用 1启用'),
('1007','robot1','可见光功能', 'imageUsed', '1', 1, '0：不启用 1启用'),
('1008','robot1','雨刷', 'wiperUsed', '0', 1, '0：不启用 1启用'),
('1009','robot1','避障', 'avoidanceUsed', '0', 1, '0：不启用 1启用'),
('1010','robot1','车灯功能', 'lightingUsed', '0', 1, '0：不启用 1启用'),
('1011','robot1','充电房', 'chargeRoomUsed', '0', 1, '0：不启用 1启用'),
('1012','robot1','机器人状态', 'robotStatusUsed', '1', 1, '0：不启用 1启用'),
('1013','robot1','轮子直径', 'wheelDiameter', '50', 1, 'cm'),
('1014','robot1','轮子到车中心的距离', 'disWheelAndCenter', '20', 1, 'cm'),
('1015','robot1','机器人运行角速度', 'speed_yaw', '0.8', 1, 'rad/s'),
('1016','robot1','ftp地址', 'ftpUrl', 'ros:ros@192.168.100.94', 0, '  ');
-- 两个速度

TRUNCATE TABLE `charge_room`;
INSERT INTO `charge_room` VALUES
('1','1001','1#充电房',0,'1.1.1.1:1','site1', '','-1,2,1,2,1,-0.5,-1,-0.5');

TRUNCATE TABLE `sys_alarm_config`;
INSERT INTO `sys_alarm_config` VALUES
-- 机器人 系统异常
('2','0002',1,'0002',3,null), -- 机器人离线
('3','0003',1,'0003',2,null), -- 摄像机离线
('4','0004',1,'0004',2,null), -- 热像仪离线
-- 机器人 电池异常
('7','0101',1,'0101',4,null), -- 电池线路异常
('8','0102',1,'0102',2,'X<10'), -- 电池电量过低
('9','0103',1,'0103',2,'X<20'), -- 电池欠压
('10','0104',1,'0104',2,'X>80'), -- 电池过压
('11','0105',1,'0105',2,null), -- 电池欠压
('12','0106',1,'0106',2,null), -- 电池过压
('13','0107',1,'0107',2,null), -- 电池低温
('14','0108',1,'0108',2,null), -- 电池高温
('15','0109',1,'0109',2,'X>70'), -- 电池BMS板温度过高
-- 机器人 电缸异常
('16','0201',1,'0201',3,null), -- 左前电缸故障
('17','0202',1,'0202',3,null), -- 右前电缸故障
('18','0203',1,'0203',3,null), -- 右后电缸故障
('19','0204',1,'0204',3,null), -- 左后电缸故障
-- 机器人 电机异常
('20','0301',1,'0301',3,null), -- 左前电机故障
('21','0302',1,'0302',3,null), -- 右前电机故障
('22','0303',1,'0303',3,null), -- 右后电机故障
('23','0304',1,'0304',3,null), -- 左后电机故障
('24','0305',1,'0305',2,'X>70'), -- 左前电缸温度过高
('25','0306',1,'0306',2,'X>70'), -- 右前电缸温度过高
('26','0307',1,'0307',2,'X>70'), -- 右后电缸温度过高
('27','0308',1,'0308',2,'X>70'), -- 左后电缸温度过高
-- 机器人 超声波/防跌落 检测异常
('28','0401',1,'0401',2,null), -- 左前方有障碍
('29','0402',1,'0402',2,null), -- 右前方有障碍
('30','0403',1,'0403',2,null), -- 右后方有障碍
('31','0404',1,'0404',2,null), -- 左后方有障碍
('32','0405',1,'0405',2,null), -- 防撞条被撞
('33','0406',1,'0406',1,null), -- 按下急停开关
('34','0407',1,'0407',4,null), -- 左侧跌落
('35','0408',1,'0408',4,null), -- 右侧跌落
-- 机器人 运动控制主板
('36','0501',1,'0501',2,'X>70'), -- 运动控制主板CPU温度过高
-- 机器人 工控机
('37','0601',1,'0601',2,'X>70'), -- 工控机CPU温度过高
-- 机器人 网络信号
('38','0701',1,'0701',2,'X<5'), -- 网络信号强度过低
--  机器人 导航
('39','0801',1,'0801',2,null), -- 行进路线偏轨
('40','0802',1,'0802',2,null), -- 定位失效
('41','0901',1,'0901',2,null); -- 障碍物

TRUNCATE TABLE clean_expired_task;
INSERT INTO `clean_expired_task` VALUES ('1',CURRENT_DATE ());
