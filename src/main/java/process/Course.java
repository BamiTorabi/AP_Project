package process;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String courseName;
    private CollegeType collegeLinked;
    private String professorID;
    private List<Score> scoreList;
    private double meanScore;
    private int units;
    private ClassTime[] classTimes;
    private String examTime;
    private ClassLevel level;

    private String courseID;

    public Course(){}

    public Course(String courseName, String prof, int units, CollegeType college, ClassTime[] times, String examDate, String examTime, ClassLevel level){
        this.courseName = courseName;
        this.professorID = prof;
        this.scoreList = new ArrayList<>();
        this.meanScore = 0;
        this.units = units;
        this.collegeLinked = college;
        this.classTimes = times;
        this.examTime = createDateTime(examDate, examTime);
        this.level = level;
        College temp = College.getInstance(college);
        this.courseID = String.format("%%02d", college.getNumber()) + String.format("%03d", temp.getLastCourseID());
    }

    public static String createDateTime(String date, String time){
        return date + " " + time;
    }

    public double getMeanScore() {
        return meanScore;
    }

    public void setMeanScore(double meanScore) {
        this.meanScore = meanScore;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public CollegeType getCollegeLinked() {
        return collegeLinked;
    }

    public void setCollegeLinked(CollegeType collegeLinked) {
        this.collegeLinked = collegeLinked;
    }

    public ClassLevel getLevel() {
        return level;
    }

    public void setLevel(ClassLevel level) {
        this.level = level;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getProfessorID() {
        return professorID;
    }

    public void setProfessorID(String professorID) {
        this.professorID = professorID;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public List<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public void addScore(Score score){
        this.scoreList.add(score);
    }

    @JsonIgnore
    public String getClassTimeStrings(){
        String S = "";
        for (ClassTime time : this.classTimes){
            S += time.toString();
            S += "\n";
        }
        return S;
    }

    public ClassTime[] giveClassTimes(){
        return this.classTimes;
    }

}
