package com.mock.weatherServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//创建服务端
class TcpServer
{
    private int port = 0;
   private ServerSocket serverSocket;


    public TcpServer(int port) throws Exception{
            this.port=port;
           serverSocket = new ServerSocket(port,3);
           System.out.println("服务器启动!");
    }

    public void service(){
           while(true){
                Socket socket = null;
                try {
                     socket = serverSocket.accept();
                     ServerThread serverThread=new ServerThread(socket);
                                     //启动线程
                     serverThread.start();
                     System.out.println("New connection accepted "+
                               socket.getInetAddress()+":"+socket.getPort());
                    } catch (IOException e) {
                     e.printStackTrace();
                    }
               }
          }

          public static void main(String[] args) throws Exception{
           TcpServer server = new TcpServer(20108);
           Thread.sleep(1000*1);
           server.service();
          }
 }