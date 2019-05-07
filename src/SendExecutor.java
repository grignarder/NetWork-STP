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

    //MSG
    private static String INPUT_ERROR_MSG="usage: java Sender <receiver_host_ip> <receiver_port> <file.txt> <MSS>";


    public SendExecutor(String args[]){
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
            this.inbuffer = new byte[this.MSS+10];
        }catch(Exception e){
            System.out.println(INPUT_ERROR_MSG);
        }
        this.init();//init socket
    }

    public void go(){

        getConnection();//getconnected

        //get the STP segment
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.filename));

            while(bis.read(this.outbuffer,0,this.outbuffer.length)!=-1){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(this.outbuffer,0,this.outbuffer.length);

                STPsegement stpSegemnt = new STPsegement(baos.toByteArray(),false,false,0,0);

                //get the udp packet
                DatagramPacket outPacket = new DatagramPacket(stpSegemnt.getByteArray(),
                        stpSegemnt.getByteArray().length,InetAddress.getByName(this.receiver_host_ip),this.receiver_port);
                //send the packet
                this.udpSocket.send(outPacket);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void init(){
        try {
            this.udpSocket = new DatagramSocket(this.sender_port,InetAddress.getByName(this.sender_ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (SocketException e) {
            e.printStackTrace();
        }
        //init logController
        this.logController = new LogController("src/log.txt");
    }

    public void getConnection(){
//        STPsegement STPforSYN= new STPsegement(new byte[0], true,false,0,0)
    }

    @Override
    public void run() {
        //listen ACK thread

        while(true){
            try {
                DatagramPacket rcvPacket = new DatagramPacket(this.inbuffer,this.inbuffer.length);
                this.udpSocket.receive(rcvPacket);
                System.out.println(new String(rcvPacket.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}

