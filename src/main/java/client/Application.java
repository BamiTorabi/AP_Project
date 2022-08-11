package client;

import client.graphical.*;
import client.logic.*;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Application implements Runnable {

    private final Client client;
    private String userID = "";
    private User userLoggedIn = null;
    private final Stack<String> pageStack;
    private PanelTemplate panel;
    private PageTemplate page;
    private DialogTemplate dialog;
    private int captchaNumber = 0;
    private ImageIcon captchaIcon;
    private Updater updater;
    private Thread updateThread;

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
            if (pageNumber == 12){
                dialog.setVisible(true);
                dialog.revalidate();
                dialog.repaint();
                return;
            }
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
        if (!pageStack.isEmpty() && pageStack.peek().equals(temp))
            return;
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
        }
        else {
            if (!(page instanceof MainPage))
                page = new MainPage(this);
            switch (pageNumber) {
                case 1:
                    if (!(panel instanceof MainPagePanel))
                        panel = new MainPagePanel(this, userID);
                    break;
                case 2:
                    if (!(panel instanceof ProfilePage))
                        panel = new ProfilePage(this, userID);
                    break;
                case 3:
                    if (!(panel instanceof CoursesList))
                        panel = new CoursesList(this, userID);
                    break;
                case 4:
                    if (!(panel instanceof ProfsList))
                        panel = new ProfsList(this, userID);
                    break;
                case 5:
                    if (!(panel instanceof WeeklyPlanPage))
                        panel = new WeeklyPlanPage(this, userID);
                    break;
                case 6:
                    if (!(panel instanceof ExamPlanPage))
                        panel = new ExamPlanPage(this, userID);
                    break;
                case 8:
                    if (!(panel instanceof TemporaryScoresPage))
                        panel = new TemporaryScoresPage(this, userID);
                    break;
                case 9:
                    if (!(panel instanceof ReportCardPage))
                        panel = new ReportCardPage(this, userID);
                    break;
                case 10:
                    if (!(panel instanceof NotificationPage))
                        panel = new NotificationPage(this, userID);
                    break;
                case 12:
                    if (!(dialog instanceof AddCourseDialog)) {
                        dialog = new AddCourseDialog(this, userID);
                    }
                    dialog.refreshDialog(pageInfo);
                    repaintApp();
                    return;
        /*panelList[6] = new RequestsPage();
        panelList[7] = new TemporaryScoresPage();
        if (userLoggedIn.isStudent() || ((Professor)userLoggedIn).isDeputy())
            panelList[8] = new ReportCardPage(userLoggedIn);*/
            }
            panel.refreshPanel(pageInfo);
        }
        page.refreshPage(pageInfo);
        repaintApp();
    }

    public void remove(){
        if (pageStack.size() > 2) {
            pageStack.pop();
            updater.popQuery();
            pageStack.pop();
            String query = updater.getLastQuery();
            updater.popQuery();
            askForInfo(getPageNumber(query), query.substring(3));
        }
    }

    public void logOut(){
        while (pageStack.size() > 1) {
            pageStack.pop();
            updater.popQuery();
        }
        setUserID("");
        askForInfo(0, String.format("%04d", captchaNumber));
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void sendUserUpdate(String userID, String fieldName, String newValue) throws IOException {
        client.send("UPDATE/USER/\"" + userID + "\"/" + fieldName + "/\"" + newValue + "\"");
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

    public User getUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(User userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
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

    public List<Course> unpackCourseList(String info){
        if (info == null || info.equals(""))
            return null;
        ArrayList<Course> courses = new ArrayList<>();
        String[] S = info.split("\\$");
        for (String row : S){
            Course course = new Course();
            for (String entry : row.split("/")){
                String[] T = entry.split(":");
                switch (T[0]){
                    case "courseID":
                        course.setCourseID(T[1]);
                        break;
                    case "courseName":
                        course.setCourseName(T[1]);
                        break;
                    case "professorName":
                    case "professorID":
                        course.setProfessorID(T[1]);
                        break;
                    case "units":
                        course.setUnits(Integer.parseInt(T[1]));
                        break;
                    case "enrolled":
                        course.setEnrolled(Integer.parseInt(T[1]));
                        break;
                    case "classTime":
                        String[] sessions = entry.substring(10).split(",");
                        List<ClassTime> times = new ArrayList<>();
                        for (String R : sessions){
                            ClassTime time = new ClassTime();
                            String[] P = R.split(" ");
                            time.setDay(Integer.parseInt(P[0]));
                            time.setStartHours(Integer.parseInt(P[1].split(":")[0]));
                            time.setStartMins(Integer.parseInt(P[1].split(":")[1]));
                            time.setEndHours(Integer.parseInt(P[2].split(":")[0]));
                            time.setEndMins(Integer.parseInt(P[2].split(":")[1]));
                            times.add(time);
                        }
                        course.setClassTimes(times.toArray(new ClassTime[0]));
                        break;
                    case "examTime":
                        course.setExamTime(String.join(" ", entry.substring(9).split("T")));
                        break;
                    case "studentList":
                        String[] IDs = entry.substring(12).split(",");
                        for (String id : IDs){
                            Score score = new Score();
                            score.setStudentLinked(id);
                            course.addScore(score);
                        }
                        break;
                    case "level":
                        course.setLevel(ClassLevel.valueOf(T[1]));
                        break;
                }
            }
            courses.add(course);
        }
        return courses;
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

    public List<Professor> unpackProfessorList(String info){
        if (info.equals(""))
            return null;
        ArrayList<Professor> professors = new ArrayList<>();
        String[] S = info.split("\\$");
        for (String row : S){
            User user = unpackUser(row);
            if (user instanceof Professor)
                professors.add((Professor) user);
        }
        return professors;
    }

    public List<Score> unpackScoreList(String info){
        if (info.equals(""))
            return null;
        ArrayList<Score> scores = new ArrayList<>();
        String[] S = info.split("\\$");
        for (String row : S){
            Score score = new Score();
            for (String entry : row.split("/")){
                String[] T = entry.split(":");
                if (T.length == 1){
                    T = new String[]{T[0], ""};
                }
                switch (T[0]){
                    case "courseLinked":
                        score.setCourseLinked(T[1]);
                        break;
                    case "courseName":
                        score.setCourseName(T[1]);
                        break;
                    case "professorName":
                        score.setProfessorName(T[1]);
                        break;
                    case "studentName":
                        score.setStudentName(T[1]);
                        break;
                    case "universityID":
                        score.setStudentLinked(T[1]);
                        break;
                    case "status":
                        score.setStatus(ScoreStatus.valueOf(T[1]));
                        break;
                    case "value":
                        score.setValue(Double.parseDouble(T[1]));
                        break;
                    case "studentProtest":
                        score.setStudentProtest(T[1]);
                        break;
                    case "professorAnswer":
                        score.setProfessorAnswer(T[1]);
                        break;
                    case "units":
                        score.setUnits(Integer.parseInt(T[1]));
                        break;
                }
            }
            scores.add(score);
        }
        return scores;
    }

    public List<Notif> unpackNotifList(String info) {
        if (info.equals(""))
            return null;
        ArrayList<Notif> notifs = new ArrayList<>();
        String[] S = info.split("\\$");
        for (String row : S) {
            Notif notif = new Notif();
            for (String entry : row.split("/")) {
                String[] T = entry.split(":");
                if (T.length == 1) {
                    T = new String[]{T[0], ""};
                }
                switch (T[0]) {
                    case "sent":
                        notif.setSent(String.join(" ", entry.substring(5).split("T")));
                        break;
                    case "seen":
                        notif.setSeen(Boolean.parseBoolean(T[1]));
                        break;
                    case "title":
                        notif.setTitle(T[1]);
                        break;
                    case "message":
                        notif.setMessage(T[1]);
                        break;
                }
            }
            notifs.add(notif);
        }
        return notifs;
    }

    public void unpackMessage(int pageNumber, String info){
        String[] S = info.split("/");
        switch (pageNumber){
            case 0:
                int x = Integer.parseInt(S[0]);
                client.receiveCaptcha(x, info.substring(5));
                setCaptcha(x, new ImageIcon(String.format("captcha%04d.jpg", x)));
                break;
            case 1:
                userLoggedIn = unpackUser(info);
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
                break;
            case 9:
            case 10:
            case 12:
                System.err.println(info);
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

    public void messageRead(int pageNumber, String ID, boolean read){
        String table = (pageNumber == 10 ? "Notifications" : "Chats");
        String message = String.format("UPDATE/%s/%s/%s", table, ID, read);
        this.updater.addQuery(String.format("INFO/%02d/%s", pageNumber, userID));
        try{
            this.client.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewStudent(Student student){
        String message = "";
        message += "\"" + student.getFirstName() + "\"/";
        message += "\"" + student.getLastName() + "\"/";
        message += "\"" + student.getNationalID() + "\"/";
        message += "\"" + student.getPhoneNumber() + "\"/";
        message += "\"" + student.getEmailAddress() + "\"/";
        message += "\"" + student.getUniversityID() + "\"/";
        message += "\"" + student.getPassword() + "\"/";
        message += student.getFirstYear() + "/";
        message += "\"" + student.getCollege() + "\"/";
        message += "\"" + student.getType() +"\"/";
        message += student.getTotalScore() + "/";
        message += student.getEducationalStatus() + "/";
        message += student.isStudent() + "/";
        message += "\"" + student.getCounsellor() + "\"";
        try{
            this.client.send("ADD/STUDENT/" + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewProfessor(Professor professor){
        String message = "";
        message += "\"" + professor.getFirstName() + "\"/";
        message += "\"" + professor.getLastName() + "\"/";
        message += "\"" + professor.getNationalID() + "\"/";
        message += "\"" + professor.getPhoneNumber() + "\"/";
        message += "\"" + professor.getEmailAddress() + "\"/";
        message += "\"" + professor.getUniversityID() + "\"/";
        message += "\"" + professor.getPassword() + "\"/";
        message += professor.getFirstYear() + "/";
        message += "\"" + professor.getCollege() + "\"/";
        message += "\"" + professor.getType() + "\"/";
        message += professor.getRoomNumber() + "/";
        message += professor.isDeputy() + "/";
        message += professor.isHead() + "/";
        message += professor.isStudent();
        try{
            this.client.send("ADD/PROFESSOR/" + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateScore(Score score){
        String message = "";
        message += "\"" + score.getCourseLinked() + "\"/";
        message += "\"" + score.getStudentLinked() + "\"/";
        message += score.getValue() + "/";
        message += "\"" + score.getStatus() + "\"/";
        message += "\"" + score.getStudentProtest() + "\"/";
        message += "\"" + score.getProfessorAnswer() + "\"";
        try{
            this.client.send("UPDATE/SCORE/" + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCourse(Course course){
        String message = "";
        message += "\"" + course.getCourseName() + "\"/";
        message += "\"" + course.getCollegeLinked() + "\"/";
        message += "\"" + course.getLevel() + "\"/";
        message += "\"" + course.getCourseID() + "\"/";
        message += "\"" + course.getExamTime() + "\"/";
        message += course.getUnits() + "/";
        message += "\"" + course.getProfessorID() + "\"";
        ClassTime[] times =  course.getClassTimes();
        for (ClassTime time : times){
            message += (time == times[0] ? "/" : ",") + time.getDay() + " \"" + time.getStartTime() + "\" \"" + time.getEndTime() + "\"";
        }
        ArrayList<Score> scores = (ArrayList<Score>) course.getScoreList();
        for (Score score : scores){
            message += (score.equals(scores.get(0)) ? "/\"" : ",\"") + score.getStudentLinked() + "\"";
        }
        try{
            this.client.send("UPDATE/COURSE/" + message);
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

    public void raiseError(String errorMessage){
        if (getPageNumber(pageStack.peek()) == 12)
            JOptionPane.showMessageDialog(dialog, errorMessage);
        else
            JOptionPane.showMessageDialog(page, errorMessage);
    }
    
    @Override
    public void run() {
        askForInfo(0, "0000");
        this.updateThread = new Thread(this.updater);
        this.updateThread.start();
    }
}
