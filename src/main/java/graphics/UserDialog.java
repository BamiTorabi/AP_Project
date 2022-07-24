package graphics;

import data.Data;
import process.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class UserDialog extends JDialog {

    private User userLoggedIn;

    private JRadioButton studentRadioButton = new JRadioButton();
    private JRadioButton professorRadioButton = new JRadioButton();
    private ButtonGroup userTypeButtons = new ButtonGroup();

    private String[] fieldNames = {"First name", "Last name", "National ID", "Phone number", "Email address", "Password", "Type", "Counsellor ID"};
    private JTextField[] fields = new JTextField[fieldNames.length];
    private JComboBox<String> typeComboBox;
    private JLabel[] labels = new JLabel[fieldNames.length];
    private JLabel userIDLabel = new JLabel();
    private JTextField userIDField = new JTextField();
    private JButton createButton = new JButton();

    private int WIDTH = 450;
    private int HEIGHT = 600;
    private int MARGIN_SIZE = 25;
    private int LABEL_HEIGHT = 40;

    public UserDialog(User user){
        super();
        this.userLoggedIn = user;
        this.setModal(true);
        this.setLayout(null);
        this.setTitle("Add User");
        this.setSize(WIDTH, HEIGHT);

        addUserRadioButton();
        addTextFields();
        addUserID();
        addCreateButton();
        this.setVisible(true);
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * LABEL_HEIGHT);
    }

    public void addUserRadioButton(){
        this.studentRadioButton.setText("Student");
        this.studentRadioButton.setSelected(true);
        this.studentRadioButton.setBounds(MARGIN_SIZE, getCoor(0), 100, LABEL_HEIGHT);
        this.studentRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(typeComboBox);
                typeComboBox = new JComboBox<>(StudentType.list());
                typeComboBox.setSelectedIndex(-1);
                typeComboBox.setBounds(MARGIN_SIZE + 125, getCoor(7), 275, LABEL_HEIGHT);
                add(typeComboBox);

                fieldNames[7] = "Counsellor ID";
                remove(labels[7]);
                labels[7].setText(fieldNames[7]);
                add(labels[7]);

                remove(userIDLabel);
                remove(userIDField);
                addUserID();
                revalidate();
                repaint();
            }
        });
        this.userTypeButtons.add(studentRadioButton);
        this.add(this.studentRadioButton);

        this.professorRadioButton.setText("Professor");
        this.studentRadioButton.setSelected(false);
        this.professorRadioButton.setBounds(MARGIN_SIZE + 200, getCoor(0), 100, LABEL_HEIGHT);
        this.professorRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(typeComboBox);
                String[] profTypes = {ProfessorType.ASSISTANT.toString(), ProfessorType.ASSOCIATE.toString(), ProfessorType.FULL.toString()};
                typeComboBox = new JComboBox<>(profTypes);
                typeComboBox.setSelectedIndex(-1);
                typeComboBox.setBounds(MARGIN_SIZE + 125, getCoor(7), 275, LABEL_HEIGHT);
                add(typeComboBox);

                fieldNames[7] = "Room No.";
                remove(labels[7]);
                labels[7].setText(fieldNames[7]);
                add(labels[7]);

                remove(userIDLabel);
                remove(userIDField);
                addUserID();
                revalidate();
                repaint();
            }
        });
        this.userTypeButtons.add(professorRadioButton);
        this.add(this.professorRadioButton);
    }

    public void addTextFields(){
        for (int i = 0; i < this.fields.length; i++){
            this.labels[i] = new JLabel();
            this.labels[i].setText(this.fieldNames[i]);
            this.labels[i].setBounds(MARGIN_SIZE, getCoor(i + 1), 125, LABEL_HEIGHT);
            this.add(this.labels[i]);

            if (i == 6)
                continue;

            this.fields[i] = new JTextField();
            this.fields[i].setBounds(MARGIN_SIZE + 125, getCoor(i + 1), 275, LABEL_HEIGHT);
            this.add(this.fields[i]);
        }
        this.typeComboBox = new JComboBox<>(StudentType.list());
        this.typeComboBox.setSelectedIndex(-1);
        this.typeComboBox.setBounds(MARGIN_SIZE + 125, getCoor(7), 275, LABEL_HEIGHT);
        this.add(this.typeComboBox);
    }

    public void addUserID(){
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String userType = (this.studentRadioButton.isSelected() ? "0" : "1");
        String collegeNum = String.format("%02d", this.userLoggedIn.getCollege().ordinal());
        String id = year + collegeNum + userType;

        this.userIDLabel.setText("User ID: " + id);
        this.userIDLabel.setBounds(MARGIN_SIZE, getCoor(9), 125, LABEL_HEIGHT);
        this.add(this.userIDLabel);

        this.userIDField.setBounds(MARGIN_SIZE + 125, getCoor(9), 50, LABEL_HEIGHT);
        this.add(this.userIDField);
    }

    public void addCreateButton(){
        this.createButton.setBackground(Color.GREEN);
        this.createButton.setText("Create");
        this.createButton.setBounds((WIDTH - 150) / 2, getCoor(11), 150, LABEL_HEIGHT);
        this.createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean allFull = !userIDField.getText().equals("");
                for (int i = 0; i < fields.length; i++){
                    if (i == 6)
                        continue;
                    allFull &= (!fields[i].getText().equals(""));
                }
                if (!allFull){
                    JOptionPane.showMessageDialog(UserDialog.this, "Please fill all fields.");
                    return;
                }
                int num = 0;
                try{
                    num = Integer.parseInt(userIDField.getText());
                    if (num < 0 || num > 1000){
                        JOptionPane.showMessageDialog(UserDialog.this, "Please enter a non-negative three digit number for the user ID field.");
                        return;
                    }
                } catch (Exception err){
                    JOptionPane.showMessageDialog(UserDialog.this, "Please enter a non-negative three digit number for the user ID field.");
                    return;
                }
                String userID = userIDLabel.getText().split(" ")[2] + String.format("%03d", num);
                if (University.getUser(userID) != null){
                    JOptionPane.showMessageDialog(UserDialog.this, "This user ID already exists. Please choose another one.");
                    return;
                }
                CollegeType type = userLoggedIn.getCollege();
                if (studentRadioButton.isSelected()){
                    User counsellor = College.getInstance(type).getUser(fields[7].getText());
                    if (counsellor == null || counsellor.isStudent()){
                        JOptionPane.showMessageDialog(UserDialog.this, "Please enter a valid professor ID from this college as a counsellor.");
                        return;
                    }
                    if (typeComboBox.getSelectedIndex() == -1){
                        JOptionPane.showMessageDialog(UserDialog.this, "Please select a valid type of student for the user.");
                        return;
                    }
                    Student student = new Student(
                            fields[0].getText(),
                            fields[1].getText(),
                            fields[2].getText(),
                            fields[3].getText(),
                            fields[4].getText(),
                            type,
                            (Professor) counsellor,
                            StudentType.values()[typeComboBox.getSelectedIndex()]
                    );
                    student.setUniversityID(userID);
                    student.setPassword(fields[5].getText());
                    College.getInstance(type).addToStudentList(student);
                    Data.writeUser(student);
                    Data.writeToLog(userLoggedIn.getUniversityID(), "NEW USER ADDED " + student.getUniversityID());
                }
                else{
                    try{
                        num = Integer.parseInt(fields[7].getText());
                    } catch (Exception err){
                        JOptionPane.showMessageDialog(UserDialog.this, "Please enter a non-negative number for the room number field.");
                        return;
                    }
                    if (typeComboBox.getSelectedIndex() == -1){
                        JOptionPane.showMessageDialog(UserDialog.this, "Please select a valid type of professor for the user.");
                        return;
                    }
                    Professor professor = new Professor(
                            fields[0].getText(),
                            fields[1].getText(),
                            fields[2].getText(),
                            fields[3].getText(),
                            fields[4].getText(),
                            type,
                            num,
                            ProfessorType.values()[typeComboBox.getSelectedIndex() + 1],
                            false,
                            false
                    );
                    professor.setUniversityID(userID);
                    professor.setPassword(fields[5].getText());
                    College.getInstance(type).addToProfList(professor);
                    Data.writeUser(professor);
                    Data.writeToLog(userLoggedIn.getUniversityID(), "NEW USER ADDED " + professor.getUniversityID());
                }
                JOptionPane.showMessageDialog(UserDialog.this, "User created successfully.");
            }
        });
        this.add(this.createButton);
    }

}