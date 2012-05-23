package de.tuchemnitz.ce.rfid;

import de.tuchemnitz.ce.rfid.datatypes.EPCChangeType;
import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;
import de.tuchemnitz.ce.rfid.events.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 16.05.12
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class EPCTimeoutList{

    private List<EPCEvent> events = new ArrayList<EPCEvent>();
    private List<EPCListChangeListener> listeners = Collections.synchronizedList(new ArrayList<EPCListChangeListener>());

    public void addReadEvent(String epc, int antenna){
            addReadEvent(new EPCEvent(epc, antenna));
    }
    
    public Boolean containsEPC(String epc){
        for (EPCEvent event: events){
            if(event.getEpc().equals(epc))
                return true;
        }
        return false;
    }
    
    public EPCEvent getEPCEventByEPC(String epc){
        for (EPCEvent event: events){
            if(event.getEpc().equals(epc))
                return event;
        }
        return null;
    }

    
    public void addChangeListener(EPCListChangeListener listener){
        this.listeners.add(listener);
    }
    
    private void fireChangeEvent(EPCEvent event, EPCChangeType type){
        for(EPCListChangeListener listener : listeners){
            listener.onChange(new EPCChangeEvent(type, event));
        }
    }

    public List<EPCEvent> getList() {
        return events;
    }



    public synchronized void addReadEvent(EPCEvent epcEvent){

        if(this.containsEPC(epcEvent.getEpc())){
            getEPCEventByEPC(epcEvent.getEpc()).newRead();
            fireChangeEvent(epcEvent, EPCChangeType.RE_ADD);
        }
        else{

            epcEvent.addListener(new EPCKillEventListener() {
                @Override
                public void killEPC(EPCEvent e) {
                    synchronized (events){
                        events.remove(e);
                        fireChangeEvent(e, EPCChangeType.DELETE);
                    }
                }
            });
            events.add(epcEvent);
            fireChangeEvent(epcEvent, EPCChangeType.ADD);
        }
    }
}
