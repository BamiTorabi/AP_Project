package graphics;

import process.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ProfsList extends JPanel {

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

    private final int SPACE_SIZE = 30;
    private final int LABEL_WIDTH = 150;
    private final int LABEL_HEIGHT = 30;

    public ProfsList(User user){
        super();
        this.userLoggedIn = user;
        this.setLayout(null);

        addFilters();
        addTable("", CollegeType.ALL, ProfessorType.ALL);
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

    public void updateTable(){
        String name = nameFilter.getText();
        CollegeType college = CollegeType.values()[collegeFilter.getSelectedIndex()];
        ProfessorType type = ProfessorType.values()[typeFilter.getSelectedIndex()];
        remove(scrollPane);
        addTable(name, college, type);
        Application.getInstance().repaintApp();
    }

    public void addTable(String name, CollegeType college, ProfessorType type){
        fillTable(name, college, type);
        if (this.tableContents.length == 0)
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
        this.nameLabel = new JLabel("Name:");
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

    public void fillTable(String name, CollegeType college, ProfessorType profType){
        List<String[]> rows = new ArrayList<>();
        for (CollegeType type : CollegeType.values()) {
            if (college == CollegeType.ALL || college == type) {
                College departmentChosen = College.getInstance(type);
                for (Professor prof : departmentChosen.getProfessorList()) {
                    if (
                            (profType == ProfessorType.ALL || profType == prof.getType()) &&
                            (name.equals("") || name.equals(prof.giveName()) || name.equals(prof.getLastName()))
                    ) {
                        String[] row = {
                            prof.giveName(),
                            prof.getCollege().toString(),
                            prof.getPhoneNumber(),
                            prof.getType().toString(),
                            prof.getEmailAddress(),
                            Integer.toString(prof.getRoomNumber())
                        };
                        rows.add(row);
                    }
                }
            }
        }
        this.tableContents = rows.toArray(new Object[0][]);
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
            updateTable();
        }
    }
}
