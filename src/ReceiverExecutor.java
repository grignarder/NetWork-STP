import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class ReceiverExecutor {
    //param
    private int receiver_port;
    private String filename;

    //other need attributes
    private int MSS = 24;
    private DatagramSocket udpSocket;
    private String receiver_ip = "localhost";

    //MSG
    private static String INPUT_ERROR_MSG = "usage: java Receiver <receiver_port> <file.txt>";

    public ReceiverExecutor(String args[]){
        //read params
        if (args.length != 2) {
            System.out.println(INPUT_ERROR_MSG);
        }
        try {
            this.receiver_port = Integer.parseInt(args[0]);
            this.filename = args[1];
        } catch (Exception e) {
            System.out.println(INPUT_ERROR_MSG);
        }
    }

    public  void go() {
        //init udp socket
        try {
            this.udpSocket = new DatagramSocket(this.receiver_port,InetAddress.getByName(this.receiver_ip));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //listen and ACK
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(true){
            byte[] buffer = new byte[this.MSS+10];
            DatagramPacket rcvPacket = new DatagramPacket(buffer,buffer.length);
            try {
                this.udpSocket.receive(rcvPacket);
                STPsegement stPsegement= new STPsegement(buffer);
                fos.write(stPsegement.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

            DatagramPacket sendPacket = null;
            try {
                sendPacket = new DatagramPacket("ACK".getBytes(),"ACK".getBytes().length,InetAddress.getByName("localhost"),8888);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                this.udpSocket.send(sendPacket);
                System.out.println("send!!!!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
