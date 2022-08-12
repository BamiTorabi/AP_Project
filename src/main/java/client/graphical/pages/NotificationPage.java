package client.graphical.pages;

import client.Application;
import client.graphical.templates.PanelTemplate;
import client.graphical.templates.WrappableTableRenderer;
import client.logic.Notif;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationPage extends PanelTemplate {

    private String[] tableColumnNames = {"#", "When", "Title", "Message", "Seen?"};
    private Object[][] tableContents = null;
    private Map<JCheckBox, Integer> boxes;
    private JTable notifTable;
    private JScrollPane scrollPane;
    private List<Notif> notifs;

    public NotificationPage(Application app, String userID){
        super(app, userID);
        this.setLayout(null);
    }

    public void addTable(String info) {
        fillTable(info);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", ""}};
        this.notifTable = new JTable(this.tableContents, this.tableColumnNames);
        this.notifTable.setRowHeight(40);
        int[] columnWidths = {50, 150, 150, 600, 50};
        TableColumnModel model = this.notifTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new WrappableTableRenderer());
        }
        for (int i = 0; i < this.tableContents.length; i++){

        }
        this.notifTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.notifTable);
        this.scrollPane.setBounds(0, 0, WIDTH, HEIGHT);
        this.add(this.scrollPane);
    }

    public void fillTable(String info){
        ArrayList<Object[]> rows = new ArrayList<>();
        notifs = app.unpackNotifList(info);
        this.boxes = new HashMap<>();
        if (notifs == null) {
            this.tableContents = null;
            return;
        }
        for (int i = 0; i < notifs.size(); i++){
            Notif notif = notifs.get(i);
            JCheckBox box = new JCheckBox();
            box.setSelected(notif.isSeen());
            box.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox box = (JCheckBox) e.getSource();
                    boolean seen = box.isSelected();
                    app.messageRead(10, String.valueOf(notifs.get(boxes.get(box)).getID()), seen);
                }
            });
            this.boxes.put(box, i);
            Object[] row = {
                    i + 1,
                    notif.getSent(),
                    notif.getTitle(),
                    notif.getMessage(),
                    ""
            };
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    @Override
    public void refreshPanel(String info) {
        this.removeAll();
        addTable(info);
    }


}
