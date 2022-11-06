import java.io.IOException;

public class Main {
    static Object object = new Object();
    static int seqNum ;
    static String receiverIpAddress;
    static String filePath;
    static TimeoutTimer timeoutTimer;
    static Thread thread;
    public static void main(String[] args) throws InterruptedException, IOException {
        filePath = args[0];
        receiverIpAddress = args[1];
        if(!filePath.equals("move")&&!filePath.equals("Move")) {
            thread = new Thread(new UdpReceiver(63002, receiverIpAddress));
            thread.start();
            System.out.println("reciever started");
            synchronized (object) {
                object.wait();
            }
            timeoutTimer = new TimeoutTimer();
            timeoutTimer.initializeTimer();
        }
        else{
            thread = new Thread(new UdpReceiver(63002, receiverIpAddress));
            thread.start();
            System.out.println("reciever started");
            Packet packet = new Packet();
            packet.createMovePacket();
            packet.sendPacket();
        }
    }
}
