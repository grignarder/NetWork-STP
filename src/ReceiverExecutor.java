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
    private LogController logController;
    private String sender_ip;
    private int sender_port;
    private boolean isSYNed = false;

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
        this.init();
    }

    public void init(){
        //init udp socket
        try {
            this.udpSocket = new DatagramSocket(this.receiver_port,InetAddress.getByName(this.receiver_ip));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.logController = new LogController("src/receiver_log.txt");
    }

    public  void go() {

        getConnection();

        //listen and ACK
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.getConnection();

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

    public void getConnection(){
        while (true){
            ///
        }
    }

    public void send(byte[] data, boolean isSYN, boolean isFIN , int seq, int ack){
        STPsegement stpSegement = new STPsegement(data,isSYN,isFIN,seq,ack);
        try {
            DatagramPacket outPacket = new DatagramPacket(stpSegement.getByteArray(),
                    stpSegement.getByteArray().length,InetAddress.getByName(this.sender_ip),this.sender_port);
            this.udpSocket.send(outPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
