package de.tuchemnitz.ce.rfid.timers;

import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;

import java.util.TimerTask;

/**
 * User: rbs
 * Date: 17.05.12
 */
public class EPCKillTask extends TimerTask{

    private EPCEvent event;

    public EPCKillTask(EPCEvent event) {
        this.event = event;
    }

    public void run() {
            event.fireKillEvent();
        }
}
