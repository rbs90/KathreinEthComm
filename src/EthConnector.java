import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 02.04.12
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class EthConnector {
    public EthConnector() throws IOException {
        Socket socket = new Socket("192.168.0.1", 4007);
        System.out.println(socket.isConnected());
        InputStream inputStream = socket.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream ));

        System.out.println("connected...");
        while(true){
            System.out.println(reader.read());
        }


    }
}
