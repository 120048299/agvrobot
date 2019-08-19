-- Navicat MySQL Data Transfer

-- Source Server         : 10.130.44.144
-- Source Server Version : 50636
-- Source Host           : 10.130.44.144:3306
-- Source Database       : cityrobot

-- Target Server Type    : MYSQL
-- Target Server Version : 50636
-- File Encoding         : 65001

-- Date: 2017-08-02 13:54:21
-- --


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_param
-- 2018.12.20 majunhui
-- ----------------------------
DROP TABLE IF EXISTS `sys_param`;
CREATE TABLE `sys_param` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称，用于界面展示时',
  `key` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '键',
  `value` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '值',
  `editable` INT DEFAULT  1 COMMENT '是否可修改， 1-是 0-否',
  `desc` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '描述 用于界面展示',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create unique index idx_sys_param_name on sys_param(`name`);
create unique index idx_sys_param_key on sys_param(`key`);

-- ----------------------------
-- 地标点，驱鸟点
-- ----------------------------
DROP TABLE IF EXISTS `ptz_set`;
CREATE TABLE `ptz_set` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `ptz_type` INT DEFAULT  0 COMMENT 'ptz类型 0 地标点, 1 门前点, 2 充电点 ,3原点 4驱鸟点 ',
  `robot_x` float(6,4) DEFAULT 0 COMMENT '车体坐标x',
  `robot_y` float(6,4) DEFAULT 0 COMMENT '车体坐标y',
  `robot_angle` float(6,4) DEFAULT 0 COMMENT '车体角度弧度(-pi,+pi)',
  `ptz_pan` INT DEFAULT 0 COMMENT '驱鸟云台水平角度*100',
  `ptz_tilt` INT DEFAULT 0 COMMENT '驱鸟云台垂直角度*100',
  `mark_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '地图上的mark的uid',
  `description`varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '驱鸟点名称',
  `scan`  INT  DEFAULT 0 COMMENT '是否定向扫描 0 否,1 是',
  `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `area_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `status`  INT  DEFAULT 1 COMMENT '0 禁用,1 启用，2 删除',
  `setted`  INT  DEFAULT 0 COMMENT '0 参数未设置,1 参数已设置',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- ----------------------------
-- Table structure for run_mark
-- ----------------------------
DROP TABLE IF EXISTS `run_mark`;
CREATE TABLE `run_mark` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `mark_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `status` INT NOT NULL,
  `lon` float(6,3) DEFAULT NULL,  -- x
  `lat` float(6,3) DEFAULT NULL,  -- y
  `site_id` varchar(32) COLLATE  utf8_bin NOT NULL,
  `move_style` int NOT NULL DEFAULT 0, -- 前进方式: 0正向前进,1倒退前进
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- ----------------------------
-- Table structure for run_line
-- ----------------------------
DROP TABLE IF EXISTS `run_line`;
CREATE TABLE `run_line` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `site_id` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `line_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `line_id` INT NOT NULL,
  `status` INT NOT NULL,
  `mark_id1` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `mark_id2` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `max_vel`  float(6,3) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for map_icon
-- ----------------------------
DROP TABLE IF EXISTS `map_icon`;
CREATE TABLE `map_icon` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `icon_name` varchar(128) COLLATE utf8_unicode_ci  COMMENT '图标名称',
  `icon_type` INT DEFAULT NULL COMMENT '图标类型，1-障碍物，2-仪表设备',
  `lon` float(6,3) DEFAULT NULL,
  `lat` float(6,3) DEFAULT NULL,
  `icon_url` varchar(128) COLLATE utf8_unicode_ci  COMMENT '图标路径',
  `site_id` varchar(32) COLLATE utf8_bin,
  `zoom_level` INT DEFAULT NULL COMMENT ' 地图放大倍数',
  `dev_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '设备ID',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `name` varchar(32) COLLATE utf8_bin NOT NULL,
  `code` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `parent_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `username` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `deptid` varchar(32)  COLLATE utf8_bin NOT NULL,
  `roleid` varchar(32)  COLLATE utf8_bin NOT NULL,
  `workcord` varchar(16) COLLATE utf8_unicode_ci DEFAULT '',
  `password` varchar(40) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `loginname` varchar(40) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `faxno` varchar(16) COLLATE utf8_unicode_ci DEFAULT '',
  `flagsex` varchar(2) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `emailno` varchar(255) COLLATE utf8_unicode_ci DEFAULT '',
  `officialno` varchar(16) COLLATE utf8_unicode_ci DEFAULT '',
  `msisdn` varchar(16) COLLATE utf8_unicode_ci DEFAULT '',
  `position` varchar(16) COLLATE utf8_unicode_ci DEFAULT '',
  `address` VARCHAR(64) COLLATE utf8_unicode_ci,
  `modifytime` datetime DEFAULT NULL,
  `priority` INT DEFAULT NULL,
  `reserv2` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `reserv1` INT DEFAULT NULL,
  `status` INT DEFAULT 0,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create unique index idx_user_loginname on user_info(loginname);
create unique index idx_user_username on user_info(username);


DROP TABLE IF EXISTS `resource`;
create table resource (
  uid varchar(32) COLLATE utf8_bin NOT NULL,
  parent_id varchar(32) COLLATE utf8_bin ,
  name varchar(100),
  type int, -- 0 menu
  permission varchar(100),
  url varchar(200),
  available bool default false,
  order_num int, -- 排序
  constraint pk_sys_resource primary key(uid)
)  ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;
create index idx_sys_resource_parent_id on resource(parent_id);


DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `role` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `description` VARCHAR(64) NOT NULL,
  available bool default false,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `role_resource`;

CREATE TABLE `role_resource` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `role_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `resource_id` varchar(32) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
-- ALTER TABLE role_resource ADD CONSTRAINT fk_role_resource FOREIGN KEY (role_id) REFERENCES role (uid) ;

-- ----------------------------
-- Table structure for user_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `user_operate_log`;
CREATE TABLE `user_operate_log` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL,
  `user_id` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `logtime` datetime NOT NULL,
  `log` varchar(2000) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- ----------------------------
-- 设备表   顶级为站点 site_id=uid
-- ----------------------------
DROP TABLE  IF EXISTS  dev;
CREATE TABLE `dev` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `name` varchar(32) COLLATE utf8_unicode_ci   COMMENT '设备名称',
  `parent_id` varchar(32)  COLLATE utf8_bin  COMMENT '所属设备',
  `dev_type_id` varchar(32)  COLLATE utf8_bin   COMMENT '设备类型',
  `params` VARCHAR(256)  COLLATE utf8_unicode_ci   COMMENT '以逗号分开:用户设备，x,y代表设备位置中心点',
  `status` INT  DEFAULT 0 COMMENT '设备状态',
  `is_system` INT  DEFAULT 0 COMMENT '1 系统设备,0 用户设备',
  `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `order_number` INT  DEFAULT 0  COMMENT '序号',
  `code` varchar(32) COLLATE utf8_bin COMMENT '编码',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备表';

-- ----------------------------
-- 站点表
-- width height resolution origin_x origin_y 根据激光导航地图文件配置
-- ----------------------------
DROP TABLE  IF EXISTS  `site`;
CREATE TABLE `site` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `code` VARCHAR(32)  COLLATE utf8_bin NOT NULL COMMENT '站点编号',
  `name` VARCHAR(32)  COLLATE utf8_bin NOT NULL COMMENT '站点名称',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0-正常',
  `pic` VARCHAR(100) COLLATE utf8_bin  COMMENT '地图地址',
  `width` INT NOT NULL DEFAULT 640 COMMENT '地图宽',
  `height` INT NOT NULL DEFAULT 320 COMMENT '地图高',
  `resolution` float(10,6) NOT NULL DEFAULT 0.01 COMMENT '地图分辨率',
  `origin_x` float(10,6) NOT NULL DEFAULT 0 COMMENT '地图原点坐标X',
  `origin_y` float(10,6) NOT NULL DEFAULT 0 COMMENT '地图原点坐标Y',
  `scale` float(10,6) NOT NULL DEFAULT 0 COMMENT '显示比例',
  `rotation` float(10,6) NOT NULL DEFAULT 0 COMMENT '旋转角度',
  `description` varchar(256)  COLLATE utf8_bin   COMMENT '描述',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='站点表';
create unique index idx_site_code on `site`(`code`);
create unique index idx_site_name on `site`(`name`);

-- ----------------------------
-- 区域表
-- ----------------------------
DROP TABLE  IF EXISTS  area;
CREATE TABLE `area` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `name` varchar(32) COLLATE utf8_unicode_ci   COMMENT '名称',
  `params` VARCHAR(256)  COLLATE utf8_unicode_ci   COMMENT '以逗号分开:用户设备，x,y代表设备位置中心点',
  `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `order_number` INT  DEFAULT 0  COMMENT '序号',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域表';


-- ----------------------------
-- 间隔表
-- ----------------------------
DROP TABLE  IF EXISTS  bay;
CREATE TABLE `bay` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `name` varchar(32) COLLATE utf8_unicode_ci   COMMENT '名称',
  `params` VARCHAR(256)  COLLATE utf8_unicode_ci   COMMENT '以逗号分开:用户设备，x,y代表设备位置中心点',
  `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `area_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `order_number` INT  DEFAULT 0  COMMENT '序号',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='间隔';

-- ----------------------------
-- 机器人表
-- ----------------------------
DROP TABLE  IF EXISTS  `robot`;
CREATE TABLE `robot` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `code` VARCHAR(32)  COLLATE utf8_bin NOT NULL COMMENT '机器人编号',
  `name` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '机器人名称',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0：停用,1-启用 ',
  `robot_ip` VARCHAR(100)  COLLATE utf8_bin  NOT NULL COMMENT '机器人地址',
  `robot_port` INT NOT NULL DEFAULT 9090 COMMENT '端口',
  `video_addr` VARCHAR(100)  COLLATE utf8_bin  NOT NULL COMMENT '光学摄像仪地址',
  `thermal_addr` VARCHAR(100)  COLLATE utf8_bin  NOT NULL COMMENT '红外热像仪地址',
  `thermal_type` INT NOT NULL DEFAULT 0 COMMENT '0 为flir设备，1 为guide设备',
  `site_id` varchar(32)  COLLATE utf8_bin NOT NULL COMMENT '所属站点',
  `description` varchar(256)  COLLATE utf8_bin COMMENT '描述',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器人表';
create unique index idx_robot_code on `robot`(`code`);
create unique index idx_robot_name on `robot`(`name`);
create unique index idx_robot_ip on `robot`(`robot_ip`);

DROP TABLE IF EXISTS `robot_param`;
CREATE TABLE `robot_param` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `robot_id` VARCHAR(32)  COLLATE utf8_bin NOT NULL COMMENT '机器人ID',
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称，用于界面展示时',
  `key` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '键',
  `value` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '值',
  `editable` INT DEFAULT  1 COMMENT '是否可修改， 1-是 0-否',
  `desc` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '描述 用于界面展示',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create unique index idx_robot_param_name on robot_param(`robot_id`,`name`);
create unique index idx_robot_param_key on robot_param(`robot_id`,`key`);

-- ----------------------------
-- 充电房表
-- 房间位置的四个点，逗号分割，8个数字,依次为左前x,y，右前x,y，右后x,y,左后x,y 。左前，右前之间为门
-- ----------------------------
DROP TABLE  IF EXISTS  `charge_room`;
CREATE TABLE `charge_room` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `code` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '充电房编号',
  `name` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '充电房名称',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0-正常',
  `addr` VARCHAR(100)  COLLATE utf8_bin  NOT NULL COMMENT '充电房地址',
  `site_id` varchar(32)  COLLATE utf8_bin  NOT NULL COMMENT '所属站点',
  `description` varchar(256) COLLATE utf8_bin COMMENT '描述',
  `corners` varchar(256) COLLATE utf8_bin COMMENT '房间位置的四个点',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='充电房表';
create unique index idx_charge_room_code on `charge_room`(`code`);
create unique index idx_charge_room_name on `charge_room`(`name`);

-- ----------------------------
-- 气象站表
-- ----------------------------
DROP TABLE  IF EXISTS  `weather_station`;
CREATE TABLE `weather_station` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `code` VARCHAR(32)  COLLATE utf8_bin COMMENT '气象站编号',
  `name` VARCHAR(32)  COLLATE utf8_bin COMMENT '气象站名称',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0-正常',
  `addr` VARCHAR(100)  COLLATE utf8_bin COMMENT '气象站地址',
  `site_id` varchar(32)  COLLATE utf8_bin  COMMENT '所属站点',
  `description` varchar(256)  COLLATE utf8_bin COMMENT '描述',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='气象站表';
create unique index idx_weather_station_code on `weather_station`(`code`);
create unique index idx_weather_station_name on `weather_station`(`name`);

-- ----------------------------
-- 系统告警配置表
-- ----------------------------
DROP TABLE  IF EXISTS  sys_alarm_config;
CREATE TABLE `sys_alarm_config` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `alarm_code` VARCHAR(32)  COLLATE utf8_bin NOT NULL  COMMENT '告警码',
  `source_type` INT NOT NULL COMMENT '告警源类型 0-系统 1-机器人 2-充电房 3-气象站',
  `alarm_type` VARCHAR(32)  COLLATE utf8_bin NOT NULL COMMENT '告警类型',
  `alarm_level` INT  NOT NULL DEFAULT 2 COMMENT '告警级别: 1-预警 2-一般告警 3-严重告警 4-危急告警',
  `alarm_exp` VARCHAR(256)  COLLATE utf8_bin  COMMENT '告警阈值表达式',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统告警配置表';
create unique index idx_sys_alarm_config_alarm_code on `sys_alarm_config`(`alarm_code`);

-- ----------------------------
-- 系统告警日志表
-- ----------------------------
DROP TABLE  IF EXISTS  `sys_alarm_log`;
CREATE TABLE `sys_alarm_log` (
  `uid` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '主键',
  `alarm_code` VARCHAR(32)  COLLATE utf8_bin NOT NULL  COMMENT '告警码',
  `source_id` VARCHAR(32)  COLLATE utf8_bin  NOT NULL COMMENT '告警源ID',
  `alarm_time` datetime   NOT NULL  COMMENT '告警时间',
  `remove_time` datetime   COMMENT '恢复时间',
  `status` INT  DEFAULT 0 COMMENT '是否已恢复 0：未恢复 1：已恢复',
  `description` VARCHAR(256)  COLLATE utf8_bin  COMMENT '描述',
  `site_id` varchar(32)  COLLATE utf8_bin NOT NULL COMMENT '所属站点',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统告警日志表';

-- ----------------------------
--  任务表
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
 `uid` varchar(32) COLLATE utf8_bin COMMENT '主键',
 `name` varchar(64) COLLATE utf8_unicode_ci  COMMENT '任务名称',
 `user_id`      varchar(32) comment '任务创建人',
 `create_time`  datetime  COMMENT '任务创建时间',
 `edit_time`    datetime  COMMENT '任务修改时间',
 `status` INT DEFAULT 0 COMMENT '0-去激活 1-已激活 2-删除 3-完成  ;  以前的状态为：-1 未执行; 1 执行中; 4 已删除; 2 已完成',
 `description`  varchar(32) COLLATE utf8_bin COMMENT '任务描述',
 `site_id`  varchar(32) COLLATE utf8_bin NOT NULL,
 `robot_id` varchar(32) COLLATE utf8_bin NOT NULL,
 `driven_method` INT COMMENT '驱鸟手段： 1 激光 2 声波 3 激光+声波',
 `repeat` INT COMMENT '循环执行：0 不循环 ,1循环',
 `map_task` INT COMMENT '是否地图任务 0不是，1是',
 `emergency` INT COMMENT '是否紧急 0不是，1是',
 `sync_status` INT COMMENT '是否已下发 0未下发，1已下发',
 PRIMARY KEY (`uid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT'任务表';

-- ----------------------------
--  任务巡检点表
-- ----------------------------
DROP TABLE IF EXISTS `task_ptz`;
CREATE TABLE `task_ptz`(
 `uid` varchar(32) COLLATE utf8_bin  COMMENT '主键',
 `task_id`  varchar(32) COLLATE utf8_bin  COMMENT '任务id',
 `ptz_set_id`  varchar(32) COLLATE utf8_bin COMMENT '巡检点id',
 `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`uid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT'任务包含了哪些巡检点';

-- ----------------------------
-- 任务的时间周期表
-- ----------------------------

DROP TABLE IF EXISTS `task_period`;
create table task_period
(
   uid                  varchar(32) COLLATE utf8_bin not null COMMENT '主键',
   task_id  		        varchar(32) COLLATE utf8_bin  not null COMMENT '任务id',
   style                INT       comment '周期方式 0隔几天, 1每周, 2 每月, 3 指定日期  ',
   style_param   	      varchar(350)  comment 'style为 隔天，保存间隔天数数字, 0即每天; 每周 保存选择的周几：1;2;3;4;5;6;7; 每月保存选择的天，01;02;35;31;指定日期时：yyyy.mm.dd;yyyy.mm.dd;多个日期',
   start_date           date comment '周期开始时间，年月日',
   end_date             date comment '周期结束时间，年月日',
   within_aday_time     varchar(120)  comment '一天内的计划开始和结束时间,可以多个时段, 时:分-时:分;时:分-时:分;时:分-时:分;',
   site_id varchar(32)  COLLATE utf8_bin NOT NULL,
   primary key (uid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- 任务运行实例  执行完成,每个该到的点都做了；中途终止（取消删除）等；正在执行，红色；等待执行，蓝色；任务超期，黄色
-- ----------------------------
drop table if exists job;
create table job
(
   uid                  varchar(32) COLLATE utf8_bin not null comment '主键',
   task_id              varchar(32) COLLATE utf8_bin not null COMMENT '任务id',
   name                 varchar(80) comment '名称',
   create_time          datetime comment '创建时间',
   plan_start_time      datetime comment '计划开始时间',
   plan_end_time        datetime comment '计划结束时间',
   real_start_time      datetime comment '实际开始时间',
   real_end_time        datetime comment '实际结束时间',
   user_id              varchar(32) comment '下达任务用户id',
   priority             tinyint default 0 comment '优先级，数字大的优先级高 10最高',
   status               tinyint comment '0-待执行; 1-执行中; 21-手工暂停; 23-自动暂停; 3-取消; 4-结束',
   site_id              varchar(32) COLLATE utf8_bin NOT NULL,
   robot_id             varchar(32) COLLATE utf8_bin NOT NULL,
   path_image           varchar(128) COLLATE utf8_unicode_ci COMMENT '巡检路径图' ,
   end_reason           varchar(128) COLLATE utf8_unicode_ci COMMENT '结束原因' ,
   primary key (uid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- 任务实例实际行走路线 阶段
-- ----------------------------
drop table if exists job_path_section;
create table job_path_section
(
   uid           varchar(32) COLLATE utf8_bin not null comment '主键',
   job_id        varchar(32) COLLATE utf8_bin not null  ,
   section_order int  default 0 COMMENT '路径序号，从0开始',
   PRIMARY KEY (`uid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- 任务实例实际行走路线阶段的各地标点
-- ----------------------------
drop table if exists job_path_section_mark;
create table job_path_section_mark
(
   section_id    varchar(32) COLLATE utf8_bin not null  ,
   mark_id       varchar(32) COLLATE utf8_bin not null  ,
   must  int  default 0 COMMENT '是否必经点 0不是，1是',
   mark_order    int  default 0 COMMENT '本路径中位置序号，从0开始'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
create unique index idx_job_path_section_mark  on job_path_section_mark(section_id,mark_id);

-- ----------------------------
-- 2017.12.4  任务日志表 张继强
-- 2018.1.15 新增modify_result字段 张继强
-- 根据送检要求,把告警等级分为正常,预警,一般告警,严重告警,危急告警,识别异常
-- task_log对应 一个(task,job,ptz_set)只保存一条。
-- ----------------------------
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
  `uid` varchar(32) COLLATE utf8_bin  COMMENT '主键',
  `task_id` varchar(32) COLLATE utf8_bin  COMMENT '执行任务id',
  `job_id` varchar(32) COLLATE utf8_bin  COMMENT '执行任务id',
  `ptz_set_id` varchar(32) COLLATE utf8_bin  COMMENT '冗余字段巡检点id',
  `robot_id` VARCHAR(32) COLLATE utf8_bin COMMENT '执行任务机器人Id',
  `begin_time` datetime COMMENT '执行任务时间',
  `finish_time` datetime COMMENT '执行任务时间',
  `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `area_id` varchar(32) COLLATE utf8_bin NOT NULL,
  `status` INT  default 0 COMMENT '执行情况 0正常结束，-1 安全因素不执行 -2 超时 -3 取消' ,
  `result` INT  default 0 COMMENT '驱鸟结果 ，剩余多少只鸟,-1 位置，0 无鸟，>0 只数' ,
  `memo` VARCHAR(64) COLLATE utf8_unicode_ci COMMENT '备注' ,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- 机器人事件日志
-- ----------------------------
DROP TABLE IF EXISTS `event_log`;
CREATE TABLE `event_log` (
  `uid` VARCHAR(32) COLLATE utf8_bin  COMMENT '主键',
  `site_id` VARCHAR(32) NOT NULL COLLATE utf8_bin NOT NULL,
  `robot_id` VARCHAR(32) NOT NULL COLLATE utf8_bin ,
  `event_type` VARCHAR(32) NOT NULL COMMENT '事件类型 ',
  `event_level` INT NOT NULL COMMENT '事件级别 ',
  `event_time` datetime COMMENT '事件时间',
  `event_desc`  varchar(128) COLLATE utf8_unicode_ci comment '事件说明',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- 抓图图片 存于picture之下，按日期存放
-- ----------------------------
DROP TABLE IF EXISTS `snapped_picture`;
CREATE TABLE `snapped_picture` (
  `uid` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `file_name` varchar(64) NOT NULL COMMENT '文件名目录与名字，',
  `memo` varchar(256) NULL COMMENT '备注',
  `is_infra` INT DEFAULT NULL COMMENT '0 可见光，1 红外)',
  `create_time` datetime DEFAULT NULL COMMENT '抓图时间',
   `site_id` varchar(32) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- 字典类型表
-- ----------------------------
drop table if exists dict_type;
create table dict_type
(
   uid         varchar(32) COLLATE utf8_bin  not null comment '主键',
   dict_name   varchar(32)  COLLATE utf8_unicode_ci not null comment '字典名称',
   dict_code   varchar(32)  COLLATE utf8_bin not null comment '字典编码',
   order_number   int null comment '序号',
   memo        varchar(32)  COLLATE utf8_unicode_ci  null comment '备注',
   primary key (uid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- 字典值表
-- ----------------------------
drop table if exists dict;
create table dict
(
   uid          varchar(32) COLLATE utf8_bin  not null comment '主键',
   dict_code   varchar(32) COLLATE utf8_bin  not null comment '字典编码',
   name         varchar(32)  COLLATE utf8_unicode_ci not null comment '名称',
   value        varchar(32) COLLATE utf8_bin  null comment '值',
   order_number   int null comment '序号',
   memo        varchar(32)  COLLATE utf8_unicode_ci null comment '备注',
   primary key (uid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- 检修区域表: 长方形
-- ----------------------------
drop table if exists maintain_area;
create table maintain_area
(
   uid      varchar(32) COLLATE utf8_bin  not null comment '主键',
   name     varchar(32)  COLLATE utf8_unicode_ci not null comment '名称',
   modify_time datetime not null comment '设置时间',
   point1  varchar(256) not null,
   point2  varchar(256) not null,
   point3  varchar(256) not null,
   point4  varchar(256) not null,
   point5  varchar(256) not null,
   maintain_type INT DEFAULT NULL COMMENT '类型(0-避障功能、1-检修区域)',
   memo     varchar(32)  COLLATE utf8_unicode_ci null comment '备注',
   site_id varchar(32) COLLATE utf8_bin NOT NULL,
   primary key (uid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


DROP TABLE IF EXISTS `clean_expired_task`;
CREATE TABLE `clean_expired_task`(
 uid      varchar(32) COLLATE utf8_bin  not null comment '主键',
 plan_clean_tasktime datetime comment '计划结束时间',
 PRIMARY KEY (`uid`)
);

-- uid 表
DROP TABLE IF EXISTS `id_table`;
CREATE TABLE `id_table`(
  `uid`      varchar(32) COLLATE utf8_bin  not null comment '主键',
  `table_name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '表名称',
  `current_num` INT NOT NULL  DEFAULT  0 COMMENT '当前num',
 PRIMARY KEY (`uid`)
);
create unique index idx_id_table on id_table(table_name);

-- 系统日志表
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `uid` VARCHAR(32) COLLATE utf8_bin  COMMENT '主键',
  `site_id` VARCHAR(32) NOT NULL COLLATE utf8_bin NOT NULL,
  `robot_id` VARCHAR(32) NOT NULL COLLATE utf8_bin COMMENT '执行任务机器人Id',
  `event` INT NOT NULL COMMENT '事件类型 1 充电  ',
  `result` INT  NOT NULL   COMMENT '成功与否，接口回应等',
  `log_time` datetime  NOT NULL COMMENT '执行任务时间',
  `desc` varchar(32) COLLATE utf8_bin  NULL COMMENT '事件',
  `memo` varchar(100) COLLATE utf8_bin  NULL COMMENT '说明',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

