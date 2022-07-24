package graphics;

import data.Data;
import process.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static process.University.getUser;

public class AddCourseDialog extends JDialog {

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

    private int WIDTH = 450;
    private int HEIGHT = 600;
    private int MARGIN_SIZE = 25;
    private int LABEL_HEIGHT = 40;
    public AddCourseDialog(User user, Course course){
        super();
        this.userLoggedIn = user;
        this.courseChosen = course;
        this.setModal(true);
        this.setLayout(null);
        this.setTitle("Add Course");
        this.setSize(WIDTH, HEIGHT);

        addLabels();
        addButtons();
        if (course != null){
            fillFields();
        }

        this.setVisible(true);
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * LABEL_HEIGHT);
    }

    public void addLabels(){
        for (int i = 0; i < this.fields.length; i++){
            this.labels[i] = new JLabel();
            this.labels[i].setText(this.fieldNames[i]);
            this.labels[i].setBounds(MARGIN_SIZE, getCoor(i + (int)(i / 4)), 125, LABEL_HEIGHT);
            this.add(this.labels[i]);

            if (i == 3 || i >= 6)
                continue;

            this.fields[i] = new JTextField();
            this.fields[i].setBounds(MARGIN_SIZE + 125, getCoor(i + (int)(i / 4)), 275, LABEL_HEIGHT);
            this.add(this.fields[i]);
        }
        this.idLabel.setText(String.format("%02d", this.userLoggedIn.getCollege().ordinal()));
        this.idLabel.setBounds(MARGIN_SIZE + 125, getCoor(8), 50, LABEL_HEIGHT);
        this.add(this.idLabel);

        this.fields[7] = new JTextField();
        this.fields[7].setBounds(MARGIN_SIZE + 150, getCoor(8), 50, LABEL_HEIGHT);
        this.add(this.fields[7]);

        this.studentField.setBounds(MARGIN_SIZE + 125, getCoor(9), 275, getCoor(12) - getCoor(9));
        this.add(this.studentField);
    }

    public void addClassTimes(){
        String text = "";
        for (ClassTime time : this.classTimeList)
            text += time.toString() + " / ";
        this.classTimeLabel.setText(text);
        this.classTimeLabel.setBounds(MARGIN_SIZE, getCoor(4), 400, LABEL_HEIGHT);
        this.add(this.classTimeLabel);
    }

    public void addButtons(){
        String[] levels = {"UNDERGRADUATE", "GRADUATE", "SHARED"};
        this.typeComboBox = new JComboBox<>(levels);
        this.typeComboBox.setSelectedIndex(-1);
        this.typeComboBox.setBounds(MARGIN_SIZE + 125, getCoor(7), 275, LABEL_HEIGHT);
        this.add(this.typeComboBox);

        this.addTimeButton.setText("Add time");
        this.addTimeButton.setBackground(Color.GREEN);
        this.addTimeButton.setBounds(MARGIN_SIZE + 125, getCoor(3), 125, LABEL_HEIGHT);
        this.addTimeButton.addActionListener(new ClassTimeListener());
        this.add(this.addTimeButton);

        this.removeTimeButton.setText("Remove time");
        this.removeTimeButton.setBackground(Color.RED);
        this.removeTimeButton.setBounds(MARGIN_SIZE + 250, getCoor(3), 125, LABEL_HEIGHT);
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

        this.okButton.setText("Add");
        this.okButton.setBackground(Color.CYAN);
        this.okButton.setBounds((WIDTH - 150) / 2, getCoor(12), 150, LABEL_HEIGHT);
        this.okButton.addActionListener(new ConfirmCourseListener());
        this.add(this.okButton);
    }

    public void fillFields(){
        this.fields[0].setText(this.courseChosen.getCourseName());
        this.fields[1].setText(this.courseChosen.getProfessorID());
        this.fields[2].setText(String.valueOf(this.courseChosen.getUnits()));
        this.classTimeList = Arrays.asList(this.courseChosen.giveClassTimes());
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

    public class ClassTimeListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            ClassTimeDialog dialog = new ClassTimeDialog();
            ClassTime time = dialog.getTime();
            dialog.dispose();
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
            if (University.getCourse(courseID) != null && courseChosen == null){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "This course ID already exists. Please choose another one.");
                return;
            }
            User prof = University.getUser(fields[1].getText());
            if (prof == null || prof.isStudent()){
                JOptionPane.showMessageDialog(AddCourseDialog.this, "This professor ID doesn't exists. Please choose another one.");
                return;
            }
            String[] ids = studentField.getText().split(" ");
            List<Student> students = new ArrayList<>();
            for (String id : ids){
                if (!id.equals("")){
                    User user = University.getUser(id);
                    if (user == null || !user.isStudent()){
                        JOptionPane.showMessageDialog(AddCourseDialog.this, "Please enter valid student IDs separated by spaces.");
                        return;
                    }
                    students.add((Student) user);
                }
            }
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
                    prof.getUniversityID(),
                    units,
                    userLoggedIn.getCollege(),
                    times,
                    fields[4].getText(),
                    fields[5].getText(),
                    level
            );
            College college = College.getInstance(userLoggedIn.getCollege());
            course.setCourseID(courseID);
            for (Score score : prof.getCourseList()){
                if (course.getCourseID().equals(score.getCourseLinked())){
                    prof.getCourseList().remove(score);
                    break;
                }
            }
            prof.addScore(new Score(prof.getUniversityID(), course.getCourseID(), 0));
            for (Student student : students){
                Score score = new Score(student.getUniversityID(), courseID, 0);
                for (Score courseScore : course.getScoreList()){
                    if (score.getStudentLinked().equals(courseScore.getStudentLinked())){
                        course.getScoreList().remove(courseScore);
                        break;
                    }
                }
                course.getScoreList().add(score);
                for (Score studentScore : student.getCourseList()){
                    if (score.getCourseLinked().equals(studentScore.getCourseLinked())){
                        student.getCourseList().remove(studentScore);
                        break;
                    }
                }
                student.getCourseList().add(score);
                Data.writeUser(student);
                Data.writeToLog(userLoggedIn.getUniversityID(), " SCORE_UPDATE FOR USER " + student.getUniversityID());
            }
            for (Course previousCourse : college.getCourseList())
                if (course.getCourseID().equals(previousCourse.getCourseID())){
                    college.getCourseList().remove(previousCourse);
                    break;
                }
            college.getCourseList().add(course);
            Data.writeUser(prof);
            Data.writeToLog(userLoggedIn.getUniversityID(), " COURSE_UPDATE FOR USER " + prof.getUniversityID());
            Data.writeCourse(course);
            Data.writeToLog(userLoggedIn.getUniversityID(), " SCORE_LIST_UPDATE FOR COURSE " + course.getCourseID());
            JOptionPane.showMessageDialog(AddCourseDialog.this, "Course added successfully.");
        }
    }
}
