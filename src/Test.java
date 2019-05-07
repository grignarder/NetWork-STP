/**
 * @author Yang ShengYuan
 * @date 2019/5/6
 * @Description ${DESCRIBE}
 **/
public class Test {
    public static void main(String args[]) {
        int n = 1367;
        byte[] xi = new byte[5];
        xi[0] = (byte) (n>>24);
        xi[1] = (byte) (n>>16);
        xi[2] = (byte) (n>>8);
        xi[3] = (byte) n;
        System.out.println(xi[0]+" "+xi[1]+" "+xi[2]+" "+xi[3]);

        System.out.println((xi[0]&0xff)<<24);
        System.out.println((xi[1]&0xff)<<16);
        System.out.println((xi[2]&0xff)<<8);
        System.out.println((xi[3]&0xff)<<0);


        int a = (int)((xi[2]&0xff)<<8);
        int b = (int)xi[3]&0xff;
        System.out.println(a+b);

        int seq = (int)((xi[0]&0xff)<<24) + (int)((xi[1]&0xff)<<16) + (int)((xi[2]&0xff)<<8) + (int)xi[3]&0xff;
        System.out.println(seq);
    }
}
