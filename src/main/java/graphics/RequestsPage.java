package graphics;

import process.College;
import process.Request;
import process.User;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RequestsPage extends JPanel {

    private User userLoggedIn;

    private String[] tableHeaders = {"Request ID", "From", "To", "Text", "Time sent", "Status"};
    private Object[][] tableContents = null;
    private JTable requestTable;
    private JScrollPane scrollPane;

    private JButton newRequestButton = new JButton();

    public RequestsPage(User user){
        super();
        this.userLoggedIn = user;
        this.setLayout(null);

        fillTable();
        addTable();

        if (this.userLoggedIn.isStudent())
            addNewRequestButton();

    }

    public void fillTable(){
        College college = College.getInstance(this.userLoggedIn.getCollege());
        List<Request> requests = null;
        if (requests == null || requests.isEmpty())
            return;
        List<String[]> rows = new ArrayList<>();
        for (Request request : requests){
            if (    (this.userLoggedIn.isStudent() && this.userLoggedIn.getUniversityID().equals(request.getStudentID())) ||
                    (!this.userLoggedIn.isStudent() && this.userLoggedIn.getUniversityID().equals(request.getProfID()))) {
                String[] row = {
                        request.getRequestID(),
                        request.getStudentID(),
                        request.getProfID(),
                        request.getText(),
                        request.getTimeSent(),
                        request.getStatus().toString()
                };
                rows.add(row);
            }
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    public void addTable(){
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", "", ""}};
        this.requestTable = new JTable(this.tableContents, this.tableHeaders);
        this.requestTable.setDefaultEditor(Object.class, null);
        this.requestTable.setRowHeight(40);
        int[] columnWidths = {100, 150, 150, 350, 150, 100};
        TableColumnModel model = this.requestTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++){
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new RequestsPage.WrappableTableRenderer() );
        }
        this.requestTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.requestTable);
        this.scrollPane.setBounds(0, 0, 1000, 600);
        this.add(this.scrollPane);
    }

    public class WrappableTableRenderer extends JTextArea implements TableCellRenderer {
        WrappableTableRenderer(){
            setLineWrap(true);
            setWrapStyleWord(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) < getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }

    public void addNewRequestButton(){
        this.newRequestButton.setText("New Request");
        this.newRequestButton.setBackground(Color.GREEN);
        this.newRequestButton.setBounds(350, 610, 300, 50);
        this.add(this.newRequestButton);
    }

}
