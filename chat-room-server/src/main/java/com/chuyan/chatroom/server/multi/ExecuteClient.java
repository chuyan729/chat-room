package com.chuyan.chatroom.server.multi;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端处理客户端连接的任务
 * 注册 私聊 群聊 退出 等
 * 扩展：显示当前在线人数、用户活跃度
 * Author： chuyan
 * Date：2019/8/6
 */
public class ExecuteClient implements Runnable{

    /*
     此处map应为static；如果不是，则这个map是这个对象的一个属性
     MultiThreadServer中一直在new ExecteClient，则会有很多的map
     所以需要是static的，共享
    多线程聊天室，并发修改，将被多个线程所访问；hashmap、treemap线程不安全，消息乱了
     */
    private static final Map<String,Socket> ONLINE_USER_MAP = new ConcurrentHashMap<> ( );

    //跟客户端交互，所以要把客户端传过来
    private final Socket client;

    public ExecuteClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //1.获取客户端输入
            InputStream clientInput = this.client.getInputStream ();
            Scanner scanner = new Scanner ( clientInput );//字节流转为字符流

            while(true){
                String line = scanner.nextLine ();//获取一行
                /**
                 * 格式
                 * 1.注册：userName：<name>
                 * 2.私聊：private:<name>:<message>
                 * 3.群聊：group:<message>
                 * 4.退出：bye
                 */

                //改进：校验
                if(line.startsWith ( "userName" )){
                    String[] segments = line.split ( "\\:" );
                    if (segments[0].equals ( "userName" )){
                        String userName = segments[1];
                        this.register(userName,client);
                    }else{
                        this.sendMessage ( this.client,"无法识别请输入：userName:xxx" );
                    }
                    continue;
                }
                if(line.startsWith ( "private" )){
                    String[] segments = line.split ( "\\:" );
                    if (segments[0].equals ( "private" )){
                        String userName = segments[1];
                        String message = segments[2];
                        this.privateChat(userName,message);
                    }else {
                        this.sendMessage ( this.client,"无法识别请输入：private:xxx:xxx" );
                    }
                    continue;
                }
                if(line.startsWith ( "group" )){
                    String[] segments = line.split ( "\\:" );
                    if (segments[0].equals ( "group" )){
                        String message = segments[1];
                        this.groupChat(message);
                    }else{
                        this.sendMessage ( this.client,"无法识别请输入：group:xxx" );
                    }
                    continue;
                }
                if(line.startsWith ( "bye" )){
                    this.quit();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    //退出
    private void quit() {
        String currentUserName = this.getCurrentUserName ();
        System.out.println ("用户-"+currentUserName+"下线");

        this.sendMessage ( this.client,"bye" );

        //将此用户从在线用户集合中删除
        ONLINE_USER_MAP.remove ( currentUserName );
//
//        Socket socket = ONLINE_USER_MAP.remove ( currentUserName );
//        this.sendMessage ( socket,"bye" );
        printOnlinrUser ();
    }

    //群聊
    private void groupChat(String message) {
        for(Socket socket : ONLINE_USER_MAP.values ()){
            if (socket.equals ( this.client )){
                continue;//群聊的消息，不给自己发
            }
            this.sendMessage ( socket,this.getCurrentUserName ()+"说："+message );
        }
    }

    //私聊
    private void privateChat(String userName, String message) {
        String currentUserName = this.getCurrentUserName ();//获得当前用户名

        //当前客户端想要私聊的目标
        Socket target = ONLINE_USER_MAP.get ( userName );

        if(target!=null){
            this.sendMessage ( target, currentUserName+"对你说："+message);
//            try {
//                OutputStream clientOutput = target.getOutputStream ();
//                OutputStreamWriter writer = new OutputStreamWriter ( clientOutput );
//                writer.write ( currentUserName+"对你说："+message );
//                writer.flush ();
//            } catch (IOException e) {
//                e.printStackTrace ();
//            }
        }
    }

    //注册
    private void register(String userName, Socket client) {
        System.out.println (userName+"加入到聊天室"+client.getRemoteSocketAddress ());
        ONLINE_USER_MAP.put ( userName,client );
        printOnlinrUser ();
        this.sendMessage ( this.client,userName+"注册成功!" );

//发送数据的代码提取到sendMessage方法中
//        try {
//            OutputStream serverToClient = client.getOutputStream ();
//            OutputStreamWriter writer = new OutputStreamWriter ( serverToClient );
//            writer.write ( userName+"注册成功!\n" );
//            writer.flush ();
//        } catch (IOException e) {
//            e.printStackTrace ();
//        }
    }

    //得到当前客户端的用户名
    private String getCurrentUserName(){
        String currentUserName = "";
        for(Map.Entry<String,Socket> entry : ONLINE_USER_MAP.entrySet ()){
            if(this.client.equals ( entry.getValue () )){
                currentUserName = entry.getKey ();
                break;
            }
        }
        return currentUserName;
    }

    /**
     *
     * @param socket 发送的对象
     * @param message 发送的消息
     */
    private void sendMessage(Socket socket,String message){
        try {
            OutputStream serverToClient = socket.getOutputStream ();
            OutputStreamWriter writer = new OutputStreamWriter ( serverToClient );
            writer.write ( message+"\n" );
            writer.flush ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private static void printOnlinrUser(){
        System.out.println ("当前在线人数："+ONLINE_USER_MAP.size ()+" 用户名如下列表：");
        for(Map.Entry<String,Socket> entry : ONLINE_USER_MAP.entrySet ()){
            System.out.println (entry.getKey ());

        }
    }
}


