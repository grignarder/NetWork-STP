import java.io.File;
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
    private int seq = 200;
    private int ack = 0;
    private byte[] buffer;

    //const
    private static String INPUT_ERROR_MSG = "usage: java Receiver <receiver_port> <file.txt>";
    private static int STPheaderSize = 10;

    public ReceiverExecutor(String args[]) throws SocketException, UnknownHostException {
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

    public void init() throws UnknownHostException, SocketException {
        this.udpSocket = new DatagramSocket(this.receiver_port,InetAddress.getByName(this.receiver_ip));
        //init logcontroller
        this.logController = new LogController("src/receiver_log.txt");
    }

    public  void go() throws IOException {

        getConnection();

         //set file output stream
        FileOutputStream fos = new FileOutputStream(this.filename);
        buffer = new byte[this.MSS+STPheaderSize];
        DatagramPacket rcvPacket = new DatagramPacket(buffer,buffer.length);

        while(true){
            this.udpSocket.receive(rcvPacket);
            STPsegement stpSegement= new STPsegement(buffer);
            if(stpSegement.getFIN()){
                killConnection(stpSegement);
                break;
            }
            this.ack = stpSegement.getSeq()+stpSegement.getDataLength();
            fos.write(stpSegement.getData());
            this.send(new byte[0],false,false,this.seq,this.ack);
        }
        this.udpSocket.close();

    }

    public void getConnection() throws IOException {
        buffer = new byte[STPheaderSize];//get the first and third handshake segement
        DatagramPacket rcvPacket = new DatagramPacket(buffer,buffer.length);
        while (true){
            this.udpSocket.receive(rcvPacket);
            STPsegement stpSegement= new STPsegement(buffer);
            if(stpSegement.getSYN()){//get the first handshake
                this.ack = stpSegement.getSeq()+1;
                this.sender_ip = rcvPacket.getAddress().getHostName();
                this.sender_port = rcvPacket.getPort();
                break;
            }
        }

        this.send(new byte[0], true, false,this.seq,this.ack);//sendSYN the second handshake
        this.udpSocket.receive(rcvPacket);//get the third handshake
        this.isSYNed = true;

        File file = new File(this.filename);
        //create file for storing
        file.createNewFile();

    }

    public void killConnection(STPsegement FINsegement) throws IOException {
        this.ack = FINsegement.getSeq()+1;
        send(new byte[0],false,true,this.seq,this.ack);
        DatagramPacket endPacket = new DatagramPacket(buffer,buffer.length);
        this.udpSocket.receive(endPacket);
        System.out.println("end");
        this.udpSocket.close();
    }

    public void send(byte[] data, boolean isSYN, boolean isFIN , int seq, int ack) throws IOException {//send method
        STPsegement stpSegement = new STPsegement(data,isSYN,isFIN,seq,ack);
        DatagramPacket outPacket = new DatagramPacket(stpSegement.getByteArray(),
                stpSegement.getByteArray().length,InetAddress.getByName(this.sender_ip),this.sender_port);
        this.udpSocket.send(outPacket);
        this.seq+=1;
        System.out.println("seq:"+seq+" "+"ack:"+ack);

    }
}
