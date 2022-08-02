package client.graphical;

import client.Application;
import client.logic.*;
//import graphics.UserDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class MainPagePanel extends JPanel {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 18);

    private Application app;
    private User user;

    private JLabel lastEntryLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private JLabel emailLabel = new JLabel();
    private JLabel counsellorLabel = new JLabel();
    private JLabel eduStatusLabel = new JLabel();
    private JButton newUserButton = new JButton();

    private int MARGIN_HEIGHT = 50;
    private int LABEL_HEIGHT = 50;

    public MainPagePanel(Application app, User user){
        super();
        this.app = app;
        this.user = user;
        this.setLayout(null);

        addGeneralLabels();
        if (this.user.isStudent())
            addStudentLabels();
        else if (((Professor)this.user).isDeputy())
            addNewUserButton();
    }

    public int getCoor(double x){
        return (int)(MARGIN_HEIGHT + x * LABEL_HEIGHT);
    }

    public void addGeneralLabels(){
        this.lastEntryLabel.setFont(normalPlainFont);
        this.lastEntryLabel.setText("Last entry time: " + Calendar.getInstance().getTime());
        this.lastEntryLabel.setBounds(50, getCoor(2), 600, LABEL_HEIGHT);
        this.add(this.lastEntryLabel);

        this.nameLabel.setFont(normalPlainFont);
        this.nameLabel.setText("Welcome, " + this.user.giveName() + "!");
        this.nameLabel.setBounds(50, getCoor(0), 600, LABEL_HEIGHT);
        this.add(this.nameLabel);

        this.emailLabel.setFont(normalPlainFont);
        this.emailLabel.setText("Email: " + this.user.getEmailAddress());
        this.emailLabel.setBounds(50, getCoor(3), 600, LABEL_HEIGHT);
        this.add(this.emailLabel);
    }

    public void addStudentLabels(){
        Student student = (Student)this.user;

        this.counsellorLabel.setFont(normalPlainFont);
        this.counsellorLabel.setText("Counsellor: ");
        this.counsellorLabel.setBounds(50, getCoor(4), 600, LABEL_HEIGHT);
        this.add(this.counsellorLabel);

        String text = "";
        switch (student.getEducationalStatus()){
            case -1:
                text = "Dropped out";
                break;
            case 0:
                text = "Studying";
                break;
            case 1:
                text = "Graduated";
                break;
            default:
        }

        this.eduStatusLabel.setFont(normalPlainFont);
        this.eduStatusLabel.setText("Educational status: " + text);
        this.eduStatusLabel.setBounds(50, getCoor(5), 600, LABEL_HEIGHT);
        this.add(this.eduStatusLabel);
    }

    public void addNewUserButton(){
        this.newUserButton.setFont(normalPlainFont);
        this.newUserButton.setText("New User");
        this.newUserButton.setBackground(Color.GREEN);
        this.newUserButton.setBounds(350, 610, 300, 50);
        this.newUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //UserDialog dialog = new UserDialog(user);
            }
        });
        this.add(this.newUserButton);
    }

}
