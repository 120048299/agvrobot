1. 安装mysql数据库
2. 建立ros数据库，用户密码ros/ros,赋予权限,赋予其他机器访问权限
3. 数据库脚本执行顺序
   (1)建表cityrobot.sql 和存储过程cityrobot_proc.sql
   (2)系统初始化数据sys_init_data.sql
   (3)用户初始化数据:custom_init_data.sql ;
