package com.mock.RoomServer;


import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

/*
 * 服务器线程处理类
 * 使用方法：自动模拟相应 或者手动点击模拟
 */
public class ServerThread extends Thread {
    // 和本线程相关的Socket
    public static int isManualSwith=0; //0 自动模拟 ，1 手动按钮切换模拟 开关门  充电 结束等
    Socket socket = null;
    ChargeRoom room =null;
    boolean running=true;
    public ServerThread(Socket socket, ChargeRoom room) {
        this.socket = socket;
        this.room = room;
    }

    //线程执行的操作，响应客户端的请求
    public void run(){
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;
        PrintWriter pw=null;
        try {
            //获取输入流，并读取客户端信息
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            os=socket.getOutputStream();//字节输出流
            while(running){
                byte [] receiveMsg=receiveData(is);
                if(receiveMsg==null){
                    continue;
                }
                System.out.println("get:"+ NumberUtil.bytesToHexString(receiveMsg));
                ByteBuffer buffer = ByteBuffer.wrap(receiveMsg);
                byte equpAddr = buffer.get();
                byte funcCode = buffer.get();
                if(funcCode == 3){//read
                    //String status="02 03 1A 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 4C 70";
                    //status=status.replace(" ","");
                    //byte [] sendMsg= NumberUtil.hexStringToBytes(status);
                    byte [] sendMsg = roomToByte();
                    os.write(sendMsg);
                    System.out.println(NumberUtil.bytesToHexString(sendMsg));
                }else{ //6 write
                    String status="0206000200000000";
                    short dataAddr = buffer.getShort();
                    short data = buffer.getShort();
                    if(isManualSwith==0){
                        //以下模拟自动进行
                        if(dataAddr==6) {//open door
                            toOpenDoor(room);
                        }else if(dataAddr==7) {//close door
                            toCloseDoor(room);
                        }else if(dataAddr==3) {//start charge
                            toStartCharge(room);
                        }else if(dataAddr==5) {//stop charge
                            toStopCharge(room);
                        }else if(dataAddr==4) {//start charge
                            room.setChargeRoomAbtment(0);
                        }
                    }
                    os.write(NumberUtil.hexStringToBytes(status));
               }
                os.flush();
            }

            socket.shutdownInput();//关闭输入流

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //关闭资源
            try {
                if(pw!=null)
                    pw.close();
                if(os!=null)
                    os.close();
                if(br!=null)
                    br.close();
                if(isr!=null)
                    isr.close();
                if(is!=null)
                    is.close();
                if(socket!=null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] roomToByte(){
        ByteBuffer buf = ByteBuffer.allocate(31);
        buf.put((byte)2);
        buf.put((byte)3);
        buf.put((byte)26);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) 0);
        buf.putShort((short) room.getIsChargeing());
        buf.putShort((short) room.getIsDoorOpened());
        buf.putShort((short) room.getIsDoorClosed());
        buf.putShort((short) 0);
        buf.putShort((short) room.getChargeRoomAbtment());
        buf.putShort((short) 0);//crc
        return buf.array();
    }

    /**
     * 接收服务器端反馈
     * @return 反馈数据
     *将流中的数据转化成字节型，可参考
     */
    private static byte[] receiveData(InputStream is){
        byte[] b = new byte[1024];
        try {
            int n = is.read(b);
            byte[] data = new byte[n];
            //复制有效数据
            System.arraycopy(b, 0, data, 0, n);
            System.out.println(NumberUtil.bytesToHexString(data));
            return data;
        } catch (Exception e){}
        return null;
    }


    void toOpenDoor(ChargeRoom room){
        try{
            room.setIsDoorOpened(0);
            room.setIsDoorClosed(0);
            Runnable task= new Runnable() {
                @Override
                public void run() {
                    try{
                        for (int i=0;i<20;i++){
                            System.out.println("door is opening ....");
                            Thread.sleep(1000);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    room.setIsDoorOpened(1);
                    room.setIsDoorClosed(0);
                    System.out.println("door is opened");
                }
            };
            new Thread(task).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void toCloseDoor(ChargeRoom room){
        try{
            room.setIsDoorOpened(0);
            room.setIsDoorClosed(0);
            Runnable task= new Runnable() {
                @Override
                public void run() {
                    try{
                        for (int i=0;i<20;i++){
                            System.out.println("door is closing....");
                            Thread.sleep(1000);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    room.setIsDoorOpened(0);
                    room.setIsDoorClosed(1);
                    System.out.println("door is closed");
                }
            };
            new Thread(task).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void toStartCharge(ChargeRoom room){
        try{

            Runnable task= new Runnable() {
                @Override
                public void run() {
                    try{
                        for (int i=0;i<10;i++){
                            System.out.println("start sharge ....");
                            Thread.sleep(1000);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    room.setIsChargeing(1);
                    System.out.println("charging....");
                }
            };
            new Thread(task).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void toStopCharge(ChargeRoom room){
        try{

            Runnable task= new Runnable() {
                @Override
                public void run() {
                    try{
                        for (int i=0;i<10;i++){
                            System.out.println("stop sharge ....");
                            Thread.sleep(1000);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    room.setIsChargeing(0);
                    System.out.println("not charging");
                }
            };
            new Thread(task).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}