package client.graphical.dialogs;

import client.Application;
import client.DataLoader;
import client.graphical.templates.DialogTemplate;
import client.logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class AddCourseDialog extends DialogTemplate {

    private User userLoggedIn;
    private Course courseChosen;

    private String[] fieldNames = {"Course name", "Professor ID", "No. of units", "Class times", "Exam date", "Exam time", "Level", "ID", "Students"};
    private JTextField[] fields = new JTextField[fieldNames.length];
    private JLabel[] labels = new JLabel[fieldNames.length];
    private JLabel classTimeLabel = new JLabel();
    private JLabel idLabel = new JLabel();
    private JTextArea studentField = new JTextArea();
    private JComboBox<String> typeComboBox;
    private JButton addTimeButton = new JButton();
    private JButton removeTimeButton = new JButton();
    private JButton okButton = new JButton();
    private List<ClassTime> classTimeList = new ArrayList<>();

    private int WIDTH = DataLoader.getConstraint("dialogs", "width");
    private int HEIGHT = DataLoader.getConstraint("dialogs", "height");
    private int MARGIN_SIZE = DataLoader.getConstraint("dialogs", "marginSize");
    private int ROW_HEIGHT = DataLoader.getConstraint("dialogs", "rowHeight");
    private int LABEL_WIDTH = DataLoader.getConstraint("dialogs", "labelWidth");
    private int ROW_WIDTH = DataLoader.getConstraint("dialogs", "rowWidth");

    public AddCourseDialog(Application app, String userID){
        super(app, userID);
        this.userLoggedIn = app.getUserLoggedIn();
        this.setVisible(true);
        this.setModalityType(ModalityType.DOCUMENT_MODAL);
        this.setSize(WIDTH, HEIGHT);
        this.setLayout(null);
        this.setTitle("Add Course");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {
                app.remove();
                dispose();
            }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        addLabels();
        addButtons();
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * ROW_HEIGHT);
    }

    public void addLabels(){
        for (int i = 0; i < this.fields.length; i++){
            this.labels[i] = new JLabel();
            this.labels[i].setText(this.fieldNames[i]);
            this.labels[i].setBounds(MARGIN_SIZE, getCoor(i + (int)(i / 4)), LABEL_WIDTH, ROW_HEIGHT);
            this.add(this.labels[i]);

            if (i == 3 || i >= 6)
                continue;

            this.fields[i] = new JTextField();
            this.fields[i].setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(i + (int)(i / 4)), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
            this.add(this.fields[i]);
        }
        this.idLabel = new JLabel();
        this.idLabel.setText(String.format("%02d", this.userLoggedIn.getCollege().ordinal()));
        this.idLabel.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(8), 2 * MARGIN_SIZE, ROW_HEIGHT);
        this.add(this.idLabel);

        this.fields[7] = new JTextField();
        this.fields[7].setBounds(MARGIN_SIZE * 2 + LABEL_WIDTH, getCoor(8), 2 * MARGIN_SIZE, ROW_HEIGHT);
        this.add(this.fields[7]);

        this.studentField = new JTextArea();
        this.studentField.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(9), ROW_WIDTH - LABEL_WIDTH, getCoor(12) - getCoor(9));
        this.add(this.studentField);
    }

    public void addClassTimes(){
        String text = "";
        for (ClassTime time : this.classTimeList)
            text += time.toString() + " / ";
        this.classTimeLabel = new JLabel();
        this.classTimeLabel.setText(text);
        this.classTimeLabel.setBounds(MARGIN_SIZE, getCoor(4), ROW_WIDTH, ROW_HEIGHT);
        this.add(this.classTimeLabel);
    }

    public void addButtons(){
        String[] levels = {"UNDERGRADUATE", "GRADUATE", "SHARED"};
        this.typeComboBox = new JComboBox<>(levels);
        this.typeComboBox.setSelectedIndex(-1);
        this.typeComboBox.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(7), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
        this.add(this.typeComboBox);

        this.addTimeButton = new JButton();
        this.addTimeButton.setText("Add time");
        this.addTimeButton.setBackground(Color.GREEN);
        this.addTimeButton.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(3), LABEL_WIDTH, ROW_HEIGHT);
        this.addTimeButton.addActionListener(new ClassTimeListener());
        this.add(this.addTimeButton);

        this.removeTimeButton = new JButton();
        this.removeTimeButton.setText("Remove time");
        this.removeTimeButton.setBackground(Color.RED);
        this.removeTimeButton.setBounds(MARGIN_SIZE + 2 * LABEL_WIDTH, getCoor(3), LABEL_WIDTH, ROW_HEIGHT);
        this.removeTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(classTimeLabel);
                classTimeList.clear();
                addClassTimes();
                revalidate();
                repaint();
            }
        });
        this.add(this.removeTimeButton);

        addClassTimes();

        this.okButton = new JButton();
        this.okButton.setText("Add");
        this.okButton.setBackground(Color.CYAN);
        this.okButton.setBounds((WIDTH - LABEL_WIDTH - MARGIN_SIZE) / 2, getCoor(12), LABEL_WIDTH + MARGIN_SIZE, ROW_HEIGHT);
        this.okButton.addActionListener(new ConfirmCourseListener());
        this.add(this.okButton);
    }

    public void fillFields(String info){
        this.courseChosen = app.unpackCourseList(info).get(0);
        this.fields[0].setText(this.courseChosen.getCourseName());
        this.fields[1].setText(this.courseChosen.getProfessorID());
        this.fields[2].setText(String.valueOf(this.courseChosen.getUnits()));
        this.classTimeList = Arrays.asList(this.courseChosen.getClassTimes());
        this.remove(this.classTimeLabel);
        addClassTimes();
        String[] examTime = this.courseChosen.getExamTime().split(" ");
        this.fields[4].setText(examTime[0]);
        this.fields[5].setText(examTime[1]);
        this.typeComboBox.setSelectedIndex(this.courseChosen.getLevel().ordinal() - 1);
        this.fields[7].setText(this.courseChosen.getCourseID().substring(2));
        this.fields[7].setEditable(false);
        String text = "";
        for (Score score : this.courseChosen.getScoreList()){
            text += score.getStudentLinked() + " ";
        }
        this.studentField.setText(text);
    }

    @Override
    public void refreshDialog(String info) {
        if (info != null && !info.equals("")){
            fillFields(info);
        }
        revalidate();
        repaint();
    }

    public class ClassTimeListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            ClassTimeDialog dialog = new ClassTimeDialog();
            ClassTime time = dialog.getTime();
            dialog.dispose();
            if (time == null)
                return;
            remove(classTimeLabel);
            classTimeList.add(time);
            addClassTimes();
            revalidate();
            repaint();
        }
    }

    public class ConfirmCourseListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(AddCourseDialog.this, "Confirm this course? All previous scores will be lost.");
            if (option != 0)
                return;
            boolean allFull = true;
            for (int i = 0; i < fields.length; i++){
                if (i == 3 || i == 6 || i == 8)
                    continue;
                allFull &= (!fields[i].getText().equals(""));
            }
            if (!allFull){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "Please fill all fields.");
                return;
            }
            int num = 0;
            try{
                num = Integer.parseInt(fields[7].getText());
                if (num < 0 || num > 1000){
                    JOptionPane.showMessageDialog(AddCourseDialog.this, "Please enter a non-negative three digit number for the course ID field.");
                    return;
                }
            } catch (Exception err){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "Please enter a non-negative three digit number for the course ID field.");
                return;
            }
            String courseID = idLabel.getText() + String.format("%03d", num);
            String prof = fields[1].getText();
            String[] ids = studentField.getText().split(" ");
            num = 0;
            try{
                num = Integer.parseInt(fields[2].getText());
                if (num < 0 || num > 10){
                    JOptionPane.showMessageDialog(AddCourseDialog.this, "Please enter a digit for the units field.");
                    return;
                }
            } catch (Exception err){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "Please enter a digit for the units field.");
                return;
            }
            int units = num;
            if (classTimeList.isEmpty()){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "Please select at least one class time.");
                return;
            }
            num = typeComboBox.getSelectedIndex();
            if (num == -1){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "Please select a type for the class level field.");
                return;
            }
            ClassLevel level = ClassLevel.values()[num + 1];
            ClassTime[] times = classTimeList.toArray(new ClassTime[0]);
            Course course = new Course(
                    fields[0].getText(),
                    prof,
                    units,
                    userLoggedIn.getCollege(),
                    times,
                    fields[4].getText(),
                    fields[5].getText(),
                    level
            );
            course.setCourseID(courseID);
            for (String id : ids){
                Score score = new Score();
                score.setStudentLinked(id);
                course.addScore(score);
            }
            app.updateCourse(course);
        }
    }


}
