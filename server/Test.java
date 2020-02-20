public class Test{
    public static void main(String[] args){
        int id = 10086;
        String info = "to:1789912638&msg:this is msg!";
        int idx = info.indexOf("&msg:");
        String msg = "from:" + id + "&msg:" + info.substring(idx + 5);
        info = info.substring(3,idx); //1789912638
        System.out.println(info);
        System.out.println(msg);

        info = "dataService:user login?id=123456&ip=123.5.6.7&port=12345";
        info = info.substring(12); //user login?id=123456&ip=123.5.6.7&port=12345
        System.out.println(info);
        if(info.indexOf("user login?") == 0){
            id = Integer.valueOf(info.substring(info.indexOf("id=") + 3, info.indexOf("&ip=")));
            System.out.println(id);
            String ip = info.substring(info.indexOf("ip=") + 3, info.indexOf("&port="));
            System.out.println(ip);
            int port = Integer.valueOf(info.substring(info.indexOf("port=") + 5));
            System.out.println(port);
        }

        byte[] data = null;

        data = "abcde中文".getBytes(); // 7个字符

        System.out.println(data.length);  //长度7
        System.out.println(new String(data, 0, data.length));
    }
}