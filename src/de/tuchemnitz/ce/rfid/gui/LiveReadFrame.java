package de.tuchemnitz.ce.rfid.gui;

import de.tuchemnitz.ce.rfid.EPCTimeoutList;
import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;
import de.tuchemnitz.ce.rfid.events.EPCChangeEvent;
import de.tuchemnitz.ce.rfid.events.EPCListChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: rbs
 * Date: 17.05.12
 */
public class LiveReadFrame extends JFrame{

    private EPCTimeoutList epcEvents;



    public LiveReadFrame(final EPCTimeoutList epcEvents) {

        this.epcEvents = epcEvents;

        final DefaultTableModel readingModel = new DefaultTableModel();
        
        epcEvents.addChangeListener(new EPCListChangeListener() {
            @Override
            public void onChange(EPCChangeEvent event) {
                synchronized (epcEvents){
                    final EPCTimeoutList sortList = epcEvents;
                    Collections.sort(sortList.getList(), new Comparator<EPCEvent>() {
                        @Override
                        public int compare(EPCEvent o1, EPCEvent o2) {
                            return o1.getLast_read().compareTo(o2.getLast_read());
                        }
                    });
                    synchronized (readingModel){
                        synchronized (sortList){

                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                            readingModel.setRowCount(0); //clear Table
                                            for(EPCEvent event : sortList.getList())
                                                readingModel.addRow(new String[]{event.getEpc(), event.getLast_read().toString(), "" + event.getAntenna()});
                                        }
                            });
                        }
                    }
                }

            }
        });

        this.setSize(new Dimension(1000, 1000));
        this.setLocation(1000, 1000);

        
        JTable liveReading = new JTable(readingModel);
        readingModel.addColumn("EPC");
        readingModel.addColumn("antenna");
        readingModel.addColumn("last read");


        this.setLayout(new BorderLayout());
        JScrollPane livePane = new JScrollPane(liveReading);
        this.add(livePane, BorderLayout.CENTER);

        this.setVisible(true);
    }


    
}
