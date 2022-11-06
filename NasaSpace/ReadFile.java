import java.io.*;
import java.util.ArrayList;

public class ReadFile implements Runnable {
    String fileName;
    int sequenceNumber;
    Object object;
    static int lastSequenceNumber;
    ReadFile(String fileName,int sequenceNumber,Object object){
        this.fileName = fileName;
        this.sequenceNumber = sequenceNumber;
        this.object = object;
    }
    @Override
    public void run() {
        BufferedInputStream reader = null;
        int count = 0;
        try {
             reader = new BufferedInputStream(new FileInputStream (this.fileName));
             int i;
             while(true){

                Packet packet = new Packet();
                 byte[] array = new byte[10000];

                 i = reader.read(array);
                 for(int j = 0;j<i;j++){
                    packet.addData(array[j]);
                 }
                 while (Buffer.getCurrentUncakedPackets()>=Buffer.MAX_UNACKED_PACKETS){
                    Thread.currentThread().sleep(1000);
                }
                //System.out.println(Buffer.getCurrentUncakedPackets());
                //System.out.println("this is it");
                packet.createDataPacket(sequenceNumber);
                Buffer.addToBuffer(sequenceNumber,packet);
                 //System.out.println(sequenceNumber+ " :"  + Buffer.buffer);
                packet.sendPacket();
                Buffer.incrementPacketCount();
                sequenceNumber +=1;
                 if(i<10000){
                     lastSequenceNumber = sequenceNumber;
                     break;
                 }
                //System.out.println(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
                System.out.println("done reading"+ count);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
