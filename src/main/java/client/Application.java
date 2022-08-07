package client;

import client.graphical.*;
import client.logic.*;

import javax.swing.*;
import java.io.IOException;
import java.util.Stack;

public class Application implements Runnable {

    private final Client client;
    private String userID = "";
    private final Stack<String> pageStack;
    private PanelTemplate panel;
    private PageTemplate page;
    private int captchaNumber = 0;
    private ImageIcon captchaIcon;
    private Updater updater;
    private Thread updateThread;
    //private List<Score> temporaryScoreList;

    public Application(Client client) {
        this.client = client;
        this.updater = new Updater(this);
        //temporaryScoreList = new ArrayList<>();
        /*Data.initialize();
        Data.readUsers();
        Data.readCourses();
        Data.readRequests();*/
        //University.setSpecialUsers();
        this.pageStack = new Stack<>();
    }

    public void repaintApp() {
        int pageNumber = getPageNumber(pageStack.peek());
        if (pageNumber > 0) {
            if (page instanceof MainPage) {
                ((MainPage) page).setMainPanel(this.panel);
                ((MainPage) page).addMainPanel();
            }
            panel.setVisible(true);
            panel.revalidate();
            panel.repaint();
        }
        page.setVisible(true);
        page.revalidate();
        page.repaint();
    }

    public int getPageNumber(String S){
        return Integer.parseInt(S.split("/")[0]);
    }

    public void newPage(int pageNumber, String pageInfo) {
        String temp = String.format("%02d/%s", pageNumber, pageInfo);
        if (!pageStack.isEmpty() && getPageNumber(pageStack.peek()) == pageNumber) {
            pageStack.pop();
        }
        pageStack.push(temp);
        pageStack.forEach(x -> System.out.print(x.substring(0, 2) + ", "));
        System.out.println();
        updater.getPageStack().forEach(x -> System.out.print(x.substring(0, 2) + ", "));
        System.out.println();
        if (pageNumber == 0){
            if (!(page instanceof LoginPage))
                page = new LoginPage(this);
            //page.refreshPage("");
        }
        else {
            if (!(page instanceof MainPage))
                page = new MainPage(this);
            //page.refreshPage(pageInfo);
            switch (pageNumber) {
                case 1:
                    if (!(panel instanceof MainPagePanel))
                        panel = new MainPagePanel(this, userID);
                    panel.refreshPanel(pageInfo);
                    break;
                case 2:
                    if (!(panel instanceof ProfilePage))
                        panel = new ProfilePage(this, userID);
                    panel.refreshPanel(pageInfo);
                    break;
                case 3:
                    panel = new CoursesList(this, userID);
                    break;
        /*panelList[3] = new ProfsList();
        panelList[4] = new WeeklyPlanPage();
        panelList[5] = new ExamPlanPage();
        panelList[6] = new RequestsPage();
        panelList[7] = new TemporaryScoresPage();
        if (userLoggedIn.isStudent() || ((Professor)userLoggedIn).isDeputy())
            panelList[8] = new ReportCardPage(userLoggedIn);*/
            }
        }
        page.refreshPage(pageInfo);
        repaintApp();
    }

    public void remove(){
        if (pageStack.size() > 2) {
            pageStack.pop();
            updater.popQuery();
            String query = updater.getLastQuery();
            askForInfo(getPageNumber(query), query.substring(3));
        }
    }
    

    public void logOut(){
        while (pageStack.size() > 1) {
            pageStack.pop();
            updater.popQuery();
        }
        setUserID("");
        repaintApp();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void sendUserUpdate(String userID, String fieldName, String newValue) throws IOException {
        client.send("UPDATE/USER/" + userID + "/" + fieldName + "/" + newValue);
    }

    public void setCaptcha(int x, ImageIcon icon){
        this.captchaNumber = x;
        this.captchaIcon = icon;
    }

    public int getCaptchaNumber() {
        return captchaNumber;
    }

    public ImageIcon getCaptchaIcon() {
        return captchaIcon;
    }

    public void setCaptchaNumber(int captchaNumber) {
        this.captchaNumber = captchaNumber;
    }

    public void setCaptchaIcon(ImageIcon captchaIcon) {
        this.captchaIcon = captchaIcon;
    }

    public void logIn(String username, String password){
        if (username.equals(""))
            username += "0";
        if (password.equals(""))
            password += "0";
        try {
            client.send("LOGIN/" + username + "/" + password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User unpackUser(String info){
        boolean isStudent = true;
        String ID = null;
        User user = new User();
        for (String entry : info.split("/")){
            String[] S = entry.split(":");
            switch (S[0]){
                case "universityID":
                    if (ID == null)
                        ID = S[1];
                    break;
                case "student":
                    isStudent = Boolean.parseBoolean(S[1]);
                    if (isStudent)
                        user = new Student();
                    else
                        user = new Professor();
                    user.setStudent(isStudent);
                    break;
                case "firstName":
                    user.setFirstName(S[1]);
                    break;
                case "lastName":
                    user.setLastName(S[1]);
                    break;
                case "phoneNumber":
                    user.setPhoneNumber(S[1]);
                    break;
                case "nationalID":
                    user.setNationalID(S[1]);
                    break;
                case "emailAddress":
                    user.setEmailAddress(S[1]);
                    break;
                case "college":
                    user.setCollegeType(CollegeType.valueOf(S[1]));
                    break;
                case "type":
                    if (user.isStudent()){
                        ((Student) user).setType(StudentType.valueOf(S[1]));
                    }
                    else{
                        ((Professor) user).setType(ProfessorType.valueOf(S[1]));
                    }
                    break;
                case "firstYear":
                    user.setFirstYear(Integer.parseInt(S[1]));
                    break;
                case "totalScore":
                    assert user instanceof Student;
                    ((Student) user).setTotalScore(Double.parseDouble(S[1]));
                    break;
                case "educationalStatus":
                    assert user instanceof Student;
                    ((Student) user).setEducationalStatus(Integer.parseInt(S[1]));
                    break;
                case "roomNumber":
                    assert user instanceof Professor;
                    ((Professor) user).setRoomNumber(Integer.parseInt(S[1]));
                    break;
                case "deputy":
                    assert user instanceof Professor;
                    ((Professor) user).setDeputy(Boolean.parseBoolean(S[1]));
                    break;
                case "counsellor":
                    assert user instanceof Student;
                    ((Student) user).setCounsellor(S[1]);
                    break;
            }
        }
        user.setUniversityID(ID);
        return user;
    }

    public void unpackMessage(int pageNumber, String info){
        String[] S = info.split("/");
        switch (pageNumber){
            case 0:
                int x = Integer.parseInt(S[0]);
                client.receiveCaptcha(info.substring(5));
                setCaptcha(x, new ImageIcon("captchaTest.jpg"));
                break;
            case 1:
            case 2:
                break;
            case 3:
                break;
        }
        newPage(pageNumber, info);
    }

    public void askForInfo(int pageNumber, String infoNeeded) {
        String message = String.format("INFO/%02d/%s", pageNumber, infoNeeded);
        this.updater.addQuery(message.substring(5));
        try{
            this.client.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void goodLogIn(String userID){
        this.userID = userID;
        askForInfo(1, userID);
    }

    public void badLogIn(){
        if (this.page instanceof LoginPage)
            ((LoginPage) this.page).setTopText("Wrong username or password. Please try again.");
        askForInfo(0, String.format("%04d", captchaNumber));
    }

    @Override
    public void run() {
        askForInfo(0, "0000");
        this.updateThread = new Thread(this.updater);
        this.updateThread.start();
    }
}
