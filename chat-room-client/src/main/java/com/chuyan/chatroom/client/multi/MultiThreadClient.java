package com.chuyan.chatroom.client.multi;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author： chuyan
 * Date：2019/8/6
 */
public class MultiThreadClient {

    public static void main(String[] args) {

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
        try {
            final Socket socket = new Socket ( host,port );

            //1.往服务器发送数据
            new WriteDataToServerThread ( socket ).start ();

            //2.从服务器读数据
            new ReadDataFromServerThread ( socket ).start ();

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
