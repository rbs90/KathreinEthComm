package de.tuchemnitz.ce.rfid.datatypes;

import de.tuchemnitz.ce.rfid.events.EPCKillEventListener;
import de.tuchemnitz.ce.rfid.timers.EPCKillTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 16.05.12
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class EPCEvent {

    private Date last_read;
    private String epc;
    private int antenna;

    ArrayList<EPCKillEventListener> listeners = new ArrayList<EPCKillEventListener>();
    private Timer timer;

    private EPCKillTask actualTask;

    public EPCEvent(String epc, int antenna) {
        this.epc = epc;
        this.antenna = antenna;
        last_read = new Date();
        timer = new Timer();
        actualTask = new EPCKillTask(this);
        timer.schedule(actualTask, 10000);
    }

    public void addListener(EPCKillEventListener listener){
        listeners.add(listener);
    }

    public void fireKillEvent(){
        for (EPCKillEventListener listener : listeners){
            listener.killEPC(this);
        }
    }

    public void newRead(){
        last_read = new Date();
        actualTask.cancel();
        actualTask = new EPCKillTask(this);
        timer.schedule(actualTask, 10000);
    }

    public Date getLast_read() {
        return last_read;
    }

    public String getEpc() {
        return epc;
    }

    public int getAntenna() {
        return antenna;
    }
}
