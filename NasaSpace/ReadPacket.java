import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class ReadPacket implements Runnable {
    private Packet packet;
    private InetAddress senderAddress;
    private Object object;
    static boolean notified = false;
    static boolean lastRead = false;
    static  boolean isMoved = false;
    public ReadPacket(InetAddress senderAddress, Packet packet,Object object){
        this.packet = packet;
        this.senderAddress = senderAddress;
        this.object = object;
    }
    public void readPacket() throws IOException {
        if(packet.getType() == 1){
            readSynPacket();
        }
        else if(packet.type == 2){
            readAckPacket();
        }
        else if(packet.type == 5){
            readMovePacket();
        }
        else {
            readFynPacket();
        }
    }
    public void readSynPacket(){
        int seqNum = packet.getSeqNum();
        System.out.println("in read syn");

        if (seqNum == Main.seqNum+1){
            if (!notified){
            synchronized (object){
                object.notify();
                System.out.println("notified");
                notified = true;
            }
            }
        }
    }
    public void readAckPacket() throws IOException {
        int ackNum = packet.getAckNum();
        System.out.println("this is ack" + ackNum);
        Buffer.removeAckedPackets(ackNum);
        Buffer.sendDroppedPackets(packet);
        if(ackNum == ReadFile.lastSequenceNumber){
            if(!lastRead){
            synchronized (object){
                lastRead = true;
                object.notify();
            }
        }
        }
    }
    public void readMovePacket() throws IOException {
        if (!isMoved){
        int seqNum = packet.getSeqNum();
        System.out.println("Moved");
        isMoved = true;
        for(int i =0;i<15;i++) {
            Packet packet = new Packet();
            packet.setSeqNum(seqNum);
            packet.setType(5);
            packet.sendPacket();
        }
        System.exit(0);
        }

    }
    public void readFynPacket(){
        synchronized (object){
            object.notify();
        }
    }
    @Override
    public void run() {
        try {
            readPacket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
