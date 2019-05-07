import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class Sender {
    public static void main(String args[]){
        SendExecutor se = new SendExecutor(args);
        Thread t = new Thread(se);
        t.start();
        se.go();
    }
}
