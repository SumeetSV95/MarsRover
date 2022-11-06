import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SynTimer {
    private TimerTask timeOut ;
    private Timer timer;
    private Packet synPacket;
    public SynTimer(Packet synPacket){
        this.synPacket = synPacket;
    }
    public void initializeTimer(){
        if(timer == null){
            this.timer = new Timer();
        }
        this.timeOut = new TimerTask() {
            @Override
            public void run() {
                //todo
                try {
                    sendSynPacket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        this.timer.scheduleAtFixedRate(timeOut,10,1000);

    }
    public void sendSynPacket() throws IOException {
        this.synPacket.sendPacket();
    }
    public void cancelTimer(){


        this.timer.cancel();
        this.timeOut.cancel();
    }
}
