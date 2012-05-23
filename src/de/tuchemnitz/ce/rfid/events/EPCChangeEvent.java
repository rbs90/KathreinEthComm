package de.tuchemnitz.ce.rfid.events;

import de.tuchemnitz.ce.rfid.datatypes.EPCChangeType;
import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;

/**
 * User: rbs
 * Date: 18.05.12
 */
public class EPCChangeEvent {

    private EPCChangeType type;
    private EPCEvent event;

    public EPCChangeEvent(EPCChangeType type, EPCEvent event) {
        this.type = type;
        this.event = event;
    }

    public EPCChangeType getType() {
        return type;
    }

    public EPCEvent getEvent() {
        return event;
    }
}
