package de.tuchemnitz.ce.rfid;

import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;
import de.tuchemnitz.ce.rfid.events.EPCReadEventListener;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 02.04.12
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class EthConnector extends Thread{

    private InputStreamReader reader;
    private OutputStream writer;

    private ArrayList<EPCReadEventListener> listeners = new ArrayList<EPCReadEventListener>();
    private InputStream inputStream;

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket("192.168.0.1", 4007);
            System.out.println(socket.isConnected());
            inputStream = socket.getInputStream();
            writer = socket.getOutputStream();
            reader = new InputStreamReader(new DataInputStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection failed!");
            System.exit(0);
        }

        System.out.println("connected...");
        ArrayList<Byte> bytes = new ArrayList<Byte>();

        TimerTask transmit = new TimerTask() {

            @Override
            public void run() {
                try {
                    writeHexString("aabb01011101aacc");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        TimerTask rec = new TimerTask() {

            @Override
            public void run() {
                try {
                    String hexResult = getHexStreamString(inputStream);

                    String[] resSplit = hexResult.replaceAll("aabb0700aacc", "").split("aabb0101");
                    for (int i = 0; i < resSplit.length; i++){
                        resSplit[i] = resSplit[i].replaceAll("aacc", "").replaceAll("aaaa", "aa");
                        String curr = resSplit[i];

                        if(curr.length() > 30){
                            //System.out.println("Complete: " + resSplit[i]);
                            String mirroredEPC = "";
                            for (int j = curr.length() - 2; j > (curr.length() - 26);j -= 2){
                                String substring = curr.substring(j, j + 2);
                                //System.out.println("Substring:" + substring);
                                mirroredEPC += substring;
                            }
                            //System.out.println(curr);
                            int antenna = Integer.parseInt(curr.substring(8,10));
                            System.out.println(antenna + ": " + mirroredEPC);
                            fireReadEvent(new EPCEvent(mirroredEPC, antenna));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(transmit, 1000, 2000);
        timer.schedule(rec, 1000, 2000);
    }

    public static String getHexStreamString(final InputStream inputStream) throws IOException{
        String result = "";

        if (inputStream.available() > 0){
            while (inputStream.available() > 0) {
                result += String.format("%02x ",inputStream.read()).trim();
            }
        }

        return result;
    }

    private void writeHexString(String hex) throws IOException {
        writer.write(hexStringToByteArray(hex));
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public void addEPCReadListener(EPCReadEventListener listener){
        listeners.add(listener);
    }

    public void fireReadEvent(EPCEvent event){
        for (EPCReadEventListener listener : listeners){
            listener.EPCread(event);
        }
    }
}
