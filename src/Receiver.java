import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class Receiver {
    public static void main(String args[]){
        ReceiverExecutor re = null;
        try {
            re = new ReceiverExecutor(args);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            re.go();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
