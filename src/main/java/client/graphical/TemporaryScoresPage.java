package client.graphical;

import client.Application;
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
        this.scrollPane.setBounds(0, 0, 1000, 600);
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
            this.saveButton.setBounds(350, 610, 300, 50);
        else
            this.saveButton.setBounds(100, 610, 300, 50);
        this.saveButton.addActionListener(new SaveListener());
        this.add(this.saveButton);
    }

    public void addConfirmButton(){
        this.confirmButton = new JButton();
        this.confirmButton.setText("Confirm");
        this.confirmButton.setBackground(Color.CYAN);
        this.confirmButton.setBounds(600, 610, 300, 50);
        this.confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfirmScoreDialog dialog = new ConfirmScoreDialog(app, scoreList);
            }
        });
        this.add(this.confirmButton);
    }

    public void saveScoreChanges(){
        /*if (this.userLoggedIn.isStudent()){
            for (Score score : this.scoreList){
                Course course = University.getCourse(score.getCourseLinked());
                for (int i = 0; i < course.getScoreList().size(); i++){
                    Score courseScore = course.getScoreList().get(i);
                    if (this.userLoggedIn.getUniversityID().equals(courseScore.getStudentLinked())){
                        course.getScoreList().set(i, score);
                        break;
                    }
                }
                Data.writeCourse(course);
                Data.writeToLog(userLoggedIn.getUniversityID(), " SCORE_LIST_UPDATE FOR COURSE " + course.getCourseID());
                for (int i = 0; i < this.userLoggedIn.getCourseList().size(); i++){
                    Score studentScore = this.userLoggedIn.getCourseList().get(i);
                    if (score.getCourseLinked().equals(studentScore.getCourseLinked())){
                        this.userLoggedIn.getCourseList().set(i, score);
                        Data.writeToLog(userLoggedIn.getUniversityID(), " SCORE_UPDATE FOR USER " + score.getStudentLinked());
                        break;
                    }
                }
            }
            Data.writeUser(this.userLoggedIn);
        }
        else{
            for (Score score : this.scoreList){
                Course course = University.getCourse(score.getCourseLinked());
                User student = University.getUser(score.getStudentLinked());
                for (int i = 0; i < course.getScoreList().size(); i++){
                    Score courseScore = course.getScoreList().get(i);
                    if (student.getUniversityID().equals(courseScore.getStudentLinked())){
                        course.getScoreList().set(i, score);
                        break;
                    }
                }
                for (int i = 0; i < student.getCourseList().size(); i++){
                    Score studentScore = student.getCourseList().get(i);
                    if (score.getCourseLinked().equals(studentScore.getCourseLinked())){
                        student.getCourseList().set(i, score);
                        break;
                    }
                }
                Data.writeUser(student);
            }
            for (Score score : this.userLoggedIn.getCourseList()){
                Course course = University.getCourse(score.getCourseLinked());
                Data.writeCourse(course);
                Data.writeToLog(userLoggedIn.getUniversityID(), " SCORE_LIST_UPDATE FOR COURSE " + course.getCourseID());
            }
        }*/
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
