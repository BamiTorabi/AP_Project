package client.graphical;

import client.Application;
import client.logic.ClassLevel;
import client.logic.CollegeType;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CoursesList extends PanelTemplate {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 16);


    private String[] tableColumnNames = {"Number", "Name", "Professor", "Units", "Enrolled", "Class Time", "Exam Time"};
    private Object[][] tableContents = null;
    private JTable courseTable;
    private JScrollPane scrollPane;

    private JLabel idLabel;
    private JTextField idFilter;
    private JLabel unitsLabel;
    private JTextField unitsFilter;
    private JLabel typeLabel;
    private JComboBox<String> typeFilter;
    private JLabel collegeLabel;
    private JComboBox<String> collegeFilter;
    private JButton filterButton;

    private JButton deputyAddButton = new JButton();
    private JButton deputyEditButton = new JButton();

    private String classID = "";
    private CollegeType college = CollegeType.ALL;
    private ClassLevel level = ClassLevel.ALL;
    private int units = 0;

    private final int SPACE_SIZE = 30;
    private final int LABEL_WIDTH = 150;
    private final int LABEL_HEIGHT = 30;

    public CoursesList(Application app, String userID) {
        super(app, userID);
        this.setLayout(null);

        /*if (!this.userLoggedIn.isStudent() && ((Professor) this.userLoggedIn).isDeputy()){
            addDeputyButtons();
        }*/
    }

    public void updatePage(){
        addTable(classID, college, level, units);
    }

    public int getCoor(double x) {
        return (int) (SPACE_SIZE * (x + 1) + LABEL_HEIGHT * 2 * x);
    }

    public void updateTable(){
        classID = this.idFilter.getText();
        college = CollegeType.values()[this.collegeFilter.getSelectedIndex()];
        level = ClassLevel.values()[this.typeFilter.getSelectedIndex()];
        remove(this.scrollPane);
        String unitsFilterText = this.unitsFilter.getText();
        try {
            units = Integer.parseInt(unitsFilterText);
            addTable(classID, college, level, units);
            app.repaintApp();
        } catch (Exception err) {
            if (!unitsFilterText.equals("")) {
                JOptionPane.showMessageDialog(CoursesList.this, "Please enter a number for the units field, or leave it empty.");
            } else {
                addTable(classID, college, level, 0);
                app.repaintApp();
            }
        }
    }

    public void addFilters() {
        addIDFilter();
        addUnitsFilter();
        addTypeFilter();
        addCollegeFilter();
        addFilterButton();
    }

    public void addTable(String id, CollegeType college, ClassLevel level, int units) {
        fillTable(id, college, level, units);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", "", "", ""}};
        this.courseTable = new JTable(this.tableContents, this.tableColumnNames);
        this.courseTable.setDefaultEditor(Object.class, null);
        this.courseTable.setRowHeight(40);
        int[] columnWidths = {80, 150, 150, 40, 80, 150, 100};
        TableColumnModel model = this.courseTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new WrappableTableRenderer());
        }
        this.courseTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.courseTable);
        this.scrollPane.setBounds(200, 0, 800, 600);
        this.add(this.scrollPane);
    }

    public void addDeputyButtons(){
        /*
        this.deputyAddButton.setText("Add Course");
        this.deputyAddButton.setBounds(250, 610, 300, 50);
        this.deputyAddButton.setBackground(Color.CYAN);
        this.deputyAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCourseDialog dialog = new AddCourseDialog(userLoggedIn, null);
                remove(scrollPane);
                updateTable();
                Application.getInstance().repaintApp();
            }
        });
        this.add(this.deputyAddButton);

        this.deputyEditButton.setText("Edit Course");
        this.deputyEditButton.setBounds(650, 610, 300, 50);
        this.deputyEditButton.setBackground(Color.BLUE);
        this.deputyEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = JOptionPane.showInputDialog(CoursesList.this, "Enter course ID: ");
                Course course = University.getCourse(id);
                if (course == null || course.getCollegeLinked() != userLoggedIn.getCollege()){
                    JOptionPane.showMessageDialog(CoursesList.this, "Invalid course ID.");
                    return;
                }
                AddCourseDialog dialog = new AddCourseDialog(userLoggedIn, course);
                updateTable();
            }
        });
        this.add(this.deputyEditButton);
         */
    }

    public void addIDFilter() {
        this.idLabel = new JLabel("Course number:");
        this.idLabel.setFont(normalPlainFont);
        this.idLabel.setBounds(SPACE_SIZE, getCoor(1), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(idLabel);

        this.idFilter = new JTextField();
        this.idFilter.setBounds(SPACE_SIZE, getCoor(1) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.add(idFilter);
    }

    public void addUnitsFilter() {
        this.unitsLabel = new JLabel("No. of units:");
        this.unitsLabel.setFont(normalPlainFont);
        this.unitsLabel.setBounds(SPACE_SIZE, getCoor(3), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(unitsLabel);

        this.unitsFilter = new JTextField();
        this.unitsFilter.setBounds(SPACE_SIZE, getCoor(3) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.add(unitsFilter);
    }

    public void addTypeFilter() {
        this.typeLabel = new JLabel("Type:");
        this.typeLabel.setFont(normalPlainFont);
        this.typeLabel.setBounds(SPACE_SIZE, getCoor(2), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(typeLabel);

        this.typeFilter = new JComboBox<>(ClassLevel.list());
        this.typeFilter.setBounds(SPACE_SIZE, getCoor(2) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.typeFilter.setSelectedIndex(0);
        this.add(typeFilter);
    }

    public void addCollegeFilter() {
        this.collegeLabel = new JLabel("College:");
        this.collegeLabel.setFont(normalPlainFont);
        this.collegeLabel.setBounds(SPACE_SIZE, getCoor(0), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(collegeLabel);

        this.collegeFilter = new JComboBox<>(CollegeType.list());
        this.collegeFilter.setBounds(SPACE_SIZE, getCoor(0) + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        this.collegeFilter.setSelectedIndex(0);
        this.add(collegeFilter);
    }

    public void addFilterButton() {
        this.filterButton = new JButton("Filter");
        this.filterButton.setBounds(SPACE_SIZE, getCoor(6), LABEL_WIDTH, 2 * LABEL_HEIGHT);
        this.filterButton.setBackground(Color.GREEN);
        this.filterButton.addActionListener(new FilterListener());
        this.add(this.filterButton);
    }

    public void fillTable(String id, CollegeType college, ClassLevel level, int units) {
        /*List<String[]> rows = new ArrayList<>();
        for (CollegeType type : CollegeType.values()) {
            if (college == CollegeType.ALL || college == type) {
                College departmentChosen = College.getInstance(type);
                for (Course course : departmentChosen.getCourseList()) {
                    if (
                            (level == ClassLevel.ALL || level == course.getLevel()) &&
                                    (units == 0 || units == course.getUnits()) &&
                                    (id.equals("") || id.equals(course.getCourseID()))
                    ) {
                        String[] row = {
                                course.getCourseID(),
                                course.getCourseName(),
                                departmentChosen.getUser(course.getProfessorID()).giveName(),
                                Integer.toString(course.getUnits()),
                                Integer.toString(course.getScoreList().size()),
                                course.getClassTimeStrings(),
                                course.getExamTime()
                        };
                        rows.add(row);
                    }
                }
            }
        }
        this.tableContents = rows.toArray(new Object[0][]);*/
    }

    @Override
    public void refreshPanel(String info) {

    }

    public class WrappableTableRenderer extends JTextArea implements TableCellRenderer {
        WrappableTableRenderer() {
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

    public class FilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateTable();
        }
    }
}
