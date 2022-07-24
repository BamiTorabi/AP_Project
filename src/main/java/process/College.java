package process;

import java.util.ArrayList;
import java.util.List;

public class College {

    private static College[] colleges = new College[CollegeType.values().length];
    private CollegeType type;
    private List<Student> studentList;
    private List<Professor> professorList;
    private List<Course> courseList;

    private Professor deputy = null;
    private Professor head = null;

    private int lastStudentID;
    private int lastProfessorID;
    private int lastCourseID;

    private College (CollegeType type){
        this.type = type;
        this.studentList = new ArrayList<>();
        this.professorList = new ArrayList<>();
        this.courseList = new ArrayList<>();
    }

    public static College getInstance(CollegeType type){
        if (colleges[type.ordinal()] == null){
            colleges[type.ordinal()] = new College(type);
        }
        return colleges[type.ordinal()];
    }

    public User getUser(String username, String password){
        for (User user : this.studentList){
            if (user.getUniversityID().equals(username) && user.getPassword().equals(password))
                return user;
        }
        for (User user : this.professorList){
            if (user.getUniversityID().equals(username) && user.getPassword().equals(password))
                return user;
        }
        return null;
    }

    public User getUser(String userID){
        for (User user : this.studentList){
            if (userID.equals(user.getUniversityID()))
                return user;
        }
        for (User user : this.professorList){
            if (userID.equals(user.getUniversityID()))
                return user;
        }
        return null;
    }

    public Course getCourse(String courseID){
        for (Course course : this.courseList){
            if (courseID.equals(course.getCourseID()))
                return course;
        }
        return null;
    }

    public Professor getDeputy() {
        return deputy;
    }

    public void setDeputy(Professor deputy) {
        this.deputy = deputy;
    }

    public Professor getHead() {
        return head;
    }

    public void setHead(Professor head) {
        this.head = head;
    }

    public CollegeType getType() {
        return type;
    }

    public void setType(CollegeType type) {
        this.type = type;
    }

    public int getLastStudentID() {
        return this.lastStudentID++;
    }

    public int getLastCourseID() {
        return this.lastCourseID++;
    }

    public int getLastProfessorID() {
        return this.lastProfessorID++;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<Professor> getProfessorList() {
        return professorList;
    }

    public void setProfessorList(List<Professor> professorList) {
        this.professorList = professorList;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void addToStudentList(Student student){
        this.studentList.add(student);
    }

    public void addToProfList(Professor prof){
        this.professorList.add(prof);
    }

    public void addToCourseList(Course course) {
        this.courseList.add(course);
    }

}
