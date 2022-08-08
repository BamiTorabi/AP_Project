package client.graphical;

import client.Application;
import client.logic.Course;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class ExamPlanPage extends PanelTemplate {

    private int MARGIN_WIDTH = 100;
    private int MARGIN_HEIGHT = 35;
    private int LABEL_WIDTH = 32;
    private int LABEL_HEIGHT = 95;

    private String[] tableHeaders = {"Course ID", "Course name", "Units", "Professor", "Exam date and time"};
    private Object[][] tableContents = null;
    private JTable examTable;
    private JScrollPane scrollPane;
    
    public ExamPlanPage(Application app, String userID){
        super(app, userID);
        this.setLayout(null);
    }

    public int getCoor(double x, boolean isHorizontal){
        if (isHorizontal){
            return (int)(MARGIN_WIDTH + x * LABEL_WIDTH);
        } else{
            return (int)(MARGIN_HEIGHT + x * LABEL_HEIGHT);
        }
    }

    public void fillTable(String info){
        ArrayList<String[]> rows = new ArrayList<>();
        ArrayList<Course> courses = (ArrayList<Course>) app.unpackCourseList(info);
        if (courses == null) {
            this.tableContents = null;
            return;
        }
        for (Course course : courses) {
            String[] row = {
                    course.getCourseID(),
                    course.getCourseName(),
                    String.valueOf(course.getUnits()),
                    course.getProfessorID(),
                    course.getExamTime()
            };
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    public void addTable(String info){
        fillTable(info);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", ""}};
        this.examTable = new JTable(this.tableContents, this.tableHeaders);
        this.examTable.setDefaultEditor(Object.class, null);
        this.examTable.setRowHeight(40);
        int[] columnWidths = {100, 150, 50, 150, 350};
        TableColumnModel model = this.examTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++){
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new ExamPlanPage.WrappableTableRenderer() );
        }
        this.examTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.examTable);
        this.scrollPane.setBounds(0, 0, 1000, 700);
        this.add(this.scrollPane);
    }

    @Override
    public void refreshPanel(String info) {
        this.removeAll();
        addTable(info);
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

}
