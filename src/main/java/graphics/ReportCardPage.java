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

public class ReportCardPage extends JPanel {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 16);
    
    private User userLoggedIn;
    private User chosenStudent = null;

    private String[] tableColumnNames = {"Course ID", "Course name", "Professor", "Units", "Score", "Finalised?"};
    private Object[][] tableContents = null;
    private JTable courseTable;
    private JScrollPane scrollPane;

    private JLabel courseIDLabel;
    private JTextField courseIDFilter;
    private JLabel studentIDLabel;
    private JTextField studentIDFilter;
    private JLabel studentNameLabel;
    private JTextField studentNameFilter;
    private JLabel collegeLabel;
    private JComboBox<String> collegeFilter;
    private JButton filterButton;
    
    private final int SPACE_SIZE = 30;
    private final int LABEL_WIDTH = 150;
    private final int LABEL_HEIGHT = 30;

    public ReportCardPage(User user){
        super();
        this.userLoggedIn = user;
        this.setLayout(null);

        addFilters();
        addTable("", CollegeType.ALL);
    }

    public int getCoor(double x){
        return (int)(SPACE_SIZE * (x + 1) + LABEL_HEIGHT * 2 * x);
    }

    public void updateTable(){
        if (userLoggedIn.isStudent()) {
            String id = courseIDFilter.getText();
            CollegeType college = CollegeType.values()[collegeFilter.getSelectedIndex()];
            remove(scrollPane);
            addTable(id, college);
            Application.getInstance().repaintApp();
        }
    }

    public void addFilters(){
        if (this.userLoggedIn.isStudent()){
            addCourseIDFilter();
            addCollegeFilter();
        }
        addFilterButton();
    }

    public void addTable(String id, CollegeType college){
        if (this.userLoggedIn.isStudent())
            fillTable(id, college, this.userLoggedIn);
        else
            fillTable("", CollegeType.ALL, this.chosenStudent);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", "", ""}};
        this.courseTable = new JTable(this.tableContents, this.tableColumnNames);
        this.courseTable.setDefaultEditor(Object.class, null);
        this.courseTable.setRowHeight(40);
        int[] columnWidths = {80, 150, 150, 40, 80, 150};
        TableColumnModel model = this.courseTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++){
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new ReportCardPage.WrappableTableRenderer() );
        }
        this.courseTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.courseTable);
        this.scrollPane.setBounds(200, 0, 800, 700);
        this.add(this.scrollPane);
    }

    public void addCourseIDFilter(){
        this.courseIDLabel = new JLabel("Course number:");
        this.courseIDLabel.setFont(normalPlainFont);
        this.courseIDLabel.setBounds(SPACE_SIZE, getCoor(0), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(courseIDLabel);

        this.courseIDFilter = new JTextField();
        this.courseIDFilter.setBounds(SPACE_SIZE, getCoor(0) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.add(courseIDFilter);
    }

    public void addCollegeFilter(){
        this.collegeLabel = new JLabel("College:");
        this.collegeLabel.setFont(normalPlainFont);
        this.collegeLabel.setBounds(SPACE_SIZE, getCoor(1), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(collegeLabel);

        this.collegeFilter = new JComboBox<>(CollegeType.list());
        this.collegeFilter.setBounds(SPACE_SIZE, getCoor(1) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.collegeFilter.setSelectedIndex(0);
        this.add(collegeFilter);
    }

    public void addFilterButton(){
        this.filterButton = new JButton("Filter");
        this.filterButton.setBounds(SPACE_SIZE, getCoor(6), LABEL_WIDTH, 2 * LABEL_HEIGHT);
        this.filterButton.setBackground(Color.GREEN);
        this.filterButton.addActionListener(new ReportCardPage.FilterListener());
        this.add(this.filterButton);
    }

    public void fillTable(String id, CollegeType college, User user){
        List<String[]> rows = new ArrayList<>();
        int totalUnits = 0;
        double totalScore = 0.0;
        if (user != null) {
            for (Score score : user.getCourseList()) {
                Course course = University.getCourse(score.getCourseLinked());
                College departmentChosen = College.getInstance(course.getCollegeLinked());
                if (
                        (college == CollegeType.ALL || college == course.getCollegeLinked()) &&
                                (id.equals("") || id.equals(course.getCourseID()))
                ) {
                    String[] row = {
                            course.getCourseID(),
                            course.getCourseName(),
                            departmentChosen.getUser(course.getProfessorID()).giveName(),
                            Integer.toString(course.getUnits()),
                            (score.getStatus() == ScoreStatus.CONFIRMED ? String.valueOf(score.getValue()) : "N/A"),
                            String.valueOf(score.getStatus())
                    };
                    rows.add(row);
                    if (score.getStatus() == ScoreStatus.CONFIRMED) {
                        totalUnits += course.getUnits();
                        totalScore += course.getUnits() * score.getValue();
                    }
                }
            }
        }
        if (totalUnits > 0) {
            totalScore /= 1.0 * totalUnits;
            String[] row = {"TOTAL:", "", "", String.valueOf(totalUnits), String.valueOf(totalScore), ""};
            rows.add(row);
        }
        else{
            String[] row = {"TOTAL:", "", "", "0", "N/A", ""};
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    public class WrappableTableRenderer extends JTextArea implements TableCellRenderer {
        WrappableTableRenderer(){
            setLineWrap(true);
            setWrapStyleWord(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }

    public class FilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateTable();
        }
    }
}
