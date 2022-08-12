package client.graphical.dialogs;

import client.Application;
import client.DataLoader;
import client.logic.*;

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
    private Application app;

    private int WIDTH = DataLoader.getConstraint("dialogs", "width");
    private int HEIGHT = DataLoader.getConstraint("dialogs", "height");
    private int MARGIN_SIZE = DataLoader.getConstraint("dialogs", "marginSize");
    private int ROW_HEIGHT = DataLoader.getConstraint("dialogs", "rowHeight");
    private int LABEL_WIDTH = DataLoader.getConstraint("dialogs", "labelWidth");
    private int ROW_WIDTH = DataLoader.getConstraint("dialogs", "rowWidth");

    public UserDialog(Application app, User user){
        super();
        this.app = app;
        this.userLoggedIn = user;
        this.setVisible(true);
        this.setModalityType(ModalityType.DOCUMENT_MODAL);
        this.setLayout(null);
        this.setTitle("Add User");
        this.setSize(WIDTH, HEIGHT);

        addUserRadioButton();
        addTextFields();
        addUserID();
        addCreateButton();
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * ROW_HEIGHT);
    }

    public void addUserRadioButton(){
        this.studentRadioButton.setText("Student");
        this.studentRadioButton.setSelected(true);
        this.studentRadioButton.setBounds(MARGIN_SIZE, getCoor(0), LABEL_WIDTH - MARGIN_SIZE, ROW_HEIGHT);
        this.studentRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(typeComboBox);
                typeComboBox = new JComboBox<>(StudentType.list());
                typeComboBox.setSelectedIndex(-1);
                typeComboBox.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(7), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
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
        this.professorRadioButton.setSelected(false);
        this.professorRadioButton.setBounds(WIDTH / 2, getCoor(0), WIDTH / 2 - MARGIN_SIZE, ROW_HEIGHT);
        this.professorRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(typeComboBox);
                String[] profTypes = {ProfessorType.ASSISTANT.toString(), ProfessorType.ASSOCIATE.toString(), ProfessorType.FULL.toString()};
                typeComboBox = new JComboBox<>(profTypes);
                typeComboBox.setSelectedIndex(-1);
                typeComboBox.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(7), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
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
            this.labels[i].setBounds(MARGIN_SIZE, getCoor(i + 1), LABEL_WIDTH, ROW_HEIGHT);
            this.add(this.labels[i]);

            if (i == 6)
                continue;

            this.fields[i] = new JTextField();
            this.fields[i].setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(i + 1), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
            this.add(this.fields[i]);
        }
        this.typeComboBox = new JComboBox<>(StudentType.list());
        this.typeComboBox.setSelectedIndex(-1);
        this.typeComboBox.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(7), ROW_WIDTH - LABEL_WIDTH, ROW_HEIGHT);
        this.add(this.typeComboBox);
    }

    public void addUserID(){
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String userType = (this.studentRadioButton.isSelected() ? "0" : "1");
        String collegeNum = String.format("%02d", this.userLoggedIn.getCollege().ordinal());
        String id = year + collegeNum + userType;

        this.userIDLabel.setText("User ID: " + id);
        this.userIDLabel.setBounds(MARGIN_SIZE, getCoor(9), LABEL_WIDTH, ROW_HEIGHT);
        this.add(this.userIDLabel);

        this.userIDField.setBounds(MARGIN_SIZE + LABEL_WIDTH, getCoor(9), 2 * MARGIN_SIZE, ROW_HEIGHT);
        this.add(this.userIDField);
    }

    public void addCreateButton(){
        this.createButton.setBackground(Color.GREEN);
        this.createButton.setText("Create");
        this.createButton.setBounds((WIDTH - LABEL_WIDTH - MARGIN_SIZE) / 2, getCoor(11), MARGIN_SIZE + LABEL_WIDTH, ROW_HEIGHT);
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
                CollegeType type = userLoggedIn.getCollege();
                if (studentRadioButton.isSelected()){
                    String counsellor = fields[7].getText();
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
                            counsellor,
                            StudentType.values()[typeComboBox.getSelectedIndex()]
                    );
                    student.setUniversityID(userID);
                    student.setPassword(fields[5].getText());
                    app.addNewStudent(student);
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
                    app.addNewProfessor(professor);
                }
                //JOptionPane.showMessageDialog(UserDialog.this, "User created successfully.");
            }
        });
        this.add(this.createButton);
    }

}