import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSender implements Runnable {
    public int port = 63001; // port to send on
    public String broadcastAddress; // multicast address to send on
    public int node = 0; // the arbitrary node number of this executable
    byte[] message ;

    // standard constructor
    public UdpSender(int thePort, String broadcastIp,byte[] message)
    {
        port = thePort;
        broadcastAddress = broadcastIp;
        this.message = message;
    }

    // Send the UDP Multicast message
    public void sendUdpMessage(byte[] message) throws IOException {
        // Socket setup
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(broadcastAddress);

        // Packet setup

        DatagramPacket packet = new DatagramPacket(message, message.length, group, port);

        // let 'er rip
        socket.send(packet);
        //System.out.println("packet sent");
        socket.close();
    }

    // the thread runnable.  Starts sending packets every 500ms.
    @Override
    public void run(){

        try {
            // set our message as "Node 1" (or applicable number)
            sendUdpMessage(this.message);

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
