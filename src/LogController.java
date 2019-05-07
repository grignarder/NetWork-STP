/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class LogController {
    int count;
    String filename;
    public LogController(String filename){
        this.filename = filename;
        this.count = 0;
    }
    public void write(){
        this.count++;
    }
}