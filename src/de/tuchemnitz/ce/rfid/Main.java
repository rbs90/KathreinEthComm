package de.tuchemnitz.ce.rfid;

import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;
import de.tuchemnitz.ce.rfid.events.EPCReadEventListener;
import de.tuchemnitz.ce.rfid.gui.LiveReadFrame;
import de.tuchemnitz.ce.rfid.gui.MainFrame;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 02.04.12
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        final EPCTimeoutList epcEvents = new EPCTimeoutList();

        EthConnector ethConnector = new EthConnector();
        ethConnector.start();

        ethConnector.addEPCReadListener(new EPCReadEventListener() {
            @Override
            public void EPCread(EPCEvent e) {
                epcEvents.addReadEvent(e);
            }
        });

        new MainFrame(epcEvents);
        new LiveReadFrame(epcEvents);
        /*
        epcEvents.addReadEvent("696E77657374AA0000000019", 1);
        epcEvents.addReadEvent("696E77657374AA0000000020", 2);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        epcEvents.addReadEvent("696E77657374AA0000000019", 1);
        epcEvents.addReadEvent("696E77657374AA0000000124", 2);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        epcEvents.addReadEvent("696E77657374AA0000001024", 1);
        epcEvents.addReadEvent("696E77657374AA0000000224", 2);
        epcEvents.addReadEvent("696E77657374AA0000003024", 2);
          */
    }
}
