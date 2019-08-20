package com.wootion.vo;

import com.wootion.model.PtzSet;
import com.wootion.model.Task;
import com.wootion.model.TaskPeriod;

import java.util.List;

public class TaskDetailInfo {
    private Task task;
    private List<PtzSet> list;
    private TaskPeriod taskPeriod;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<PtzSet> getList() {
        return list;
    }

    public void setList(List<PtzSet> list) {
        this.list = list;
    }

    public TaskPeriod getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(TaskPeriod taskPeriod) {
        this.taskPeriod = taskPeriod;
    }
}
