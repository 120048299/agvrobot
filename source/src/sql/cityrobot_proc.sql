
DELIMITER //
Drop PROCEDURE IF EXISTS deal_expired_task//
CREATE PROCEDURE deal_expired_task( )
BEGIN
  DECLARE timeflag int;
 set timeflag = (SELECT count(1) from clean_expired_task where plan_clean_tasktime < NOW());
 if timeflag > 0 then
    update task_plan a, clean_expired_task b set a.status=7 where a.plan_end_time < b.plan_clean_tasktime and a.`status` in(0,1,5,6);
    update task_plan a, clean_expired_task b , task_exec c set c.status=7 where a.plan_end_time < b.plan_clean_tasktime and c.`status` in(0,1,5,6) and a.uid=c.task_plan_id;
    update clean_expired_task SET plan_clean_tasktime = DATE_ADD(plan_clean_tasktime,INTERVAL 1 DAY);
end if;
end //

drop FUNCTION  IF EXISTS `getTableId`//
CREATE FUNCTION `getTableId`(tableName varchar(32)) RETURNS varchar(6)
BEGIN
	DECLARE currentNum INT default 0;
	set currentNum = (SELECT current_num+1 from id_table where table_name=tableName);
    update id_table set current_num = currentNum where table_name=tableName;
    return lpad(currentNum,6,'0');
END //