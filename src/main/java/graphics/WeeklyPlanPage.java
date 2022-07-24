package graphics;

import process.*;

import javax.swing.*;
import java.util.List;

public class WeeklyPlanPage extends JPanel {

    private int MARGIN_WIDTH = 100;
    private int MARGIN_HEIGHT = 35;
    private int LABEL_WIDTH = 32;
    private int LABEL_HEIGHT = 95;

    private User userLoggedIn;

    public WeeklyPlanPage(User user){
        super();
        this.userLoggedIn = user;
        this.setLayout(null);

        setHeaders();
        addCourses();
    }

    public int getCoor(double x, boolean isHorizontal){
        if (isHorizontal){
            return (int)(MARGIN_WIDTH + x * LABEL_WIDTH);
        } else{
            return (int)(MARGIN_HEIGHT + x * LABEL_HEIGHT);
        }
    }

    public void setHeaders(){
        for (int i = 7; i < 20; i++){
            JLabel numberLabel = new JLabel(String.format("%02d", i) + ":00");
            numberLabel.setBounds(getCoor(2 * (i - 7), true), 0, 2 * LABEL_WIDTH, MARGIN_HEIGHT);
            this.add(numberLabel);
        }
        for (int i = 0; i < 7; i++){
            JLabel dayLabel = new JLabel(ClassTime.getDayName(i));
            dayLabel.setBounds(0, getCoor(i, false), MARGIN_WIDTH, LABEL_HEIGHT);
            this.add(dayLabel);
        }
    }

    public void addCourses(){
        List<Score> scoreList = userLoggedIn.getCourseList();
        if (scoreList.isEmpty())
            return;
        for (Score score : scoreList){
            int collegeNum = Integer.parseInt(score.getCourseLinked().substring(0, 2));
            College college = College.getInstance(CollegeType.values()[collegeNum]);
            Course course = college.getCourse(score.getCourseLinked());
            for (ClassTime time : course.giveClassTimes()){
                JTextArea timeText = new JTextArea(course.getCourseName() + "\n" + time.toString().split(", ")[1]);
                timeText.setEditable(false);
                timeText.setLineWrap(true);
                timeText.setWrapStyleWord(true);
                double start = time.getStartHours() + time.getStartMins() / 60.0;
                double end = time.getEndHours() + time.getEndMins() / 60.0;
                int startCoor = getCoor(2 * (start - 7), true);
                int endCoor = getCoor(2 * (end - 7), true);
                timeText.setBounds(startCoor, getCoor(time.getDay(), false), endCoor - startCoor, LABEL_HEIGHT);
                this.add(timeText);
            }
        }
    }

}
