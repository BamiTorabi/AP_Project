package client;

import client.graphical.*;
import client.logic.*;

import javax.swing.*;
import java.io.IOException;
import java.util.Stack;

public class Application implements Runnable{

    private Client client;
    private User userLoggedIn = null;
    private JFrame[] pageList;
    private JPanel[] panelList;
    private Stack<Integer> pageStack;
    private int captchaNumber = 0;
    private ImageIcon captchaIcon;
    //private List<Score> temporaryScoreList;

    public Application(Client client){
        this.client = client;
        //temporaryScoreList = new ArrayList<>();
        /*Data.initialize();
        Data.readUsers();
        Data.readCourses();
        Data.readRequests();*/
        //University.setSpecialUsers();
        userLoggedIn = null;
        pageList = new JFrame[2];
        panelList = new JPanel[10];
        pageStack = new Stack<>();
    }

    public void repaintApp(){
        int top = pageStack.peek();
        JFrame frameToBeDrawn = pageList[top > 0 ? 1 : 0];
        if (top > 0) {
            JPanel panelToBeDrawn = panelList[top - 1];
            if (frameToBeDrawn instanceof MainPage){
                ((MainPage) frameToBeDrawn).setMainPanel(panelToBeDrawn);
                ((MainPage) frameToBeDrawn).addMainPanel();
            }
            panelToBeDrawn.setVisible(true);
            panelToBeDrawn.revalidate();
            panelToBeDrawn.repaint();
        }
        frameToBeDrawn.setVisible(true);
        frameToBeDrawn.revalidate();
        frameToBeDrawn.repaint();
    }

    public void newPage(int n){
        if (!pageStack.isEmpty() && pageStack.peek() == n)
            return;
        pageStack.push(n);
        System.err.println(pageStack);
        if (n == 0){
            pageList[0] = new LoginPage(this);
        }
        if (pageStack.size() == 2) {
            pageList[1] = new MainPage(this, userLoggedIn);
        }
        switch (n) {
            case 1:
                panelList[0] = new MainPagePanel(this, userLoggedIn);
                break;
            case 2:
                panelList[1] = new ProfilePage(this, userLoggedIn, userLoggedIn);
                break;
            case 3:
                panelList[2] = new CoursesList(this, userLoggedIn);
                break;
        /*panelList[3] = new ProfsList();
        panelList[4] = new WeeklyPlanPage();
        panelList[5] = new ExamPlanPage();
        panelList[6] = new RequestsPage();
        panelList[7] = new TemporaryScoresPage();
        if (userLoggedIn.isStudent() || ((Professor)userLoggedIn).isDeputy())
            panelList[8] = new ReportCardPage(userLoggedIn);*/
        }
        repaintApp();
    }

    public void remove(){
        if (pageStack.size() > 2) {
            pageStack.pop();
        }
        repaintApp();
    }

    public int getCurrentPage(){
        if (pageStack.size() == 0)
            return -1;
        return pageStack.peek();
    }

    public void logOut(){
        while (pageStack.size() > 1)
            pageStack.pop();
        setUserLoggedIn(null);
        repaintApp();
    }


    public User getUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(User userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    /*public void addToTempScoreList(Score score){
        temporaryScoreList.add(score);
    }

    public void setScores(){
        if (temporaryScoreList.isEmpty())
            return;
        for (Score score : temporaryScoreList){
            Student student = (Student) University.getUser(score.getStudentLinked());
            Course course = University.getCourse(score.getCourseLinked());
            student.addScore(score);
            course.addScore(score);
        }
    }*/

    public void sendCaptchaRequest() throws IOException {
        client.send("CAPTCHA/" + String.format("%04d", captchaNumber));
    }

    public void sendUserUpdate(String userID, String fieldName, String newValue) throws IOException {
        client.send("UPDATE/USER/" + userID + "/" + fieldName + "/" + newValue);
    }

    public void setCaptcha(int x, ImageIcon icon){
        this.captchaNumber = x;
        this.captchaIcon = icon;
        if (pageList[0] == null) {
            pageList[0] = new LoginPage(this);
            newPage(0);
        }
        else {
            ((LoginPage) pageList[0]).updatePage(true);
        }
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

    public void getPageInfo(int t){
        try {
            client.send("INFO/" + userLoggedIn.getUniversityID() + String.format("/%d", t));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void badLogIn(){
        ((LoginPage) pageList[0]).wrongLogIn();
    }

    public void unpackUser(String info){
        boolean isStudent = true;
        String ID = null;
        if (userLoggedIn != null)
            ID = userLoggedIn.getUniversityID();
        userLoggedIn = new User();
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
                        userLoggedIn = new Student();
                    else
                        userLoggedIn = new Professor();
                    userLoggedIn.setStudent(isStudent);
                    break;
                case "firstName":
                    userLoggedIn.setFirstName(S[1]);
                    break;
                case "lastName":
                    userLoggedIn.setLastName(S[1]);
                    break;
                case "phoneNumber":
                    userLoggedIn.setPhoneNumber(S[1]);
                    break;
                case "nationalID":
                    userLoggedIn.setNationalID(S[1]);
                    break;
                case "emailAddress":
                    userLoggedIn.setEmailAddress(S[1]);
                    break;
                case "college":
                    userLoggedIn.setCollegeType(CollegeType.valueOf(S[1]));
                    break;
                case "type":
                    if (userLoggedIn.isStudent()){
                        ((Student) userLoggedIn).setType(StudentType.valueOf(S[1]));
                    }
                    else{
                        ((Professor) userLoggedIn).setType(ProfessorType.valueOf(S[1]));
                    }
                    break;
                case "firstYear":
                    userLoggedIn.setFirstYear(Integer.parseInt(S[1]));
                    break;
                case "totalScore":
                    assert userLoggedIn instanceof Student;
                    ((Student) userLoggedIn).setTotalScore(Double.parseDouble(S[1]));
                    break;
                case "educationalStatus":
                    assert userLoggedIn instanceof Student;
                    ((Student) userLoggedIn).setEducationalStatus(Integer.parseInt(S[1]));
                    break;
                case "roomNumber":
                    assert userLoggedIn instanceof Professor;
                    ((Professor) userLoggedIn).setRoomNumber(Integer.parseInt(S[1]));
                    break;
                case "deputy":
                    assert userLoggedIn instanceof Professor;
                    ((Professor) userLoggedIn).setDeputy(Boolean.parseBoolean(S[1]));
                    break;
                case "counsellor":
                    assert userLoggedIn instanceof Student;
                    ((Student) userLoggedIn).setCounsellor(S[1]);
                    break;
            }
        }
        userLoggedIn.setUniversityID(ID);
    }

    @Override
    public void run() {
        try {
            sendCaptchaRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
