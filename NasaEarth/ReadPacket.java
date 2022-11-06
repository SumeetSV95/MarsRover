import java.io.IOException;
import java.net.InetAddress;

public class ReadPacket implements Runnable {
    private Packet packet;
    private InetAddress senderAddress;
    private static Packet synPacket;
    static boolean synDone = false;
    public ReadPacket(InetAddress senderAddress, Packet packet){
        this.packet = packet;
        this.senderAddress = senderAddress;
    }
    public void readPacket() throws IOException, InterruptedException {
        if(packet.type == 1){
            readSynPacket();
        }
        else if(packet.type == 2){
            readDataPacket();
        }
        else if(packet.type == 5){
            readMovePacket();
        }
        else{
            readFynPacket();
        }
    }
    public void readSynPacket() throws IOException {
    //todo
        synchronized (Main.object){
        if(Buffer.getExpectedSequenceNum()==0){
       int seqNum = packet.getSeqNum();

       Buffer.setExpectedSequenceNum(seqNum);
       synPacket = new Packet();
       synPacket.createSynPacket();
       synPacket.sendPacket();
        System.out.println("in here");

            Main.object.notify();
           System.out.println("notified");
       }
        }

    }
    public void readMovePacket(){
        if(packet.getSeqNum()==Buffer.getExpectedSequenceNum()){
            System.out.println("Move recieved");
            System.exit(0);
        }
    }
    public void readDataPacket() throws InterruptedException {
        //todo
        if (!synDone){
            System.out.println(synPacket);
            System.out.println(synPacket.synTimer);
            synPacket.synTimer.cancelTimer();
            synDone = true;
        }
        int seqNum = packet.getSeqNum();
        System.out.println( "sequence Number" + seqNum);
        if (seqNum>=Buffer.getExpectedSequenceNum()&&!Buffer.buffer.containsKey(seqNum)){
            Buffer.addToBuffer(seqNum,packet);
        }
    }
    public void readFynPacket() throws IOException {
        System.out.println("in fyn packet");
        for(int i =0;i<10;i++){
        Packet packet1 = new Packet();
        packet1.setType(3);
        packet1.setHeaderLength(10);
        packet1.setSeqNum(0);
        packet1.setAckNum(0);
        packet1.sendPacket();
        }
        Main.timeoutTimer.cancelTimer();
        Main.thread.interrupt();
        System.exit(0);
    }

    @Override
    public void run() {
        try {
            readPacket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
