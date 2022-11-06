import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Buffer {
    static int expectedSequenceNum=0;
    static TreeMap<Integer,Packet> buffer = new TreeMap<>();


    static synchronized void incrementExpectedSequenceNum(){
        expectedSequenceNum +=1;
    }

    public static int getExpectedSequenceNum(){
        return expectedSequenceNum;
    }
    static synchronized void addToBuffer(int synNum,Packet packet){
        buffer.put(synNum,packet);
    }
    public static synchronized void setExpectedSequenceNum(int expectedSequenceNum) {
        Buffer.expectedSequenceNum = expectedSequenceNum;
    }
    public static synchronized void readFromBuffer() throws IOException {
        Set<Integer> pollSet = new HashSet<>();
        Packet sendPacket = new Packet();
        boolean isCumulativeAck = false;
        System.out.println(buffer);
        System.out.println(getExpectedSequenceNum());
        for(Map.Entry<Integer,Packet> entry:Buffer.buffer.entrySet()){
            Packet packet = entry.getValue();
            if(packet.getSeqNum()==getExpectedSequenceNum()){
              WriteToFile.writeToFile(packet.data);
              incrementExpectedSequenceNum();
              pollSet.add(packet.getSeqNum());
            }else{
                isCumulativeAck = true;

                sendPacket.addToCumulativeAck(packet.getSeqNum());
            }
        }

        sendPacket.setAckNum(getExpectedSequenceNum());
        sendPacket.setHeaderLength(10+sendPacket.cumulativeAcks.size());
        for(Integer seqNum: pollSet){
            Buffer.buffer.remove(seqNum);
        }
        sendPacket.setType(2);
        sendPacket.sendPacket();
    }
}
