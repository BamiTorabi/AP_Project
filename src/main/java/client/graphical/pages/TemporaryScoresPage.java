package client.graphical.pages;

import client.Application;
import client.DataLoader;
import client.graphical.templates.PanelTemplate;
import client.graphical.templates.WrappableTableRenderer;
import client.graphical.dialogs.ConfirmScoreDialog;
import client.logic.Score;
import client.logic.ScoreStatus;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TemporaryScoresPage extends PanelTemplate {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 16);

    private String[] tableColumnNames = {"Course", "Professor", "Student", "Score", "Protest", "Answer", "Status"};
    private Object[][] tableContents = null;
    private JTable courseTable;
    private JScrollPane scrollPane;
    private JButton saveButton = new JButton();
    private JButton confirmButton = new JButton();
    private List<Score> scoreList = new ArrayList<>();

    private final int BUTTON_WIDTH = DataLoader.getConstraint("tablePanel", "buttonWidth");
    private final int BUTTON_HEIGHT = DataLoader.getConstraint("tablePanel", "buttonHeight");
    private final int BUTTON_SPACE = DataLoader.getConstraint("tablePanel", "buttonSpace");
    private final int TABLE_HEIGHT = DataLoader.getConstraint("tablePanel", "tableHeight");
    private final int MARGIN_SIZE = DataLoader.getConstraint("tablePanel", "marginSize");

    public TemporaryScoresPage(Application app, String userID){
        super(app, userID);
        this.setLayout(null);
    }

    public void addTable(String info) {
        fillTable(info);
        if (this.tableContents == null || this.tableContents.length == 0)
            this.tableContents = new Object[][]{{"", "", "", "", "", "", ""}};
        this.courseTable = new JTable(this.tableContents, this.tableColumnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                if (app.getUserLoggedIn().getUserType().equals("Student")){
                    if (column == 4 && scoreList.get(row).getStatus() == ScoreStatus.TEMPORARY)
                        return true;
                }
                else{
                    if (    (column == 3 && scoreList.get(row).getStatus() != ScoreStatus.CONFIRMED) ||
                            (column == 5 && scoreList.get(row).getStatus() == ScoreStatus.PROTESTED))
                        return true;
                }
                return false;
            }
        };
        this.courseTable.setRowHeight(40);
        int[] columnWidths = {125, 125, 125, 100, 225, 225, 75};
        TableColumnModel model = this.courseTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            model.getColumn(i).setPreferredWidth(columnWidths[i]);
            model.getColumn(i).setCellRenderer(new WrappableTableRenderer());
        }
        this.courseTable.getTableHeader().setResizingAllowed(false);
        this.scrollPane = new JScrollPane(this.courseTable);
        this.scrollPane.setBounds(0, 0, WIDTH, TABLE_HEIGHT);
        this.add(this.scrollPane);
    }

    public void fillTable(String info) {
        this.scoreList = app.unpackScoreList(info);
        List<String[]> rows = new ArrayList<>();
        for (Score score : this.scoreList){
            String[] row = {
                    score.getCourseLinked() + "\n" + score.getCourseName(),
                    score.getProfessorName(),
                    score.getStudentLinked() + "\n" + score.getStudentName(),
                    String.valueOf(score.getValue()),
                    score.getStudentProtest(),
                    score.getProfessorAnswer(),
                    String.valueOf(score.getStatus())
            };
            rows.add(row);
        }
        this.tableContents = rows.toArray(new Object[0][]);
    }

    public void addSaveButton(){
        this.saveButton = new JButton();
        this.saveButton.setText("Save");
        this.saveButton.setBackground(Color.GREEN);
        if (app.getUserLoggedIn().getUserType().equals("Student"))
            this.saveButton.setBounds((WIDTH - BUTTON_WIDTH) / 2, HEIGHT - MARGIN_SIZE - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        else
            this.saveButton.setBounds((WIDTH - BUTTON_SPACE) / 2 - BUTTON_WIDTH, HEIGHT - MARGIN_SIZE - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.saveButton.addActionListener(new SaveListener());
        this.add(this.saveButton);
    }

    public void addConfirmButton(){
        this.confirmButton = new JButton();
        this.confirmButton.setText("Confirm");
        this.confirmButton.setBackground(Color.CYAN);
        this.confirmButton.setBounds((WIDTH + BUTTON_SPACE) / 2, HEIGHT - MARGIN_SIZE - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfirmScoreDialog dialog = new ConfirmScoreDialog(app, scoreList);
            }
        });
        this.add(this.confirmButton);
    }

    @Override
    public void refreshPanel(String info) {
        this.removeAll();
        addSaveButton();
        if (!app.getUserLoggedIn().getUserType().equals("Student"))
            addConfirmButton();
        addTable(info);
    }

    public class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableModel model = courseTable.getModel();
            for (int i = 0; i < scoreList.size(); i++){
                Score score = scoreList.get(i);
                try{
                    double num = Double.parseDouble((String) model.getValueAt(i, 3));
                    if (num < 0 || num > 20){
                        JOptionPane.showMessageDialog(TemporaryScoresPage.this, "Please enter a valid number between 0 to 20 as the score.");
                        return;
                    }
                    score.setValue(num);
                } catch (Exception err){
                    JOptionPane.showMessageDialog(TemporaryScoresPage.this, "Please enter a valid number between 0 to 20 as the score.");
                    return;
                }
                String studentProtest = (String) model.getValueAt(i, 4);
                score.setStudentProtest(studentProtest);
                String professorAnswer = (String) model.getValueAt(i, 5);
                score.setProfessorAnswer(professorAnswer);

                if (score.getStatus() != ScoreStatus.CONFIRMED) {
                    if (score.getValue() == 0){
                        score.setStatus(ScoreStatus.PENDING);
                    }
                    else {
                        if (studentProtest.equals("")) {
                            score.setStatus(ScoreStatus.TEMPORARY);
                        } else {
                            if (professorAnswer.equals("")) {
                                score.setStatus(ScoreStatus.PROTESTED);
                            } else {
                                score.setStatus(ScoreStatus.ANSWERED);
                            }
                        }
                    }
                }
                app.updateScore(score);
            }
        }
    }
}
