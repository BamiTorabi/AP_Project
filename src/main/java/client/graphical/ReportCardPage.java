package client.graphical;

import client.Application;
import client.logic.CollegeType;
import client.logic.Score;
import client.logic.ScoreStatus;
import client.logic.User;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ReportCardPage extends PanelTemplate {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 16);

    private User chosenStudent = null;

    private String[] tableColumnNames = {"Course ID", "Course name", "Professor", "Units", "Score", "Status"};
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

    public ReportCardPage(Application app, String userID){
        super(app, userID);
        this.setLayout(null);
    }

    public int getCoor(double x){
        return (int)(SPACE_SIZE * (x + 1) + LABEL_HEIGHT * 2 * x);
    }

    public void updateTable(){
        if (app.getUserLoggedIn().getUserType().equals("Student")) {
            String id = courseIDFilter.getText();
            CollegeType college = CollegeType.values()[collegeFilter.getSelectedIndex()];
            remove(scrollPane);
            addTable(id);
            app.repaintApp();
        }
    }

    public void getFilterText(){

    }


    public void addTable(String info){
        fillTable(info);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", "", ""}};
        this.courseTable = new JTable(this.tableContents, this.tableColumnNames);
        this.courseTable.setDefaultEditor(Object.class, null);
        this.courseTable.setRowHeight(40);
        int[] columnWidths = {80, 150, 150, 40, 80, 150};
        TableColumnModel model = this.courseTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++){
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new WrappableTableRenderer() );
        }
        this.courseTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.courseTable);
        this.scrollPane.setBounds(0, 0, 1000, 700);
        this.add(this.scrollPane);
    }

    public void fillTable(String info){
        if (info.equals(""))
            return;
        ArrayList<Score> scores = (ArrayList<Score>) app.unpackScoreList(info);
        List<String[]> rows = new ArrayList<>();
        int totalUnits = 0;
        double totalScore = 0.0;
        for (Score score : scores){
            String[] row = {
                    score.getCourseLinked(),
                    score.getCourseName(),
                    score.getProfessorName(),
                    (score.getStatus() == ScoreStatus.CONFIRMED ? String.valueOf(score.getUnits()) : "N/A"),
                    String.valueOf(score.getValue()),
                    String.valueOf(score.getStatus())
            };
            if (score.getStatus() == ScoreStatus.CONFIRMED) {
                totalUnits += score.getUnits();
                totalScore += score.getUnits() * score.getValue();
            }
            rows.add(row);
        }
        if (totalUnits > 0) {
            totalScore /= 1.0 * totalUnits;
            String[] row = {"TOTAL:", "", "", String.valueOf(totalUnits), String.valueOf(totalScore), ""};
            rows.add(row);
        }
        else{
            String[] row = {"TOTAL:", "", "", "0.0", "N/A", ""};
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    @Override
    public void refreshPanel(String info) {
        this.removeAll();
        addTable(info);
    }

    public class FilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            getFilterText();
            //askForTable();
        }
    }
}
