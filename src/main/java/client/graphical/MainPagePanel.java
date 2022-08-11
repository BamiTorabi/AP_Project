package client.graphical;

import client.Application;
import client.logic.*;
//import graphics.UserDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class MainPagePanel extends PanelTemplate {

    private Font normalPlainFont = new Font("Arial", Font.PLAIN, 18);

    private User user;

    private JLabel lastEntryLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private JLabel emailLabel = new JLabel();
    private JLabel counsellorLabel = new JLabel();
    private JLabel eduStatusLabel = new JLabel();
    private JButton newUserButton = new JButton();

    private int MARGIN_HEIGHT = 50;
    private int LABEL_HEIGHT = 50;

    public MainPagePanel(Application app, String userID){
        super(app, userID);
        this.setLayout(null);
    }

    public int getCoor(double x){
        return (int)(MARGIN_HEIGHT + x * LABEL_HEIGHT);
    }

    public void addGeneralLabels(){
        this.lastEntryLabel = new JLabel();
        this.lastEntryLabel.setFont(normalPlainFont);
        this.lastEntryLabel.setText("Last entry time: " + Calendar.getInstance().getTime());
        this.lastEntryLabel.setBounds(50, getCoor(2), 600, LABEL_HEIGHT);
        this.add(this.lastEntryLabel);

        this.nameLabel = new JLabel();
        this.nameLabel.setFont(normalPlainFont);
        this.nameLabel.setText("Welcome, " + this.user.giveName() + "!");
        this.nameLabel.setBounds(50, getCoor(0), 600, LABEL_HEIGHT);
        this.add(this.nameLabel);

        this.emailLabel = new JLabel();
        this.emailLabel.setFont(normalPlainFont);
        this.emailLabel.setText("Email: " + this.user.getEmailAddress());
        this.emailLabel.setBounds(50, getCoor(3), 600, LABEL_HEIGHT);
        this.add(this.emailLabel);
    }

    public void addStudentLabels(){
        Student student = (Student)this.user;

        this.counsellorLabel = new JLabel();
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

        this.eduStatusLabel = new JLabel();
        this.eduStatusLabel.setFont(normalPlainFont);
        this.eduStatusLabel.setText("Educational status: " + text);
        this.eduStatusLabel.setBounds(50, getCoor(5), 600, LABEL_HEIGHT);
        this.add(this.eduStatusLabel);
    }

    public void addNewUserButton(){
        this.newUserButton = new JButton();
        this.newUserButton.setFont(normalPlainFont);
        this.newUserButton.setText("New User");
        this.newUserButton.setBackground(Color.GREEN);
        this.newUserButton.setBounds(350, 610, 300, 50);
        this.newUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserDialog dialog = new UserDialog(app, user);
            }
        });
        this.add(this.newUserButton);
    }

    @Override
    public void refreshPanel(String info) {
        if (info.equals(lastUpdate))
            return;
        lastUpdate = info;
        this.removeAll();
        this.user = app.unpackUser(info);

        addGeneralLabels();
        if (this.user.getUserType().equals("Student"))
            addStudentLabels();
        else if (this.user.getUserType().equals("Professor") && ((Professor)this.user).isDeputy())
            addNewUserButton();
    }
}
