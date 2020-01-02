package webapp.demoh_socket;


import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.ext.Act1;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SoDemoClientTest {

    public static void test() {
//        do_test();

        for(int i=0; i<10; i++){
            new Thread(()->do_test()).start();
        }
    }

    private static void do_test(){
        try {

            SocketClient client = new SocketClient();
            client.start();

            Thread.sleep(100);

            client.send("/demog/中文/1","Hello 世界!", msg->{
                System.out.println(msg.toString());
            });


            Thread.sleep(100);

            client.send("/demog/中文/2","Hello 世界2!", msg->{
                System.out.println(msg.toString());
            });

            Thread.sleep(100);

            client.send("/demog/中文/3","close", msg->{
                System.out.println(msg.toString());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public static byte[] getBytes(InputStream input) throws IOException {
        if (input == null) {
            return null;
        }

        byte[] lenBts = new byte[4];
        if (input.read(lenBts) < -1) {
            return null;
        }

        int len = bytesToInt32(lenBts);
        byte[] bytes = new byte[len];
        bytes[0] = lenBts[0];
        bytes[1] = lenBts[1];
        bytes[2] = lenBts[2];
        bytes[3] = lenBts[3];

        input.read(bytes, 4, len - 4);

        return bytes;
    }

    public static SocketMessage getMessage(InputStream input) {
        try {
            byte[] bytes = getBytes(input);
            if (bytes == null) {
                return null;
            }

            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            SocketMessage tmp = SocketMessage.decode(buffer);

            return tmp;
        } catch (Throwable ex) {
            System.out.println("Decoding failure::");
            ex.printStackTrace();
            return null;
        }
    }

    private static int bytesToInt32(byte[] bs) {
        //获取最高八位
        int num1 = 0;
        num1 = (num1 ^ (int) bs[0]);
        num1 = num1 << 24;
        //获取第二高八位
        int num2 = 0;
        num2 = (num2 ^ (int) bs[1]);
        num2 = num2 << 16;
        //获取第二低八位
        int num3 = 0;
        num3 = (num3 ^ (int) bs[2]);
        num3 = num3 << 8;
        //获取低八位
        int num4 = 0;
        num4 = (num4 ^ (int) bs[3]);
        return num1 ^ num2 ^ num3 ^ num4;
    }
}
