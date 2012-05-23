package de.tuchemnitz.ce.rfid.gui;

import de.tuchemnitz.ce.rfid.EPCTimeoutList;
import de.tuchemnitz.ce.rfid.datatypes.EPCChangeType;
import de.tuchemnitz.ce.rfid.datatypes.EPCEvent;
import de.tuchemnitz.ce.rfid.datatypes.ItemType;
import de.tuchemnitz.ce.rfid.events.EPCChangeEvent;
import de.tuchemnitz.ce.rfid.events.EPCListChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * User: rbs
 * Date: 17.05.12
 */
public class MainFrame extends JFrame{

    private final EPCTimeoutList epcEvents;
    private Connection conn ;
    private Statement statement;

    private ArrayList<String> currentMediaList = new ArrayList<String>();

    public MainFrame(EPCTimeoutList epcEvents) throws HeadlessException {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://78.47.125.58/inwest","inwest","exBSmASnTbT8MVtA");
            statement = conn.createStatement();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection to database failed:\n" + e);
            System.exit(-2);
        }

        this.epcEvents = epcEvents;

        final DefaultTableModel mediaLayout = new DefaultTableModel();
        final JTable media = new JTable(mediaLayout);
        mediaLayout.addColumn("Type");
        mediaLayout.addColumn("Title");
        mediaLayout.addColumn("ISBN");
        mediaLayout.addColumn("Status");

        this.epcEvents.addChangeListener(new EPCListChangeListener() {
            @Override
            public void onChange(EPCChangeEvent event) {
                if(event.getType() == EPCChangeType.ADD){
                    EPCEvent epcEvent = event.getEvent();
                    String item_id = epcEvent.getEpc();

                    try {
                        ItemType itemType = getItemType(item_id);

                        if(itemType == null){
                            System.out.println("IGNORED " + item_id + " - NO TYPE DEFINED!");
                            return;
                        }
                        switch (itemType){
                            case MEDIA:
                                ResultSet resultSet = statement.executeQuery("SELECT * " +
                                        "FROM media " +
                                        "LEFT JOIN item_is_media ON (media.id=item_is_media.media_id) " +
                                        "LEFT JOIN item_status ON (item_status.item_id=item_is_media.item_id) " +
                                        "WHERE item_is_media.item_id = '" + item_id + "'" +
                                        "ORDER BY pot DESC\n" +
                                        "LIMIT 1");
                                if(resultSet.next()){
                                    String name = resultSet.getString("name");
                                    String isbn = resultSet.getString("isbn");
                                    int statustype_id = -1;
                                    if(! resultSet.getString("statustype_id").equals("NULL"))
                                        statustype_id = resultSet.getInt("statustype_id");
                                    mediaLayout.addRow(new String[]{"media", name, isbn, getStatusName(statustype_id)});
                                }
                                break;
                            
                            case ATTACHMENT:
                                ResultSet resultSet2 = statement.executeQuery("SELECT * " +
                                        "FROM attachment " +
                                        "LEFT JOIN item_is_attachment ON ( attachment.id = item_is_attachment.attachment_id ) " +
                                        "LEFT JOIN item_status ON ( item_status.item_id = item_is_attachment.item_id ) " +
                                        "WHERE item_is_attachment.item_id =  '" + item_id + "'" +
                                        "ORDER BY pot DESC \n" +
                                        "LIMIT 1");
                                if(resultSet2.next()){
                                    String name = resultSet2.getString("name");

                                    int statustype_id = -1;
                                    if(! resultSet2.getString("statustype_id").equals("NULL"))
                                    statustype_id = resultSet2.getInt("statustype_id");
                                    mediaLayout.addRow(new String[]{"media", name, "---", getStatusName(statustype_id)});
                                }
                                break;
                            }

                } catch (SQLException e) {
                        e.printStackTrace();
                }
                }
            }
        });
        this.setTitle("Kathrein-GUI");
        this.setSize(new Dimension(500, 500));
        this.setLocation(0,0);

        this.setLayout(new BorderLayout());




        JButton dbButton = new JButton("Database");
        JButton readerButton = new JButton("Reader");
        JButton lendButton = new JButton("Lend");
        JButton receiveButton = new JButton("Receive");
        JButton clearButton = new JButton("Clear");


        JPanel settingsPane = new JPanel();
        settingsPane.add(dbButton);
        settingsPane.add(readerButton);
        JPanel actionPane = new JPanel();
        actionPane.setLayout(new GridLayout(1, 4));
        actionPane.add(lendButton);
        actionPane.add(receiveButton);
        actionPane.add(clearButton);

        JScrollPane mediaPane = new JScrollPane(media);

        this.add(mediaPane, BorderLayout.CENTER);
        this.add(actionPane, BorderLayout.SOUTH);
        this.add(settingsPane, BorderLayout.NORTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    private ItemType getItemType(String item_id) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT COUNT(item_id) FROM item_is_media WHERE item_id = \"" + item_id + "\"");
        if(result.next()){
            if(result.getInt(1) > 0)
                return ItemType.MEDIA;
        }
        result = statement.executeQuery("SELECT COUNT(item_id) FROM item_is_attachment WHERE item_id = " + "\"" + item_id + "\"");
        if(result.next()){
            if(result.getInt(1) > 0)
                return ItemType.MEDIA;
        }
        return null;
    }
        
    private String getStatusName(int status_id){
        switch (status_id){
            case 1: return "Lended";
            case 2: return "Extended";
            case 3: return "in Library";
            case -1: return "in Library (never lend)";
        }
        return null;
    }
}
