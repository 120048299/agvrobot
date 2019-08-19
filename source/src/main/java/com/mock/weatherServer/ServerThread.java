package com.mock.weatherServer;


import java.io.*;
import java.net.Socket;

/*
 * 服务器线程处理类
 */
public class ServerThread extends Thread {
    // 和本线程相关的Socket
    Socket socket = null;
    boolean running=true;
    public ServerThread(Socket socket) {
        this.socket = socket;
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
                if(receiveMsg!=null){
                    System.out.println("get:"+ NumberUtil.bytesToHexString(receiveMsg));
                }
                String status="03 03 00 20 00 00 00 D3 02 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 4C 70";
                status=status.replace(" ","");
                byte [] sendMsg= NumberUtil.hexStringToBytes(status);
                os.write(sendMsg);
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
}