package com.example.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {

    private boolean mIsServiceDestoryed = false;
    private String[] mDefinedMessages = new String[]{
            "你好啊，哈哈哈哈",
            "请问你叫什么名字？",
            "今天天气不错啊！",
            "你知道吗？我可是可以和多个人同时聊天",
            "你有对象了么？"
    };

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestoryed) {
                try {
                    final Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread(){
                        @Override
                        public void run() {
                            try{
                                responseClient(client);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void responseClient(Socket client) throws IOException{
            //用于接受客户端消息
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //用于向客户端发送消息
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
            out.println("欢迎来到聊天室！");
            while (!mIsServiceDestoryed){
                String str = in.readLine();
                if(str == null){
                    //客户端断开连接
                    break;
                }
                int i = new Random().nextInt(mDefinedMessages.length);
                String msg = mDefinedMessages[i];
                out.println(msg);
                System.out.println("send: "+msg);
            }
            System.out.println("client quit.");
            //关闭流
            MyUtils.close(out);
            MyUtils.close(in);
            client.close();
        }
    }

}
