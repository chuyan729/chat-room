package com.chuyan.chatroom.server.multi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author： chuyan
 * Date：2019/8/6
 */
public class MultiThreadServer {
    public static void main(String[] args) {

        int port = 6666;
        if(args.length>0){
            try{
                port = Integer.parseInt ( args[0] );
            }catch (NumberFormatException e){
                System.out.println ("端口参数不正确，采用默认端口："+port);
            }

        }

        //准备线程池，四种方法：自己实例化new；单线程池；固定容量的；无限制的
        //无限制的---任务执行周期短，任务数量大==不能选择
        //单线程池--顺序执行==不能选择

        //在此选择固定的--fixed
        final ExecutorService executorService = Executors.newFixedThreadPool ( 10 );

        try {
            ServerSocket serverSocket = new ServerSocket ( port );
            System.out.println ("等待客户端连接...");
            while (true) {
                Socket client = serverSocket.accept ();//阻塞，等待客户端连接
                executorService.submit ( new ExecuteClient ( client ) );//连接后，提交任务
            }

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
