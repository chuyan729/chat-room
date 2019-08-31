package com.chuyan.chatroom.client.single;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天室客户端程序
 * Author： chuyan
 * Date：2019/8/5
 */
public class SingleThreadClient {

    public static void main(String[] args) {
        try {

            //0.通过命令行获取参数
            int port = 6666;
            if(args.length>0){
                try{
                    port = Integer.parseInt ( args[0] );
                }catch (NumberFormatException e){
                    System.out.println ("端口参数不正确，采用默认端口："+port);
                }

            }

            String host = "127.0.0.1";
            if(args.length>1){

                // 对host进行一个格式校验
                String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                        +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                        +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                        +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
                Pattern pattern = Pattern.compile(ip);
                Matcher matcher = pattern.matcher(args[1]);
                if(matcher.matches ()){
                    host = args[1];
                }else{
                    System.out.println ("ip参数不正确，采用默认ip："+host);
                }
            }
            //1.创建Socket对象
            Socket clientSocket = new Socket ( host,port );

            //2.发送数据和接收数据
            OutputStream clientOut = clientSocket.getOutputStream ();
            OutputStreamWriter writer = new OutputStreamWriter ( clientOut );
            writer.write ( "你好服务器\n" );
            writer.flush ();

            InputStream clientInput = clientSocket.getInputStream ();
            Scanner sc = new Scanner ( clientInput );
            String serverData = sc.nextLine ();
            System.out.println ("来自服务器的数据："+serverData);

            //3.关闭连接
            clientSocket.close ();
            System.out.println ("关闭连接");
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }
}
