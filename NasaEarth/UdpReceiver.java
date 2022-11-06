import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UdpReceiver implements Runnable {
    public int port = 63001; // port to listen on
    public String broadcastAddress; // multicast address to listen on
    public boolean running = true;

    // standard constructor
    public UdpReceiver(int thePort, String broadcastIp)
    {
        port = thePort;
        broadcastAddress = broadcastIp;

    }

    // listens to the ipaddress and reports when a message arrived
    public void receiveUDPMessage() throws
            IOException {
        byte[] buffer=new byte[70050];

        // create and initialize the socket
        System.out.println(broadcastAddress);
        DatagramSocket socket=new DatagramSocket(port);
        //InetAddress group=InetAddress.getByName(broadcastAddress);



        while(!Thread.currentThread().isInterrupted()){
            try {
                DatagramPacket packet=new DatagramPacket(buffer,buffer.length);

                // blocking call.... waits for next packet
                socket.receive(packet);
                //System.out.println("revieved packet");

                //String msg=new String(packet.getData(),packet.getOffset(),packet.getLength());
                //System.out.println(packet.getAddress());
                //String msg = new String(packet.getData(),packet.getOffset(),packet.getLength());
                //ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
                //Packet packet1 = (Packet) iStream.readObject();
                //iStream.close();
                ReadPacket readPacket = new ReadPacket(packet.getAddress() ,Packet.generatePacketFromBytes(packet.getData(),packet.getOffset(),packet.getLength()));
                new Thread(readPacket).start();


                //new Thread(readPacket).start();


                //System.out.println("[Multicast UDP message received from "+packet.getAddress()+"] "+msg);

                // give us a way out if needed
                if("EXIT".equals(packet.toString())) {
                    System.out.println("No more messages. Exiting : ");
                    break;
                }
            }catch(Exception e){
                e.printStackTrace();
                running = false;
            }
        }

        //close up ship

        socket.close();
    }

    // the thread runnable.  just starts listening.
    @Override
    public void run(){
        try {
            receiveUDPMessage();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
