package client.graphical;

import client.Application;
import client.logic.Professor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class MainPage extends PageTemplate {

    private int BUTTON_SIZE = 100;
    private int TOOLBAR_HEIGHT = 100;

    private JToolBar topToolbar = new JToolBar();
    private GridBagConstraints gbc = new GridBagConstraints();

    private JButton backButton = new JButton();
    private JButton homeButton = new JButton();
    private JButton logOutButton = new JButton();
    private JLabel timeToolbar = new JLabel();
    private JPanel mainPanel = new JPanel();

    private JComboBox<String> listsButton;
    private JComboBox<String> eduButton;
    private JComboBox<String> reportButton;
    private JComboBox<String> customizeButton;

    public MainPage(Application app){
        super(app);
        this.setTitle("Sharif EDU");
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(1000, 800);
    }

    public int getCoor(double num){
        return (int)(num * BUTTON_SIZE);
    }

    public void addTopToolbar(){
        this.topToolbar = new JToolBar();
        this.topToolbar.setLayout(new GridBagLayout());
        this.topToolbar.setFloatable(false);
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        addButtons();
        this.topToolbar.setBounds(0, 0, this.getWidth(), TOOLBAR_HEIGHT);
        this.add(this.topToolbar);
    }

    public void addButtons(){
        addBackButton();
        addHomeButton();
        addLogOutButton();
        addTimeToolbar();
        addListsButton();
        addEduButton();
        addReportButton();
        addCustomButton();
    }

    public void addBackButton(){
        this.backButton = new JButton();
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
        this.homeButton = new JButton();
        this.homeButton.setIcon(new ImageIcon("resources/icons/home.png"));
        this.homeButton.setOpaque(false);
        this.homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.askForInfo(1, app.getUserID());
            }
        });
        gbc.gridx = 1;
        this.topToolbar.add(this.homeButton, gbc);
    }

    public void addLogOutButton(){
        this.logOutButton = new JButton();
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
        this.timeToolbar = new JLabel();
        this.timeToolbar.setText(Calendar.getInstance().getTime().toString());
        this.timeToolbar.setHorizontalAlignment(SwingConstants.CENTER);
        this.timeToolbar.setSize(450, TOOLBAR_HEIGHT);
        gbc.gridx = 3;
        this.topToolbar.add(this.timeToolbar, gbc);
    }

    public void addCustomButton(){
        this.customizeButton = new JComboBox<>(new String[] {"Chat", "Notifications", "Profile"});
        this.customizeButton.setSelectedIndex(-1);
        this.customizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (customizeButton.getSelectedIndex()){
                    case 0: // 11
                        break;
                    case 1:
                        app.askForInfo(10, app.getUserID());
                        break;
                    case 2:
                        app.askForInfo(2, app.getUserID());
                        break;
                    default:
                }
                listsButton.setSelectedIndex(-1);
            }
        });
        gbc.gridx = 7;
        this.topToolbar.add(this.customizeButton, gbc);
    }


    public void addListsButton(){
        this.listsButton = new JComboBox<>(new String[] {"Courses List", "Professors List"});
        this.listsButton.setSelectedIndex(-1);
        this.listsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (listsButton.getSelectedIndex()){
                    case 0:
                        app.askForInfo(3, "");
                        break;
                    case 1:
                        app.askForInfo(4, "");
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
        this.eduButton = new JComboBox<>(new String[] {"Weekly Plan", "Exam Plan"});
        this.eduButton.setSelectedIndex(-1);
        this.eduButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (eduButton.getSelectedIndex()){
                    case 0:
                        app.askForInfo(5, app.getUserID());
                        break;
                    case 1:
                        app.askForInfo(6, app.getUserID());
                        break;
                    case 2:
                        //app.newPage(7);
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
        if (app.getUserLoggedIn().getUserType().equals("Student"))
           this.reportButton.addItem("Report Card");
        else if ((app.getUserLoggedIn().getUserType().equals("Special")) || (app.getUserLoggedIn() instanceof Professor && ((Professor) app.getUserLoggedIn()).isDeputy())){
            this.reportButton.addItem("Students Info");
        }
        this.reportButton.setSelectedIndex(-1);
        this.reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (reportButton.getSelectedIndex()){
                    case 0:
                        app.askForInfo(8, app.getUserID());
                        break;
                    case 1:
                        if (app.getUserLoggedIn().getUserType().equals("Student"))
                            app.askForInfo(9, app.getUserID());
                        else{
                            app.askForInfo(13, app.getUserID() + "/\"%\"");
                        }
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

    @Override
    public void refreshPage(String info) {
        this.getContentPane().removeAll();
        this.addTopToolbar();
        this.addMainPanel();
    }
}
