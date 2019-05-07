import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class STPsegement {

    //header
    private byte[] header = new byte[10];

    //data
    private byte[] data ;

    public STPsegement(byte[] data, Boolean isSYN, Boolean isFIN, int seq, int ack){
        //init data
        int index = -1;
        for(int i = 0;i<data.length;i++){
            if(data[i]==0){
                index = i;
                break;
            }
        }
        if(index==-1){
            this.data = data;
        }else{
            this.data = new byte[index];
            System.arraycopy(data,0,this.data,0,index);
        }

        //init header
        for(int i = 0; i<10;i++){
            header[i] = 0;
        }
        //SYN
        if(isSYN){
            header[0] = 1;
        }
        //FIN
        if(isFIN){
            header[1] = 1;
        }
        //seq
        header[2] = (byte) (seq>>24);
        header[3] = (byte) (seq>>16);
        header[4] = (byte) (seq>>8);
        header[5] = (byte) (seq);
        //ack
        header[6]=(byte)(ack>>24);
        header[7]=(byte)(ack>>16);
        header[8]=(byte)(ack>>8);
        header[9]=(byte)(ack);

    }

    public STPsegement(byte[] buffer){
        for(int i = 0 ;i<header.length;i++){
            header[i] = buffer[i];
        }
        int index = 0;
        for(int i = header.length;i<buffer.length;i++){
            if(buffer[i]!=0){
                index+=1;
            }
        }
        this.data = new byte[index];
        for(int i = 0;i<index;i++){
            data[i] = buffer[i+header.length];
        }
    }

    public byte[] getData(){
        return this.data;
    }

    public boolean getSYN(){
        return this.header[0] == 1;
    }

    public boolean getFIN(){
        return this.header[1] ==1;
    }

    public int getSeq(){
        int seq = ((header[2]&0xff)<<24) + ((header[3]&0xff)<<16) + ((header[4]&0xff)<<8) + header[5]&0xff;
        return seq;
    }

    public int getAck(){
        int ack = ((header[6]&0xff)<<24) + ((header[7]&0xff)<<16) + ((header[8]&0xff)<<8) + header[9]&0xff;
        return ack;
    }

    public byte[] getByteArray(){
       byte[] out = new byte[header.length+data.length];
       for(int i = 0 ;i<header.length;i++){
           out[i] = header[i];
       }
       for(int i = 0; i<data.length;i++){
           out[i+header.length] = data[i];
       }
       return out;
    }
}
