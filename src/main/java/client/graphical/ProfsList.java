package client.graphical;

import client.Application;
import client.logic.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ProfsList extends PanelTemplate {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 16);

    private User userLoggedIn;

    private String[] tableColumnNames = {"Name", "College", "Phone No.", "Type", "Email Address", "Room Number"};
    private Object[][] tableContents = null;
    private JTable courseTable;
    private JScrollPane scrollPane;

    private JLabel nameLabel;
    private JTextField nameFilter;
    private JLabel typeLabel;
    private JComboBox<String> typeFilter;
    private JLabel collegeLabel;
    private JComboBox<String> collegeFilter;
    private JButton filterButton;

    private String profName = "";
    private CollegeType college = CollegeType.ALL;
    private ProfessorType type = ProfessorType.ALL;

    private final int SPACE_SIZE = 30;
    private final int LABEL_WIDTH = 150;
    private final int LABEL_HEIGHT = 30;

    public ProfsList(Application app, String userID){
        super(app, userID);
        this.setLayout(null);
    }

    public int getCoor(double x){
        return (int)(SPACE_SIZE * (x + 1) + LABEL_HEIGHT * 2 * x);
    }

    public void addFilters(){
        addNameFilter();
        addTypeFilter();
        addCollegeFilter();
        addFilterButton();
    }

    public void getFilterText() {
        profName = nameFilter.getText();
        college = CollegeType.values()[collegeFilter.getSelectedIndex()];
        type = ProfessorType.values()[typeFilter.getSelectedIndex()];
    }
    public void askForTable(){
        String message = "";
        if (!profName.equals(""))
            message += "/lastName=\"" + profName + "\"";
        if (college != CollegeType.ALL)
            message += "/college=\"" + college + "\"";
        if (type != ProfessorType.ALL)
            message += "/type=\"" + type + "\"";
        if (message.equals(""))
            message += "/";
        app.askForInfo(4, message.substring(1));
    }

    public void addTable(String info){
        fillTable(info);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", "", ""}};
        this.courseTable = new JTable(this.tableContents, this.tableColumnNames);
        this.courseTable.setDefaultEditor(Object.class, null);
        this.courseTable.setRowHeight(40);
        int[] columnWidths = {200, 100, 100, 100, 250, 50};
        TableColumnModel model = this.courseTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++){
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new WrappableTableRenderer() );
        }
        this.courseTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.courseTable);
        this.scrollPane.setBounds(200, 0, 800, 600);
        this.add(this.scrollPane);
    }

    public void addNameFilter(){
        this.nameLabel = new JLabel("Last Name:");
        this.nameLabel.setFont(normalPlainFont);
        this.nameLabel.setBounds(SPACE_SIZE, getCoor(1), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(nameLabel);

        this.nameFilter = new JTextField();
        this.nameFilter.setBounds(SPACE_SIZE, getCoor(1) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.add(nameFilter);
    }

    public void addTypeFilter(){
        this.typeLabel = new JLabel("Type:");
        this.typeLabel.setFont(normalPlainFont);
        this.typeLabel.setBounds(SPACE_SIZE, getCoor(2), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(typeLabel);

        this.typeFilter = new JComboBox<>(ProfessorType.list());
        this.typeFilter.setBounds(SPACE_SIZE, getCoor(2) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.typeFilter.setSelectedIndex(0);
        this.add(typeFilter);
    }

    public void addCollegeFilter(){
        this.collegeLabel = new JLabel("College:");
        this.collegeLabel.setFont(normalPlainFont);
        this.collegeLabel.setBounds(SPACE_SIZE, getCoor(0), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(collegeLabel);

        this.collegeFilter = new JComboBox<>(CollegeType.list());
        this.collegeFilter.setBounds(SPACE_SIZE, getCoor(0) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.collegeFilter.setSelectedIndex(0);
        this.add(collegeFilter);
    }

    public void addFilterButton(){
        this.filterButton = new JButton("Filter");
        this.filterButton.setBounds(SPACE_SIZE, getCoor(6), LABEL_WIDTH, 2 * LABEL_HEIGHT);
        this.filterButton.setBackground(Color.GREEN);
        this.filterButton.addActionListener(new FilterListener());
        this.add(this.filterButton);
    }

    public void fillTable(String info){
        ArrayList<String[]> rows = new ArrayList<>();
        ArrayList<Professor> courses = (ArrayList<Professor>) app.unpackProfessorList(info);
        if (courses == null) {
            this.tableContents = null;
            return;
        }
        for (Professor professor : courses) {
            String[] row = {
                    professor.giveName(),
                    String.valueOf(professor.getCollege()),
                    professor.getPhoneNumber(),
                    String.valueOf(professor.getType()),
                    professor.getEmailAddress(),
                    String.valueOf(professor.getRoomNumber())
            };
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    @Override
    public void refreshPanel(String info) {
        this.removeAll();
        addFilters();
        addTable(info);
    }

    public class WrappableTableRenderer extends JTextArea implements TableCellRenderer{
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

    public class FilterListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            getFilterText();
            askForTable();
        }
    }
}
