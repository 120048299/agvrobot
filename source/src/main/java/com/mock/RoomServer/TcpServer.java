package com.mock.RoomServer;

import com.wootion.agvrobot.utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 模拟充电房服务
 */
class TcpServer
{
    private int port = 0;
    private ServerSocket serverSocket;
    private JFrame fm=new JFrame("mock charge room");
    private JPanel cardpan,buttonPan,containbtn;

    private JTextArea ta1,ta2;
    private ChargeRoom room =new ChargeRoom();

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
                 ServerThread serverThread=new ServerThread(socket,room);
                 //启动线程
                 serverThread.start();
                 System.out.println("New connection accepted "+
                           socket.getInetAddress()+":"+socket.getPort());
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
    }




    public void initFrame(){
        setIcon();
        //以下为初始化组件
        fm.setLayout( new GridLayout(2,1));
        buttonPan=new JPanel();
        //buttonPan.setLayout(new GridLayout(2,1));

        containbtn=new JPanel();
        containbtn.setBounds(0,0,800,50);

        buttonPan.add(containbtn);
        fm.add(buttonPan);

        cardpan=new JPanel();
        cardpan.setBounds(0,200,200,200);


        ta1=new JTextArea(30,50);
        ta1.setLineWrap(true);
        ta2=new JTextArea(30,50);
        cardpan.add(ta1);
        cardpan.add(ta2);

        JPanel pan1=new JPanel();

        //模式设置
        JButton btn=new JButton("openDoor");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                room.setIsDoorOpened(1);
                room.setIsDoorClosed(0);
            }
        });
        containbtn.add(btn);

        btn=new JButton("closeDoor");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                room.setIsDoorClosed(1);
                room.setIsDoorOpened(0);
            }
        });
        containbtn.add(btn);

        btn=new JButton("set get Voltage");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               room.setChargeRoomAbtment(1);
            }
        });
        containbtn.add(btn);

        btn=new JButton("set no Voltage");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                room.setChargeRoomAbtment(0);
            }
        });
        containbtn.add(btn);


        btn=new JButton("start charging");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                room.setIsChargeing(1);
            }
        });
        containbtn.add(btn);

        btn=new JButton("end charge");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                room.setIsChargeing(0);
            }
        });
        containbtn.add(btn);

        fm.add(cardpan);

        fm.setSize(800,500);
        fm.setLocation(100,100);
        fm.setVisible(true);
        fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private void showStatus(){
        String status= room.toString();
        ta1.setText(status);
    }


    public  void startWatch(){
        new Thread(new Runnable() {
            @Override public void run() {
                while(true) {
                    try {
                        showStatus();
                        Thread.sleep(1000);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        break;
                    }
                }
            }
        }).start();

    }


    public static void main(String[] args) throws Exception{
        TcpServer server = new TcpServer(5000);
        server.initFrame();
        Thread.sleep(1000*1);
        server.startWatch();
        server.service();

    }



    public void setIcon()
    {
        Toolkit tk=Toolkit.getDefaultToolkit();
        //String dir = System.getProperty("user.dir");

        Image image=tk.createImage(FileUtil.getBasePath()+"files/charge.jpeg");
        fm.setIconImage(image);
    }
}
