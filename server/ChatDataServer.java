import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


class DataServiceThread implements Runnable{
    
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    
    public DataServiceThread(DatagramSocket socket,DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run() {
        String info = null;
        byte[] data = null;
        info = new String(packet.getData(), 0, packet.getLength());
        try{
            if(info.equals("启动路由服务器")){
                data = "dataService:route start!".getBytes();
                socket.send(new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort()));
                Rser.addSer(packet.getPort(), packet.getAddress().getHostAddress());
                System.out.println(Rser.getSerCount()+"个路由服务器已连接");
            }else if(info.indexOf("user:login?") == 0){  // user:login?id=xxx&password=xxx...
                int id = Integer.valueOf(info.substring(info.indexOf("id=") + 3, info.indexOf("&password=")));
                String p = info.substring(info.indexOf("password=") + 9);
                if(Duser.ulogin(id, p)){
                    Rser routeSer = Rser.getARser();  // 获得一个路由服务器
                    data = ("dataService:address?ip=" + routeSer.ip + "&port=" + routeSer.port).getBytes();  // dataService:address?ip=xxx&port=xxx
                    socket.send(new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort()));
                    //System.out.println(id+"已登录");
                    data = ("dataService:user login?id="+id+"&ip="+packet.getAddress().getHostAddress()+"&port="+packet.getPort()).getBytes();  // dataService:user login?id=xxx&ip=xxx&port=xxx
                    if(routeSer != null) socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(routeSer.ip), routeSer.port));
                }else{
                    data = "dataService:f".getBytes();
                    socket.send(new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort()));
                }
            }
            else if(info.indexOf("user:logout?") == 0){  // user:logout?id=xxx&password=xxx...
                int id = Integer.valueOf(info.substring(info.indexOf("id=") + 3));
                data = ("dataService:user logout?id="+id).getBytes();
                Rser routeSer = Rser.getARser();
                if(routeSer != null) socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(routeSer.ip), routeSer.port));
                //System.out.println(id+"已登出");
            }
            else{
                System.out.println(info);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

class Rser {  //路由服务器
    private static Rser[] routeService = new Rser[5];
    public int port;
    public String ip;
    public int link; // 连接数，用于判断是否满

    public Rser (int port, String ip) {
        this.port = port;
        this.ip = ip;
        link = 0;
    }

    public void setLink(int link){
        this.link = link;
    }

    public static boolean addSer (int port, String ip){  //一个路由服务器连接时添加
        boolean add = false;
        for(int i =0;i<5;i++){
            if(routeService[i]==null){
                if(!add){
                    routeService[i] = new Rser(port, ip);
                    add = true;
                }
            }
            else if(routeService[i].port == port && routeService[i].ip.equals(ip)){
                routeService[i] = null;
            }
        }
        return add;
    }

    public static int getSerCount(){  //获得连接的路由服务器数
        int count = 0;
        for(int i = 0;i<5;i++){
            if(routeService[i]!=null){
                count++;
            }
        }
        return count;
    }

    public static Rser getARser(){
        for(int i=0;i<5;i++){
            if(routeService[i]!=null){  // 没有判断路由服务器是否满
                return routeService[i];
            }
        }
        return null;
    }
}

class Duser {
    private static Duser[] user = new Duser[5];  // 假设有5个用户信息

    public int id;
    private String name;
    private String password;

    public Duser (int id){
        this.id = id;
        this.name = "name";
        this.password = "123456";
    }

    public void rename (String name){
        this.name = name;
    }

    public boolean pass (String p){
        boolean pa = password.equals(p) ? true : false;
        return pa;
    }

    public boolean cPassword(String oldp, String newp){
        boolean c = false;
        if(password.equals(oldp) && !password.equals(newp)){
            password = newp;
            c = true;
        }
        return c;
    }

    public static boolean ulogin(int id ,String p){
        boolean in = false;
        for(int i=0;i<5;i++){
            if(user[i] != null && user[i].id == id){
                in = user[i].pass(p);
            }
        }
        return in;
    }

    public static String getUName(int id){
        for(int i=0;i<5;i++){
            if(user[i] != null && user[i].id == id){
                return user[i].name;
            }
        }
        return null;
    }

    public static void user_test(){
        user[0] = new Duser(1789912638);
        user[0].rename("Trnny");
        user[1] = new Duser(1964014004);
        user[1].rename("waff");
        user[2] = new Duser(428781998);
        user[2].cPassword("123456", "12345678tu");
    }
}

public class ChatDataServer {   // 数据服务
    public static void main(String[] args){
        try {
            DatagramSocket socket = new DatagramSocket(11290);  // 以固定port启动
            DatagramPacket packet = null;
            byte[] data = null;
            //ChatDataServer cds = new ChatDataServer();
            System.out.println("数据服务器正在运行");
            Duser.user_test();
            while(true){
                data = new byte[1024]; // 创建字节数组，指定接收的数据包的大小
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                Thread thread = new Thread(new DataServiceThread(socket, packet));
                thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }
}