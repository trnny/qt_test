import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class RouteServiceThread implements Runnable{
    
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    
    public RouteServiceThread(DatagramSocket socket,DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run() {
        String info = null;
        InetAddress address = null;
        int port;  //
        int id;   //  用户消息来源id
        String ip;   // 用户消息去向ip
        byte[] data = null;
        try {
            info = new String(packet.getData(), 0, packet.getLength());
            if(info.indexOf("dataService:")==0 && packet.getPort()==Dcfg.port){  // 从数据服务器来 1.服务器启动成功 2.用户登录 3.用户退出
                info = info.substring(12);
                if(info.equals("route start!")){  // dataService:route start!
                    System.out.println("路由服务器启动成功!");
                }else if(info.indexOf("user login?") == 0){  // dataService:user login?id=xxx&ip=xxx&port=xxx
                    id = Integer.valueOf(info.substring(info.indexOf("id=") + 3, info.indexOf("&ip=")));
                    ip = info.substring(info.indexOf("ip=") + 3, info.indexOf("&port="));
                    port = Integer.valueOf(info.substring(info.indexOf("port=") + 5));
                    if(Cuser.addUser(id, ip, port)){
                        System.out.printf("%d 登录\n", id);
                    }
                }else if(info.indexOf("user logout?")==0){  //dataService:user login?id=xxx
                    id = Integer.valueOf(info.substring(info.indexOf("id=") + 3));
                    if(Cuser.delUser(id)){
                        System.out.printf("%d 登出\n", id);
                    }
                }
            }else{  // 不是数据服务器来 (to:xxx&msg:xxx)  // 基本转发功能完成 需添加 转发返回状态判断
                id = Cuser.getID(packet.getAddress().getHostAddress(), packet.getPort());
                //System.out.println("消息id:" + id);
                if(id != 0 && info.indexOf("to:")==0){
                    int idx = info.indexOf("&msg:");
                    String msg = "from:" + id + "&msg:" + info.substring(idx + 5);
                    id = Integer.valueOf(info.substring(3,idx));  // 去向id
                    address = InetAddress.getByName(Cuser.getIP(id));
                    port = Cuser.getPORT(id);
                    data = msg.getBytes();
                    socket.send(new DatagramPacket(data, data.length, address, port));
                    //System.out.println(msg);
                }
                else {
                    System.out.println("未知消息:" + info);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Cuser {
    private static Cuser[] user = new Cuser[20];  // 登录时赋值，登出时删除
    public int id;
    public int port;
    public String ip;

    public Cuser (int id, int port, String ip){
        this.id = id;
        this.port = port;
        this.ip = ip;
    }

    public static int getID(String ip, int port){
        for(int i=0;i<20;i++){
            if(user[i] != null){
                //System.out.println(user[i].id+" "+user[i].ip+" "+user[i].port);
                if(user[i].port == port && user[i].ip.equals(ip)){
                    return user[i].id;
                }
            }
        }
        return 0;
    }
    
    public static String getIP(int id){
        for(int i=0;i<20;i++){
            if(user[i] != null){
                if(user[i].id == id){
                    return user[i].ip;
                }
            }
        }
        return null;
    }
    
    public static int getPORT(int id){
        for(int i=0;i<20;i++){
            if(user[i] != null){
                if(user[i].id == id){
                    return user[i].port;
                }
            }
        }
        return 0;
    }
    
    public static boolean addUser(int id, String ip, int port){
        delUser(id);
        for(int i=0;i<20;i++){
            if(user[i] == null ){
                user[i] = new Cuser(id, port, ip);
                //System.out.println(id + " " + ip + " " + port);
                return true;
            }
        }
        return false;
    }
    
    public static boolean delUser(int id){
        boolean del = false;
        for(int i=0;i<20;i++){
            if(user[i] != null && user[i].id == id){
                user[i] = null;
                del = true;
            }
        }
        return del;
    }
}

class Dcfg{   // 保存一下数据服务器的信息(假定它是不变的)
    public static int port = 11290;
    public static String ip = "10.70.2.122";
}

public class ChatRouteServer {   // 路由r服务
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();  // 启动一个路由服务器 告知数据服务器 ，从数据服务器获取本服务器IP, port
        DatagramPacket packet = null;
        byte[] data = null;

        data = "启动路由服务器".getBytes();
        InetAddress DSAddress = InetAddress.getByName(Dcfg.ip);
        packet = new DatagramPacket(data, data.length, DSAddress, Dcfg.port);
        socket.send(packet);
        System.out.println("路由服务器正在启动...");

        while(true){
            data = new byte[1024]; //创建字节数组，指定接收的数据包的大小
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            Thread thread = new Thread(new RouteServiceThread(socket, packet));
            thread.start();
        }
    }
}