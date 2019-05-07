import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class Sender {
    public static void main(String args[]){
        //init sendExecuter
        SendExecutor se = null;
        try {
            se = new SendExecutor(args);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //start listening thread
        Thread t = new Thread(se);
        t.start();
        //start sending
        try {
            se.go();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
