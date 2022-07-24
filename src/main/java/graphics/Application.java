package graphics;

import data.Data;
import process.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Application {

    private static User userLoggedIn;
    private static JFrame[] pageList;
    private static JPanel[] panelList;
    private static Application app;
    private final CaptchaLoader captchaLoader;
    private static Stack<Integer> pageStack;
    private static List<Score> temporaryScoreList;

    private Application(){
        temporaryScoreList = new ArrayList<>();
        Data.initialize();
        Data.readUsers();
        Data.readCourses();
        Data.readRequests();
        University.setSpecialUsers();
        //University.debugUsers();
        captchaLoader = CaptchaLoader.getInstance();
        userLoggedIn = null;
        pageList = new JFrame[2];
        panelList = new JPanel[10];
        pageList[0] = new LoginPage();
        pageStack = new Stack<>();
        newPage(0);
    }

    public static Application getInstance(){
        if (app == null) {
            app = new Application();
        }
        return app;
    }

    public static void repaintApp(){
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

    public static void newPage(int n){
        if (!pageStack.isEmpty() && pageStack.peek() == n)
            return;
        pageStack.push(n);
        if (pageStack.size() == 2){
            pageList[1] = new MainPage(app.getUserLoggedIn());
            panelList[0] = new MainPagePanel(app.getUserLoggedIn());
            panelList[1] = new ProfilePage(app.getUserLoggedIn());
            panelList[2] = new CoursesList(app.getUserLoggedIn());
            panelList[3] = new ProfsList(app.getUserLoggedIn());
            panelList[4] = new WeeklyPlanPage(app.getUserLoggedIn());
            panelList[5] = new ExamPlanPage(app.getUserLoggedIn());
            panelList[6] = new RequestsPage(app.getUserLoggedIn());
            panelList[7] = new TemporaryScoresPage(app.getUserLoggedIn());
            if (userLoggedIn.isStudent() || ((Professor)userLoggedIn).isDeputy())
                panelList[8] = new ReportCardPage(app.getUserLoggedIn());
        }
        repaintApp();
    }

    public static void remove(){
        if (pageStack.size() > 2) {
            pageStack.pop();
            repaintApp();
        }
    }

    public static void logOut(){
        while (pageStack.size() > 1)
            pageStack.pop();
        setUserLoggedIn(null);
        repaintApp();
    }
    
    public CaptchaLoader getCaptchaLoader() {
        return captchaLoader;
    }

    public static User getUserLoggedIn() {
        return userLoggedIn;
    }

    public static void setUserLoggedIn(User userLoggedIn) {
        if (Application.userLoggedIn == null)
            Data.writeToLog(null, " LOG IN " + userLoggedIn.getUniversityID());
        if (userLoggedIn == null)
            Data.writeToLog(null, " LOG OUT " + Application.userLoggedIn.getUniversityID());
        Application.userLoggedIn = userLoggedIn;
    }

    public static void saveUser(){
        Data.writeUser(Application.userLoggedIn);
        Data.writeToLog(userLoggedIn.getUniversityID(), "PROFILE UPDATE");
    }

    public static void addToTempScoreList(Score score){
        temporaryScoreList.add(score);
    }

    public static void setScores(){
        if (temporaryScoreList.isEmpty())
            return;
        for (Score score : temporaryScoreList){
            Student student = (Student) University.getUser(score.getStudentLinked());
            Course course = University.getCourse(score.getCourseLinked());
            student.addScore(score);
            course.addScore(score);
        }
    }

}
