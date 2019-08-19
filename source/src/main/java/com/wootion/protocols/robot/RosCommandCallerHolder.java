package com.wootion.protocols.robot;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RosCommandCallerHolder {

    private static final long serialVersionUID = -8224792866430647454L;
    private static RosCommandCallerHolder rosCommandCallerHolder = new RosCommandCallerHolder();//单例
    private boolean flag = false;
    private RosCommandCallerHolder(){}
    public static RosCommandCallerHolder getInstance(){
        return rosCommandCallerHolder;
    }
    private static  ConcurrentHashMap<Integer,RosCommandCaller>  taskMap = new ConcurrentHashMap<Integer, RosCommandCaller>();

    private static ExecutorService service= Executors.newFixedThreadPool(10);



    public Future<Object> addStep(Integer transId,RosCommandCaller rosCommandCaller){
        taskMap.putIfAbsent(transId, rosCommandCaller);
        Future<Object> future=service.submit(rosCommandCaller);
        return  future;
    }
    public synchronized RosCommandCaller getStep(Integer transId){
        return taskMap.get(transId);
    }
    public synchronized void removeStep(Integer transId){
        taskMap.remove(transId);
    }



}