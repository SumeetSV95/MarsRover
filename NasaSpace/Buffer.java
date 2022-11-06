import java.io.IOException;
import java.util.*;

public class Buffer {
    final static int MAX_UNACKED_PACKETS = 50;
    static private int currentUncakedPackets = 0;
    static public TreeMap<Integer,Packet> buffer = new TreeMap<>();
    static synchronized public int getCurrentUncakedPackets(){
        return currentUncakedPackets;
    }

    static synchronized void decrementPacketCount(){
        currentUncakedPackets -= 1;
    }
    static synchronized void incrementPacketCount(){
        currentUncakedPackets += 1;
    }
    static synchronized Packet getPacket(int seqNumber){
        if (buffer.containsKey(seqNumber)){
            return buffer.get(seqNumber);
        }
       return null;
    }
    static synchronized void addToBuffer(int synNum,Packet packet){
        buffer.put(synNum,packet);
    }
    static synchronized void removeAckedPackets(int ack){
        System.out.println(buffer);
        System.out.println(ack);
        while(!buffer.isEmpty()&& buffer.firstKey()<ack){
            Map.Entry<Integer,Packet> entry= buffer.pollFirstEntry();
            Packet packet = entry.getValue();
            if(packet.getType()==1){
                packet.cancelSynTimer();
            }else{
                packet.cancelTimeOut();
            }
            decrementPacketCount();
        }
    }
    static synchronized void sendDroppedPackets(Packet packet) throws IOException {
        ArrayList<Integer> cumulativeAcks = packet.getCumulativeAcks();
        System.out.println("cumulative acks: " + cumulativeAcks);
        Set<Integer> set = new HashSet<>();
        for(Integer seq:cumulativeAcks){
            for(Map.Entry<Integer,Packet>entry:Buffer.buffer.entrySet()){
                if(entry.getKey()<seq&&!set.contains(entry.getKey())){
                    entry.getValue().sendPacket();
                    System.out.println("send form dropped packets" + entry.getKey());
                    set.add(entry.getKey());
                }else if(seq.equals(entry.getKey())){
                    set.add(seq);
                    break;
                }
            }
        }
    }
}
