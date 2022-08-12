package client.graphical.dialogs;

import client.Application;
import client.DataLoader;
import client.logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ConfirmScoreDialog extends JDialog {

    private User userLoggedIn;
    private List<Score> scoreList;
    private Application app;
    private Score scoreChosen = null;
    private String[] scoreDetails = {"", "", "", "", "", "", ""};

    private JComboBox<String> scoreComboBox;
    private String[] labelNames = {"Course ID", "Course Name", "Student ID", "Student Name", "Score", "Protest", "Answer"};
    private JLabel[] labels = new JLabel[labelNames.length];
    private JButton confirmButton = new JButton();

    private int WIDTH = DataLoader.getConstraint("dialogs", "width");
    private int HEIGHT = DataLoader.getConstraint("dialogs", "height");
    private int MARGIN_SIZE = DataLoader.getConstraint("dialogs", "marginSize");
    private int ROW_HEIGHT = DataLoader.getConstraint("dialogs", "rowHeight");
    private int LABEL_WIDTH = DataLoader.getConstraint("dialogs", "labelWidth");
    private int ROW_WIDTH = DataLoader.getConstraint("dialogs", "rowWidth");

    public ConfirmScoreDialog(Application app, List<Score> scoreList){
        super();
        this.app = app;
        this.scoreList = scoreList;
        this.setModal(true);
        this.setLayout(null);
        this.setTitle("Confirm Score");
        this.setSize(WIDTH, HEIGHT);

        addScoreComboBox();
        addLabels();
        addConfirmButton();
        this.setVisible(true);
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * ROW_HEIGHT);
    }

    public void addScoreComboBox(){
        String[] choices = new String[this.scoreList.size()];
        for (int i = 0; i < this.scoreList.size(); i++) {
            Score score = this.scoreList.get(i);
            choices[i] = score.getCourseLinked() + " - " + score.getStudentLinked();
        }
        this.scoreComboBox = new JComboBox<>(choices);
        this.scoreComboBox.setSelectedIndex(-1);
        this.scoreComboBox.setBounds(MARGIN_SIZE, getCoor(0), ROW_WIDTH, ROW_HEIGHT);
        this.scoreComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeLabels();
                setScoreChosen(scoreComboBox.getSelectedIndex());
                addLabels();
                revalidate();
                repaint();
            }
        });
        this.add(this.scoreComboBox);
    }

    public void setScoreChosen(int x){
        if (x == -1){
            this.scoreChosen = null;
            this.scoreDetails = new String[]{"", "", "", "", "", "", ""};
        }
        else{
            this.scoreChosen = this.scoreList.get(x);
            String[] details = {
                    scoreChosen.getCourseLinked(),
                    scoreChosen.getCourseName(),
                    scoreChosen.getStudentLinked(),
                    scoreChosen.getStudentName(),
                    String.valueOf(this.scoreChosen.getValue()),
                    this.scoreChosen.getStudentProtest(),
                    this.scoreChosen.getProfessorAnswer()
            };
            this.scoreDetails = details;
        }
    }

    public void addLabels(){
        for (int i = 0; i < this.labelNames.length; i++){
            String text = this.labelNames[i] + ": " + this.scoreDetails[i];
            this.labels[i] = new JLabel(text);
            this.labels[i].setBounds(MARGIN_SIZE, getCoor(i + 1 + (int)(i / 6)), ROW_WIDTH, ROW_HEIGHT);
            this.add(this.labels[i]);
        }
    }

    public void removeLabels(){
        for (int i = 0; i < this.labelNames.length; i++)
            this.remove(this.labels[i]);
    }

    public void addConfirmButton(){
        this.confirmButton.setText("Confirm score");
        this.confirmButton.setBackground(Color.CYAN);
        this.confirmButton.setBounds((WIDTH - MARGIN_SIZE - LABEL_WIDTH) / 2, getCoor(11), MARGIN_SIZE + LABEL_WIDTH, ROW_HEIGHT);
        this.confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = scoreComboBox.getSelectedIndex();
                if (x == -1){
                    JOptionPane.showMessageDialog(ConfirmScoreDialog.this, "Please select a score to confirm.");
                    return;
                }
                int option = JOptionPane.showConfirmDialog(ConfirmScoreDialog.this, "Confirm the selected score?\n(These changes are permanent and irreversible.)");
                if (option == 0){
                    scoreList.get(x).setStatus(ScoreStatus.CONFIRMED);
                    app.updateScore(scoreList.get(x));
                }
            }
        });
        this.add(this.confirmButton);
    }

}
