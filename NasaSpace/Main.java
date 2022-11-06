import java.awt.dnd.DragSource;
import java.io.IOException;
import java.net.InetAddress;

public class Main {

    static int seqNum ;
    static String receiverIpAddress;
    static Thread thread;
    public static void main(String[] args) throws IOException, InterruptedException {
       final Object object = new Object();
        String filePath = args[0];
        receiverIpAddress = args[1];
        if(!filePath.equals("move")&&!filePath.equals("Move")) {
        thread = new Thread(new UdpReceiver(63100, receiverIpAddress,object));
        thread.start();
        System.out.println("reciever started");
        Packet packet = new Packet();
        seqNum = packet.createSynPacket();
        System.out.println(seqNum);
        Buffer.addToBuffer(seqNum,packet);
        packet.sendPacket();
        System.out.println("packet sent");
        synchronized (object){
            object.wait();
            packet.synTimer.cancelTimer();
        }
        System.out.println("reading started");
        ReadFile readFile = new ReadFile(filePath,Main.seqNum+1,object);
        Thread thread1 = new Thread(readFile);
        thread1.start();
        synchronized (object){
            object.wait();
        }
        System.out.println("initializing fyn");
        Packet packet1 = new Packet();
        packet1.setType(3);
        packet1.setHeaderLength(10);
        packet1.setSeqNum(ReadFile.lastSequenceNumber);
        packet1.setAckNum(0);
        packet1.sendPacket();
        Buffer.addToBuffer(ReadFile.lastSequenceNumber,packet1);
        packet1.initializeTimer();
        synchronized (object){
            object.wait();
        }
        System.out.println("here");
        thread.interrupt();
        System.exit(0);
    }else{
            thread = new Thread(new UdpReceiver(63100, receiverIpAddress,object));
            thread.start();
            System.out.println("reciever started");
        }
    }

}
