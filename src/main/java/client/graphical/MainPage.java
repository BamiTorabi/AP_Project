package client.graphical;

import client.Application;
import client.logic.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class MainPage extends JFrame {

    private Application app;
    private User user;

    private int BUTTON_SIZE = 100;
    private int TOOLBAR_HEIGHT = 100;

    private JToolBar topToolbar = new JToolBar();
    private GridBagConstraints gbc = new GridBagConstraints();

    private JButton backButton = new JButton();
    private JButton homeButton = new JButton();
    private JButton logOutButton = new JButton();
    private JButton profileButton = new JButton();
    private JLabel timeToolbar = new JLabel();
    private JPanel mainPanel = new JPanel();

    private JComboBox<String> listsButton;
    private JComboBox<String> eduButton;
    private JComboBox<String> reportButton;

    public MainPage(Application app, User user){
        super();
        this.app = app;
        this.user = user;
        this.setTitle("Sharif EDU");
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(1000, 800);

        this.addTopToolbar();
        this.addMainPanel();
    }

    public int getCoor(double num){
        return (int)(num * BUTTON_SIZE);
    }

    public void addTopToolbar(){
        this.topToolbar.setLayout(new GridBagLayout());
        this.topToolbar.setFloatable(false);
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        addBackButton();
        addHomeButton();
        addLogOutButton();
        addTimeToolbar();
        addListsButton();
        addEduButton();
        addReportButton();
        addProfileButton();
        this.topToolbar.setBounds(0, 0, this.getWidth(), TOOLBAR_HEIGHT);
        this.add(this.topToolbar);
    }

    public void addBackButton(){
        this.backButton.setIcon(new ImageIcon("resources/icons/backarrow.png"));
        this.backButton.setOpaque(false);
        gbc.gridx = 0;
        this.backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.remove();
            }
        });
        this.topToolbar.add(this.backButton, gbc);
    }

    public void addHomeButton(){
        this.homeButton.setIcon(new ImageIcon("resources/icons/home.png"));
        this.homeButton.setOpaque(false);
        this.homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.newPage(1);
            }
        });
        gbc.gridx = 1;
        this.topToolbar.add(this.homeButton, gbc);
    }

    public void addLogOutButton(){
        this.logOutButton.setIcon(new ImageIcon("resources/icons/logout.png"));
        this.logOutButton.setOpaque(false);
        gbc.gridx = 2;
        this.logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                app.logOut();
            }
        });
        this.topToolbar.add(this.logOutButton, gbc);
    }

    public void addTimeToolbar(){
        this.timeToolbar.setText(Calendar.getInstance().getTime().toString());
        this.timeToolbar.setHorizontalAlignment(SwingConstants.CENTER);
        this.timeToolbar.setSize(450, TOOLBAR_HEIGHT);
        gbc.gridx = 3;
        this.topToolbar.add(this.timeToolbar, gbc);
    }

    public void addProfileButton(){
        this.profileButton.setText("My Profile");
        gbc.gridx = 7;
        this.profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.getPageInfo(2);
            }
        });
        this.topToolbar.add(this.profileButton, gbc);
    }

    public void addListsButton(){
        this.listsButton = new JComboBox<>(new String[] {"Courses List", "Professors List"});
        this.listsButton.setSelectedIndex(-1);
        this.listsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (listsButton.getSelectedIndex()){
                    case 0:
                        app.newPage(3);
                        break;
                    case 1:
                        app.newPage(4);
                        break;
                    default:
                }
                listsButton.setSelectedIndex(-1);
            }
        });
        gbc.gridx = 4;
        this.topToolbar.add(this.listsButton, gbc);
    }

    public void addEduButton(){
        this.eduButton = new JComboBox<>(new String[] {"Weekly Plan", "Exam Plan", "Requests"});
        this.eduButton.setSelectedIndex(-1);
        this.eduButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (eduButton.getSelectedIndex()){
                    case 0:
                        app.newPage(5);
                        break;
                    case 1:
                        app.newPage(6);
                        break;
                    case 2:
                        app.newPage(7);
                    default:
                }
                eduButton.setSelectedIndex(-1);
            }
        });
        gbc.gridx = 5;
        this.topToolbar.add(this.eduButton, gbc);
    }

    public void addReportButton(){
        this.reportButton = new JComboBox<>(new String[] {"Temporary Scores"});
        if (this.user.isStudent())
            this.reportButton.addItem("Report Card");
        this.reportButton.setSelectedIndex(-1);
        this.reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (reportButton.getSelectedIndex()){
                    case 0:
                        app.newPage(8);
                        break;
                    case 1:
                        app.newPage(9);
                        break;
                    default:
                }
                reportButton.setSelectedIndex(-1);
            }
        });
        gbc.gridx = 6;
        this.topToolbar.add(this.reportButton, gbc);
    }

    public void addMainPanel(){
        this.mainPanel.setBounds(0, TOOLBAR_HEIGHT, this.getWidth(), this.getHeight() - TOOLBAR_HEIGHT);
        this.add(this.mainPanel);
    }

    public void setMainPanel(JPanel mainPanel) {
        this.remove(this.mainPanel);
        this.mainPanel = mainPanel;
    }
}
