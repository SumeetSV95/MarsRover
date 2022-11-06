import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutTimer {
    private TimerTask timeOut ;
    private Timer timer;
    private int sequenceNumber;
    public TimeoutTimer(int sequenceNumber){
        this.sequenceNumber = sequenceNumber;
    }
    public void initializeTimer(){
        if(timer == null){
            this.timer = new Timer();
        }
        this.timeOut = new TimerTask() {
            @Override
            public void run() {
                try {
                    sendPacket(sequenceNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        this.timer.scheduleAtFixedRate(timeOut,10000,10000);

    }
    public void sendPacket(int sequenceNumber) throws IOException {
        Packet packet = Buffer.getPacket(sequenceNumber);
        //System.out.println( packet.data);

        System.out.println("squeenceNumber "+packet.getSeqNum());
        //System.out.println("in here");
        packet.sendPacket();

    }
    public void cancelTimer(){


        this.timer.cancel();
        this.timeOut.cancel();
    }
}
