import java.io.*;
import java.net.*;

/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class SendExecutor implements Runnable{
    //param, no MWS、timeout、pdrop、seed
    private String receiver_host_ip;
    private int receiver_port;
    private String filename;
    private int MSS;

    //other needed attributes
    private byte[] outbuffer;
    private byte[] inbuffer;
    private DatagramSocket udpSocket;
    private String sender_ip = "localhost";
    private int sender_port = 8888;
    private LogController logController;
    private boolean isSYNed = false;
    private int seq = 100;//init seq
    private int ack = 0;
    private int rcvAck = -1;
    private boolean getFined = false;


    //MSG
    private static String INPUT_ERROR_MSG="usage: java Sender <receiver_host_ip> <receiver_port> <file.txt> <MSS>";
    private static int STPheaderSize = 10;


    public SendExecutor(String args[]) throws SocketException, UnknownHostException {
        //read params
        if(args.length!=4){
            System.out.println(INPUT_ERROR_MSG);
        }
        try{
            this.receiver_host_ip = args[0];
            this.receiver_port = Integer.parseInt(args[1]);
            this.filename = args[2];
            this.MSS = Integer.parseInt(args[3]);
            this.outbuffer = new byte[this.MSS];
            this.inbuffer = new byte[this.MSS+STPheaderSize];
        }catch(Exception e){
            System.out.println(INPUT_ERROR_MSG);
        }
        this.init();//init sth
    }

    public void init() throws UnknownHostException, SocketException {
        //init socket
        this.udpSocket = new DatagramSocket(this.sender_port,InetAddress.getByName(this.sender_ip));
        //init logController
        this.logController = new LogController("src/sender_log.txt");
    }

    public void go() throws IOException {
        getConnection();

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.filename));
        while(bis.read(this.outbuffer,0,this.outbuffer.length)!=-1){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(this.outbuffer,0,this.outbuffer.length);
            this.send(baos.toByteArray(),false,false,this.seq,this.ack);
        }
        while(true){
            if(this.rcvAck == this.seq){
                killConnection();
                break;
            }
        }

    }

    public void getConnection() throws IOException {
        this.send(new byte[0],true,false,this.seq,0);//send SYN the first handshake
        while(true){
            synchronized (this){
                if(this.isSYNed){
                    this.seq++;
                    this.send(new byte[0],false,false,this.seq,this.ack);//send the third handshake
                    break;
                }
            }
        }
    }

    public synchronized void send(byte[] data, boolean isSYN, boolean isFIN , int seq, int ack) throws IOException {
        STPsegement stpSegement = new STPsegement(data,isSYN,isFIN,seq,ack);
        DatagramPacket outPacket = new DatagramPacket(stpSegement.getByteArray(),
                stpSegement.getByteArray().length,InetAddress.getByName(this.receiver_host_ip),this.receiver_port);
        this.udpSocket.send(outPacket);
        this.seq+=stpSegement.getDataLength();
        //for debugging.....
        System.out.println("seq:"+seq+" "+"ack:"+ack);
        //for debugging.....
    }

    public void killConnection() throws IOException {
        send(new byte[0],false,true,this.seq,this.ack); //F
        this.seq+=1;
        while(true){
            if(this.getFined){//get F+A
                this.send(new byte[0],false,false,this.seq,this.ack);
                break;
            }
        }
        System.out.println("end");
        this.udpSocket.close();

    }

    @Override
    public void run() {
        //listen ACK thread
        while(!this.getFined){
            try {
                DatagramPacket rcvPacket = new DatagramPacket(this.inbuffer,this.inbuffer.length);
                this.udpSocket.receive(rcvPacket);
                STPsegement rcvSTPsegement = new STPsegement(rcvPacket.getData());
                if(rcvSTPsegement.getSYN()){//if is the second handshake
                    this.isSYNed = true;
                }else if(rcvSTPsegement.getFIN()) {//if is FIN+ACK
                    this.getFined = true;
                } else{//if is normal ACK
                    this.rcvAck = rcvSTPsegement.getAck();
                }
                this.ack = rcvSTPsegement.getSeq()+1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
