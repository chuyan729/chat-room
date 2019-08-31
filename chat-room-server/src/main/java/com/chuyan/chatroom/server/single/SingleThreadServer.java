package com.chuyan.chatroom.server.single;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室服务端程序
 * Author： chuyan
 * Date：2019/8/5
 */
public class SingleThreadServer {

    public static void main(String[] args) {

        try {

            //0.通过命令行获取服务器端口
            int port = 6666;
            if(args.length>0){
                try{
                    port = Integer.parseInt ( args[0] );
                }catch (NumberFormatException e){
                    System.out.println ("端口参数不正确，采用默认端口："+port);
                }

            }

            //1.创建ServerScoket
            ServerSocket serverSocket = new ServerSocket ( port );
            //服务器监听的端口号是哪里
            System.out.println ("服务器启动："+serverSocket.getLocalSocketAddress ());

            //2.等待客户端连接
            System.out.println ("等待客户端连接");
            Socket clientSocket = serverSocket.accept ();

            //3.接收和发送数据
            InputStream clientInput = clientSocket.getInputStream ();
            Scanner sc = new Scanner ( clientInput );
            String clientData = sc.nextLine ();
            System.out.println ("来自客户端的消息："+clientData);

            OutputStream clientOutput = clientSocket.getOutputStream ();
            OutputStreamWriter writer = new OutputStreamWriter ( clientOutput );
            writer.write ( "欢迎连接服务器\n" );
            writer.flush ();
//            OutputStream clientOutput = clientSocket.getOutputStream ();
//            PrintStream writer = new PrintStream ( clientOutput );
//            writer.println ( "欢迎连接服务器" );

        } catch (IOException e) {
            e.printStackTrace ();
        }

    }
}
