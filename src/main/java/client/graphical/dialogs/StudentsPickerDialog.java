package client.graphical.dialogs;

import client.Application;
import client.DataLoader;
import client.graphical.templates.DialogTemplate;
import client.graphical.templates.WrappableTableRenderer;
import client.logic.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class StudentsPickerDialog extends DialogTemplate {

    private JButton profileButton;
    private JButton reportCardButton;
    private JLabel userIDLabel;
    private JTextField userIDField;

    private String[] tableColumnNames = {"ID", "Name"};
    private Object[][] tableContents = null;
    private JTable userTable;
    private JScrollPane scrollPane;

    private int WIDTH = DataLoader.getConstraint("dialogs", "width");
    private int HEIGHT = DataLoader.getConstraint("dialogs", "height");
    private int MARGIN_SIZE = DataLoader.getConstraint("dialogs", "marginSize");
    private int ROW_HEIGHT = DataLoader.getConstraint("dialogs", "rowHeight");
    private int LABEL_WIDTH = DataLoader.getConstraint("dialogs", "labelWidth");
    private int ROW_WIDTH = DataLoader.getConstraint("dialogs", "rowWidth");
    private int BUTTON_WIDTH = DataLoader.getConstraint("dialogs", "buttonWidth");

    public StudentsPickerDialog(Application app, String userID){
        super(app, userID);
        this.setVisible(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLayout(null);
        this.setTitle("Select student");
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {
                app.remove();
                dispose();
            }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        addLabel();
        addButtons();
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * ROW_HEIGHT);
    }

    public void addLabel(){
        this.userIDLabel = new JLabel();
        this.userIDLabel.setText("User ID:");
        this.userIDLabel.setBounds(MARGIN_SIZE, getCoor(0), LABEL_WIDTH, ROW_HEIGHT);
        this.add(this.userIDLabel);

        this.userIDField = new JTextField();
        this.userIDField.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(0), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
        this.userIDField.getDocument().addDocumentListener(new UserIDListener());
        this.add(this.userIDField);
    }

    public void addButtons(){
        this.profileButton = new JButton();
        this.profileButton.setText("Show profile");
        this.profileButton.setBackground(Color.CYAN);
        this.profileButton.setBounds(MARGIN_SIZE, getCoor(12), BUTTON_WIDTH, ROW_HEIGHT);
        this.profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModalityType(ModalityType.MODELESS);
                setVisible(false);
                app.askForInfo(2, userIDField.getText());
            }
        });
        this.add(this.profileButton);

        this.reportCardButton = new JButton();
        this.reportCardButton.setText("Show report card");
        this.reportCardButton.setBackground(Color.BLUE);
        this.reportCardButton.setBounds(WIDTH - MARGIN_SIZE - BUTTON_WIDTH, getCoor(12), BUTTON_WIDTH, ROW_HEIGHT);
        this.reportCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModalityType(ModalityType.MODELESS);
                setVisible(false);
                app.askForInfo(9, userIDField.getText());
            }
        });
        this.add(this.reportCardButton);
    }

    public void addTable(String info) {
        fillTable(info);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", ""}};
        this.userTable = new JTable(this.tableContents, this.tableColumnNames);
        this.userTable.setDefaultEditor(Object.class, null);
        this.userTable.setRowHeight(40);
        int[] columnWidths = {100, 300};
        TableColumnModel model = this.userTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new WrappableTableRenderer());
        }
        this.userTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.userTable);
        this.scrollPane.setBounds(MARGIN_SIZE, getCoor(1), WIDTH - 2 * MARGIN_SIZE, getCoor(11) - getCoor(1));
        this.add(this.scrollPane);
    }

    public void fillTable(String info) {
        if (this.scrollPane != null)
            remove(this.scrollPane);
        ArrayList<User> users = (ArrayList<User>) app.unpackUserList(info);
        ArrayList<String[]> rows = new ArrayList<>();
        if (users == null)
            return;
        for (User user : users){
            String[] row = {user.getUniversityID(), user.getFirstName()};
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    @Override
    public void refreshDialog(String info) {
        this.setVisible(true);
        addTable(info);
        revalidate();
        repaint();
    }

    public class UserIDListener implements DocumentListener{
            @Override
            public void insertUpdate(DocumentEvent e) {
                getNewTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                getNewTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                getNewTable();
            }

            public void getNewTable(){
                app.askForInfo(13, app.getUserID() + "/\"" + userIDField.getText() + "%\"");
            }
    }
}
