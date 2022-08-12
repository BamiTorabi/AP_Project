package client.graphical;

import client.Application;
import client.logic.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class NewChatDialog extends DialogTemplate{

    private JRadioButton userListRadioButton = new JRadioButton();
    private JRadioButton userIDRadioButton = new JRadioButton();
    private ButtonGroup buttons = new ButtonGroup();

    private ArrayList<User> users;
    private JScrollPane userListPane;
    private JPanel userListPanel;
    private JCheckBox[] checkBoxes;
    private JCheckBox allSelected;
    private GridBagConstraints gbc;

    private JTextField userIDField;
    private JLabel userIDLabel;

    private JTextArea messageArea;
    private JLabel messageLabel;
    private JButton sendButton;

    private int WIDTH = 450;
    private int HEIGHT = 600;
    private int MARGIN_SIZE = 25;
    private int LABEL_HEIGHT = 40;

    public NewChatDialog(Application app, String userID) {
        super(app, userID);
        this.setVisible(true);
        this.setModalityType(ModalityType.DOCUMENT_MODAL);
        this.setLayout(null);
        this.setTitle("New chat...");
        this.setSize(WIDTH, HEIGHT);
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

        this.gbc = new GridBagConstraints();

        addRadioButtons();
        addUserIDField();
        addMessageBox();
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * LABEL_HEIGHT);
    }

    public void addRadioButtons(){
        this.userListRadioButton = new JRadioButton("Choose from list");
        this.userListRadioButton.setSelected(true);
        this.userListRadioButton.setBounds(MARGIN_SIZE, getCoor(0), 200, LABEL_HEIGHT);
        this.userListRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userIDField != null)
                    remove(userIDField);
                if (userIDLabel != null)
                    remove(userIDLabel);
                add(allSelected);
                add(userListPane);
                revalidate();
                repaint();
            }
        });
        this.buttons.add(this.userListRadioButton);
        this.add(this.userListRadioButton);

        this.userIDRadioButton = new JRadioButton("Enter ID");
        this.userIDRadioButton.setSelected(false);
        this.userIDRadioButton.setBounds(MARGIN_SIZE + 200, getCoor(0), 200, LABEL_HEIGHT);
        this.userIDRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allSelected != null)
                    remove(allSelected);
                if (userListPane != null)
                    remove(userListPane);
                add(userIDField);
                add(userIDLabel);
                revalidate();
                repaint();
            }
        });
        this.buttons.add(this.userIDRadioButton);
        this.add(this.userIDRadioButton);
    }

    public void addCheckBoxes(String info){
        fillCheckBoxes(info);

        this.allSelected = new JCheckBox();
        this.allSelected.setText("All");
        this.allSelected.setBounds(MARGIN_SIZE , getCoor(1), 100, LABEL_HEIGHT);
        this.allSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxes == null || checkBoxes.length == 0)
                    return;
                for (JCheckBox checkBox : checkBoxes)
                    checkBox.setSelected(allSelected.isSelected());
                revalidate();
                repaint();
            }
        });
    }

    public void fillCheckBoxes(String info){
        this.users = (ArrayList<User>) app.unpackUserList(info);
        int n = this.users.size();
        this.checkBoxes = new JCheckBox[n];
        if (this.userListPanel != null)
            remove(this.userListPanel);
        this.userListPanel = new JPanel(new GridBagLayout());
        gbc.gridy = 0;
        for (int i = 0; i < n; i++){
            User user = this.users.get(i);
            String S = user.getUniversityID() + " - " + user.giveName();
            this.checkBoxes[i] = new JCheckBox(S);
            this.checkBoxes[i].setSelected(false);
            this.userListPanel.add(this.checkBoxes[i], gbc);
            gbc.gridy++;
        }
        this.userListPane = new JScrollPane(this.userListPanel);
        this.userListPane.setBounds(MARGIN_SIZE, getCoor(2), 400, getCoor(5) - getCoor(2));
    }

    public void addUserIDField(){
        this.userIDLabel = new JLabel("User ID:");
        this.userIDLabel.setBounds(MARGIN_SIZE, getCoor(2), 125, LABEL_HEIGHT);

        this.userIDField = new JTextField();
        this.userIDField.setBounds(MARGIN_SIZE + 125, getCoor(2), 275, LABEL_HEIGHT);
    }

    public void addMessageBox(){
        this.messageLabel = new JLabel("Message:");
        this.messageLabel.setBounds(MARGIN_SIZE, getCoor(5), 125, LABEL_HEIGHT);
        this.add(this.messageLabel);

        this.messageArea = new JTextArea();
        this.messageArea.setBounds(MARGIN_SIZE, getCoor(6), 400, getCoor(11) - getCoor(6));
        this.add(this.messageArea);

        this.sendButton = new JButton("Send message");
        this.sendButton.setBackground(Color.GREEN);
        this.sendButton.setBounds((WIDTH - 150) / 2, getCoor(11), 150, LABEL_HEIGHT);
        this.sendButton.addActionListener(new SendListener());
        this.add(this.sendButton);
    }

    @Override
    public void refreshDialog(String info) {
        if (lastUpdate.equals(info))
            return;
        lastUpdate = info;
        addCheckBoxes(info);
        this.userListRadioButton.doClick();
    }

    public class SendListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messageArea.getText();
            if (userListRadioButton.isSelected()){
                boolean noneSelected = true;
                for (int i = 0; i < users.size(); i++){
                    if (checkBoxes[i].isSelected()){
                        noneSelected = false;
                        app.addNewChat(userID, users.get(i).getUniversityID(), message);
                    }
                }
                if (noneSelected){
                    JOptionPane.showMessageDialog(NewChatDialog.this, "Please select at least one user to send a message to.");
                    return;
                }
            }
            else{
                app.addNewChat(userID, userIDField.getText(), message);
            }
        }
    }
}
