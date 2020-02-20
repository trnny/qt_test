import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.io.InputStreamReader;

class Dcfg{   // 保存数据服务器的信息(假定它是不变的)
    public static int port = 11290;
    public static String ip = "10.70.38.26";
}

class ChatThread implements Runnable{  // 与路由服务器

    DatagramSocket socket = null;

    public ChatThread(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
    }
}

class DataThread implements Runnable{  // 与数据服务器

    DatagramSocket socket = null;

    public DataThread(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
    }
}

// 

public class ChatClient { 
    public static void main(String[] args){
        try{
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = null;
            byte[] data = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String read = null;
            boolean login = false;
            int id = 0;
            int state = 0;  // 0 空闲 1 已登录 2 对话中
            System.out.println("客户端启动成功");
            while(true){
                read = br.readLine();
                if(state == 0){
                }else if(state == 1){
                }else{
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
