package com.wootion.protocols.robot;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RosActionCallerHolder {

    private static RosActionCallerHolder rosActionCallerHolder = new RosActionCallerHolder();//单例
    private boolean flag = false;
    private RosActionCallerHolder(){}
    public static RosActionCallerHolder getInstance(){
        return rosActionCallerHolder;
    }
    private static  ConcurrentHashMap<Integer,RosActionCaller>  taskMap = new ConcurrentHashMap<Integer, RosActionCaller>();

    private static ExecutorService service= Executors.newFixedThreadPool(10);



    public Future<Object> addStep(Integer transId,RosActionCaller rosActionCaller){
        taskMap.putIfAbsent(transId, rosActionCaller);
        Future<Object> future=service.submit(rosActionCaller);
        return  future;
    }
    public synchronized RosActionCaller getStep(Integer transId){
        return taskMap.get(transId);
    }
    public synchronized void removeStep(Integer transId){
        taskMap.remove(transId);
    }



}