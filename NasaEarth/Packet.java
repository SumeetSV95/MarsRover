import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Packet implements Serializable {
    final int  MAX_PACKET_SIZE = 10000;
    int type;
    int headerLength;
    int seqNum ;
    int ackNum ;
   // int dataLength;
    transient SynTimer synTimer;
    private static final long serialVersionUID = -6785768889157546991L;
    ArrayList<Byte> data = new ArrayList<>();
    ArrayList<Integer> cumulativeAcks = new ArrayList<>() ;
    transient TimeoutTimer timer;

    public void addData(byte data){
        this.data.add(data);
    }
    public boolean isFull(){
        return data.size() == this.MAX_PACKET_SIZE;
    }

    public void setAckNum(int ackNum) {
        this.ackNum = ackNum;
    }
    public ArrayList<Byte> getData(){
        return this.data;
    }
    public ArrayList<Integer> getCumulativeAcks(){
        return this.cumulativeAcks;
    }
    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAckNum() {
        return ackNum;
    }



    public int getHeaderLength() {
        return headerLength;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public int getType() {
        return type;
    }
    public void addToCumulativeAck(int seqNum){
        cumulativeAcks.add(seqNum);
    }

    public void createSynPacket(){
        this.setType(1);
        this.setAckNum(0);
        this.setHeaderLength(10);
        Buffer.incrementExpectedSequenceNum();
        this.setSeqNum(Buffer.getExpectedSequenceNum());
        this.synTimer = new SynTimer(this);
        this.synTimer.initializeTimer();

    }
    public void createMovePacket(){
        this.setType(5);
        int sequenceNum = (int) (Math.random()*1000);
        this.setSeqNum(sequenceNum);
        Buffer.setExpectedSequenceNum(seqNum);
        this.synTimer = new SynTimer(this);
        this.synTimer.initializeTimer();
    }


    public void sendPacket() throws IOException {
        ArrayList<Byte> msg =  generateBytesFromPacket();
        byte[] result = new byte[msg.size()];
        for(int i = 0; i < msg.size(); i++) {
            result[i] = msg.get(i);
        }
        UdpSender sender = new UdpSender(63100,Main.receiverIpAddress,result);
        Thread thread = new Thread(sender);
        thread.start();
       // System.out.println("packet sent");
    }
    public ArrayList<Byte> generateBytesFromPacket(){
        ArrayList<Byte> byteArrayList = new ArrayList<>();
        byteArrayList.add((byte)this.type);
        byteArrayList.add((byte)this.headerLength);
        for (byte b:ByteBuffer.allocate(4).putInt(this.seqNum).array()){
            byteArrayList.add(b);
        }
        for (byte b:ByteBuffer.allocate(4).putInt(this.ackNum).array()){
            byteArrayList.add(b);
        }
        //System.out.println(this.data);
        //System.out.println(this.data.size());
        for (int seq:this.cumulativeAcks){
            for (byte b:ByteBuffer.allocate(4).putInt(seq).array()){
                byteArrayList.add(b);
            }
        }
        byteArrayList.addAll(this.data);
        //System.out.println("byte arraylist size"+ byteArrayList.size());
        return byteArrayList;
    }
    public static Packet generatePacketFromBytes(byte[] bytes,int offset,int length) throws IOException {
        Packet packet = new Packet();

        ByteArrayInputStream byteArrayInputStr
                = new ByteArrayInputStream(bytes,offset,length);
        packet.setType((int) byteArrayInputStr.read());
        int headerLength = (int) byteArrayInputStr.read();
        packet.setHeaderLength(headerLength);
        byte[] seqBytes = new byte[4];
        byteArrayInputStr.read(seqBytes);
        packet.setSeqNum(ByteBuffer.wrap(seqBytes).getInt());
        byte[] ackBytes = new byte[4];
        byteArrayInputStr.read(ackBytes);
        packet.setAckNum(ByteBuffer.wrap(ackBytes).getInt());
        if (headerLength>10){
            int acks = headerLength -10;
            for (int j =0;j<acks;j++){
                byte[] sBytes = new byte[4];
                byteArrayInputStr.read(sBytes);
                packet.cumulativeAcks.add(ByteBuffer.wrap(sBytes).getInt());
            }
        }
        byte[] array = new byte[10000];
        int i = byteArrayInputStr.read(array);
        for(int j=0;j<i;j++){
            packet.addData(array[j]);
        }

        return packet;
    }
}
