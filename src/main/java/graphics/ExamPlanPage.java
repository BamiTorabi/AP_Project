package graphics;

import process.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExamPlanPage extends JPanel {

    private int MARGIN_WIDTH = 100;
    private int MARGIN_HEIGHT = 35;
    private int LABEL_WIDTH = 32;
    private int LABEL_HEIGHT = 95;

    private User userLoggedIn;

    private String[] tableHeaders = {"Course ID", "Course name", "Units", "Professor", "Exam date and time"};
    private Object[][] tableContents = null;
    private JTable examTable;
    private JScrollPane scrollPane;
    
    public ExamPlanPage(User user){
        super();
        this.userLoggedIn = user;
        this.setLayout(null);

        fillTable();
        addTable();
    }

    public int getCoor(double x, boolean isHorizontal){
        if (isHorizontal){
            return (int)(MARGIN_WIDTH + x * LABEL_WIDTH);
        } else{
            return (int)(MARGIN_HEIGHT + x * LABEL_HEIGHT);
        }
    }

    public void fillTable(){
        List<Score> scores = this.userLoggedIn.getCourseList();
        if (scores == null || scores.isEmpty())
            return;
        List<String> sortedScores = new ArrayList<>();
        for (Score score : scores){
            if (score.getStatus() != ScoreStatus.CONFIRMED){
                Course course = University.getCourse(score.getCourseLinked());
                String[] timeSplit = course.getExamTime().split("[\\W]");
                String temp = timeSplit[0];
                timeSplit[0] = timeSplit[2];
                timeSplit[2] = temp;
                String reverseTime = "";
                for (String S : timeSplit)
                    reverseTime += S + " ";
                sortedScores.add(reverseTime + course.getCourseID());
            }
        }
        Collections.sort(sortedScores);
        if (sortedScores.isEmpty())
            return;
        List<String[]> rows = new ArrayList<>();
        for (String S : sortedScores){
            Course course = University.getCourse(S.split(" ")[5]);
            String[] row = {
                    course.getCourseID(),
                    course.getCourseName(),
                    Integer.toString(course.getUnits()),
                    University.getUser(course.getProfessorID()).giveName(),
                    course.getExamTime()
            };
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    public void addTable(){
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
