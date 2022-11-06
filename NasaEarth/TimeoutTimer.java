import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutTimer {
    private TimerTask timeOut ;
    private Timer timer;

    public void initializeTimer(){
        if(timer == null){
            this.timer = new Timer();
        }
        this.timeOut = new TimerTask() {
            @Override
            public void run() {
            //todo
                try {
                    sendAck();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        this.timer.scheduleAtFixedRate(timeOut,10,1000);

    }

    public void sendAck() throws IOException {
        System.out.println("writer started");
        Buffer.readFromBuffer();
    }
    public void cancelTimer(){


        this.timer.cancel();
        this.timeOut.cancel();
    }

}
