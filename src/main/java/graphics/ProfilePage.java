package graphics;

import process.Professor;
import process.Student;
import process.University;
import process.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfilePage extends JPanel {

    private Font bigBoldFont = new Font("Arial", Font.BOLD, 30);
    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 20);
    private Font normalBoldFont = new Font("Arial", Font.BOLD, 20);

    private JLabel nameLabel = new JLabel();
    private JLabel titleLabel = new JLabel();
    private JLabel nationalIDLabel = new JLabel();
    private JLabel universityIDLabel = new JLabel();
    private JLabel phoneNumberLabel = new JLabel();
    private JLabel emailAddressLabel = new JLabel();
    private JLabel collegeLabel = new JLabel();

    private JLabel scoreLabel = new JLabel();
    private JLabel yearAddedLabel = new JLabel();
    private JLabel counsellorLabel = new JLabel();
    private JLabel graduationStatusLabel = new JLabel();
    private JLabel roomNumberLabel = new JLabel();

    private JTextField phoneNumberText = new JTextField();
    private JTextField emailAddressText = new JTextField();
    private JButton saveButton = new JButton();

    private User profileToBeDrawn;
    private Student studentToBeDrawn;
    private Professor profToBeDrawn;

    private Application app = Application.getInstance();

    private final int LABEL_WIDTH = 700;
    private final int LABEL_HEIGHT = 50;
    private final int MARGIN_SIZE = 50;
    private final int SPACE_SIZE = 40;

    public ProfilePage(User user){
        super();
        this.profileToBeDrawn = user;
        this.setLayout(null);

        putGeneralInfo();
        if ((profileToBeDrawn.isStudent())) {
            studentToBeDrawn = (Student) profileToBeDrawn;
            putStudentInfo();
        } else {
            profToBeDrawn = (Professor) profileToBeDrawn;
            putProfessorInfo();
        }
        this.addTitle();
        this.addSaveButton();
    }

    public void putGeneralInfo(){
        this.addName();
        this.addNationalID();
        this.addUniversityID();
        this.addPhoneNumber();
        this.addEmailAddress();
        this.addCollege();
    }

    public void putStudentInfo(){
        this.addScore();
        this.addEntryYear();
        this.addCounsellor();
        this.addGraduationStatus();
    }

    public void putProfessorInfo(){
        this.addRoomNumber();
    }

    public int getCoor(double num){
        return (int)(MARGIN_SIZE + (num - 1) * SPACE_SIZE);
    }

    public void addTitle(){
        this.titleLabel.setFont(normalBoldFont);
        String labelText = "";
        if (profileToBeDrawn.isStudent()){
            labelText = (studentToBeDrawn).getType().toString().toUpperCase() + " STUDENT";
        }
        else{
            labelText = (profToBeDrawn).getType().toString().toUpperCase() + " PROFESSOR";
        }
        this.titleLabel.setText(labelText);
        this.titleLabel.setBounds(MARGIN_SIZE, getCoor(0), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.titleLabel);
    }

    public void addName(){
        this.nameLabel.setFont(bigBoldFont);
        this.nameLabel.setText(profileToBeDrawn.getFirstName() + " " + profileToBeDrawn.getLastName());
        this.nameLabel.setBounds(MARGIN_SIZE, getCoor(1), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.nameLabel);
    }

    public void addNationalID(){
        this.nationalIDLabel.setFont(normalPlainFont);
        this.nationalIDLabel.setText("National ID: " + profileToBeDrawn.getNationalID());
        this.nationalIDLabel.setBounds(MARGIN_SIZE, getCoor(3), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.nationalIDLabel);
    }

    public void addUniversityID(){
        this.universityIDLabel.setFont(normalPlainFont);
        this.universityIDLabel.setText("University ID: " + profileToBeDrawn.getUniversityID());
        this.universityIDLabel.setBounds(MARGIN_SIZE, getCoor(4), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.universityIDLabel);
    }

    public void addPhoneNumber(){
        this.phoneNumberLabel.setFont(normalPlainFont);
        this.phoneNumberLabel.setText("Phone Number: ");
        this.phoneNumberLabel.setBounds(MARGIN_SIZE, getCoor(5), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.phoneNumberLabel);

        this.phoneNumberText.setFont(normalPlainFont);
        this.phoneNumberText.setBorder(null);
        this.phoneNumberText.setBackground(Color.WHITE);
        this.phoneNumberText.setText(profileToBeDrawn.getPhoneNumber());
        this.phoneNumberText.setBounds(MARGIN_SIZE + 175, getCoor(5), 400, 50);
        this.add(this.phoneNumberText);
    }

    public void addEmailAddress(){
        this.emailAddressLabel.setFont(normalPlainFont);
        this.emailAddressLabel.setText("Email Address: ");
        this.emailAddressLabel.setBounds(MARGIN_SIZE, getCoor(6), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.emailAddressLabel);

        this.emailAddressText.setFont(normalPlainFont);
        this.emailAddressText.setBorder(null);
        this.phoneNumberText.setBackground(Color.WHITE);
        this.emailAddressText.setText(profileToBeDrawn.getEmailAddress());
        this.emailAddressText.setBounds(MARGIN_SIZE + 175, getCoor(6), 400, 50);
        this.add(this.emailAddressText);
    }

    public void addCollege(){
        this.collegeLabel.setFont(normalPlainFont);
        this.collegeLabel.setText("College: " + profileToBeDrawn.getCollege());
        this.collegeLabel.setBounds(MARGIN_SIZE, getCoor(7), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.collegeLabel);
    }

    public void addScore(){
        this.scoreLabel.setFont(normalPlainFont);
        this.scoreLabel.setText("Total Score: " + studentToBeDrawn.getTotalScore());
        this.scoreLabel.setBounds(MARGIN_SIZE, getCoor(8), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.scoreLabel);
    }

    public void addEntryYear(){
        this.yearAddedLabel.setFont(normalPlainFont);
        this.yearAddedLabel.setText("Year Joined: " + studentToBeDrawn.getFirstYear());
        this.yearAddedLabel.setBounds(MARGIN_SIZE, getCoor(9), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.yearAddedLabel);
    }

    public void addCounsellor(){
        this.counsellorLabel.setFont(normalPlainFont);
        this.counsellorLabel.setText("Counsellor: " + University.getUser(studentToBeDrawn.getCounsellor()).giveName());
        this.counsellorLabel.setBounds(MARGIN_SIZE, getCoor(10), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.counsellorLabel);
    }

    public void addGraduationStatus(){
        this.graduationStatusLabel.setFont(normalPlainFont);
        String labelText = "Graduation Status: ";
        switch (studentToBeDrawn.getEducationalStatus()){
            case -1:
                labelText += "Dropped out";
                break;
            case 0:
                labelText += "Studying";
                break;
            case 1:
                labelText += "Graduated";
                break;
        }
        this.graduationStatusLabel.setText(labelText);
        this.graduationStatusLabel.setBounds(MARGIN_SIZE, getCoor(11), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.graduationStatusLabel);
    }

    public void addRoomNumber(){
        this.roomNumberLabel.setFont(normalPlainFont);
        this.roomNumberLabel.setText("Room No: " + profToBeDrawn.getRoomNumber());
        this.roomNumberLabel.setBounds(MARGIN_SIZE, getCoor(8), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.roomNumberLabel);
    }

    public void addSaveButton(){
        this.saveButton.setText("Save");
        this.saveButton.setBounds(850, 600, 100, 40);
        this.saveButton.setBackground(Color.GREEN);
        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phone = phoneNumberText.getText();
                String email = emailAddressText.getText();
                if (!phone.equals("") && !email.equals("")){
                    profileToBeDrawn.setPhoneNumber(phone);
                    profileToBeDrawn.setEmailAddress(email);
                    app.saveUser();
                    JOptionPane.showMessageDialog(ProfilePage.this, "Profile successfully updated.");
                }
                else{
                    JOptionPane.showMessageDialog(ProfilePage.this, "Please fill in every field to continue.");
                }
            }
        });
        this.add(this.saveButton);
    }
}
