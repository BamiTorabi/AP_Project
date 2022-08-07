package client.graphical;

import client.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends PageTemplate {
    private JLabel topTextLabel = new JLabel();
    private JLabel usernameLabel = new JLabel();
    private JLabel passwordLabel = new JLabel();
    private JLabel captchaLabel = new JLabel();

    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField captchaField = new JTextField();

    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();
    private JButton captchaPicture;

    private final int HEIGHT = 300;
    private final int WIDTH = 450;
    private final int HORIZONTAL_MARGIN = 50;
    private final int VERTICAL_MARGIN = 10;
    private final int HORIZONTAL_SPACE = 5;
    private final int VERTICAL_SPACE = 10;
    private final int TOP_LABEL_WIDTH = 400;
    private final int FIELD_WIDTH = 250;
    private final int LABEL_WIDTH = 100;
    private final int LABEL_HEIGHT = 20;
    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 50;
    private String topText;

    public LoginPage(Application app){
        super(app);
        this.setTitle("Login");
        this.setSize(WIDTH, HEIGHT);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.topText = "Welcome!";
    }

    @Override
    public void refreshPage(String info) {
        this.getContentPane().removeAll();
        setTopText(this.topText);
        addUsernameField();
        addPasswordField();
        addCaptchaField();
        addButtons();
    }

    public int getCoor(double num, boolean isHorizontal){
        if (isHorizontal)
            return (int) (HORIZONTAL_MARGIN + (num - 1) * (LABEL_WIDTH + HORIZONTAL_SPACE));
        else
            return (int) (VERTICAL_MARGIN + (num - 1) * (LABEL_HEIGHT + VERTICAL_SPACE));
    }

    public void setTopText(String topText){
        this.topTextLabel.setText(topText);
        this.topTextLabel.setBounds(getCoor(1, true), getCoor(1, false), TOP_LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.topTextLabel);
    }

    public void addUsernameField(){
        this.usernameLabel.setText("User ID:");
        this.usernameLabel.setBounds(getCoor(1, true), getCoor(2, false), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.usernameLabel);

        this.usernameField.setBounds(getCoor(2, true), getCoor(2, false), FIELD_WIDTH, LABEL_HEIGHT);
        this.add(this.usernameField);
    }

    public void addPasswordField(){
        this.passwordLabel.setText("Password:");
        this.passwordLabel.setBounds(getCoor(1, true), getCoor(3, false), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.passwordLabel);

        this.passwordField.setBounds(getCoor(2, true), getCoor(3, false), FIELD_WIDTH, LABEL_HEIGHT);
        this.add(this.passwordField);
    }

    public void addCaptchaField(){
        this.captchaLabel.setText("CAPTCHA:");
        this.captchaLabel.setBounds(getCoor(1, true), getCoor(4, false), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.captchaLabel);

        this.captchaField.setBounds(getCoor(2, true), getCoor(4, false), FIELD_WIDTH, LABEL_HEIGHT);
        this.add(this.captchaField);

        this.captchaPicture = new JButton(app.getCaptchaIcon());
        this.captchaPicture.setBounds(getCoor(2, true), getCoor(5, false), 100, 50);
        this.captchaPicture.addActionListener(new CaptchaListener());
        this.add(this.captchaPicture);
    }

    public void addButtons(){
        this.okButton.setText("Enter");
        this.okButton.setAlignmentX(0.5F);
        this.okButton.setBounds(getCoor(1.5, true), getCoor(7, false), BUTTON_WIDTH, BUTTON_HEIGHT);
        this.okButton.addActionListener(new EnterListener());
        this.add(this.okButton);

        this.cancelButton.setText("Close");
        this.cancelButton.setAlignmentX(0.5F);
        this.cancelButton.setBounds(getCoor(2.5, true) , getCoor(7, false), BUTTON_WIDTH, BUTTON_HEIGHT);
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        this.add(this.cancelButton);
    }

    public class CaptchaListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            captchaField.setText("");
            app.askForInfo(0, String.format("%04d", app.getCaptchaNumber()));
        }
    }

    public class EnterListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String captchaFieldText = captchaField.getText();
            String usernameFieldText = usernameField.getText();
            String passwordFieldText = String.valueOf(passwordField.getPassword());
            if (captchaFieldText.equals(String.format("%04d", app.getCaptchaNumber()))){
                app.logIn(usernameFieldText, passwordFieldText);
            }
            else{
                topText = "Invalid captcha. Please try again.";
            }
            setVisible(false);
            usernameField.setText("");
            passwordField.setText("");
            captchaField.setText("");
        }
    }
}
