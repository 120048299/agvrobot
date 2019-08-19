package com.wootion.task.event;

import com.wootion.model.TaskLog;
import com.wootion.robot.MemRobot;
import com.wootion.task.exec.MemTask;
import lombok.Data;

/**
 * 告警计算事件：业务执行结束 发起告警计算
 *
 */
@Data
public class AlarmCalcEvent {
    private TaskLog taskLog;
    public AlarmCalcEvent() {
    }

    public AlarmCalcEvent(TaskLog taskLog) {
        this.taskLog = taskLog;
    }

}
