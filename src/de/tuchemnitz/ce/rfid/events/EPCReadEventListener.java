package de.tuchemnitz.ce.rfid.events;

import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 16.05.12
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
public interface EPCReadEventListener {
    public void EPCread(EPCEvent e);
}
