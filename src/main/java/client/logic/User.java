package client.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User {

    private String firstName;
    private String lastName;
    private String nationalID;
    private String phoneNumber;
    private String emailAddress;
    private String universityID;
    private String password;
    private int firstYear;
    private CollegeType college;
    private String userType;
    private List<Score> courseList;

    public User(){
        this.courseList = new ArrayList<>();
    }

    public User(String firstName, String lastName, String ID, String phoneNumber, String emailAddress, CollegeType type, String userType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalID = ID;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.college = type;
        //College col = College.getInstance(type);
        this.userType = userType;
        this.firstYear = Calendar.getInstance().get(Calendar.YEAR);
        /*this.universityID = ("" + firstYear) +
                String.format("%02d", type.ordinal()) +
                (student ? "0" : "1") +
                ("" + String.format("%03d",isStudent ? col.getLastStudentID() : col.getLastProfessorID()));*/
        this.password = this.universityID;
        this.courseList = new ArrayList<>();
        // id = first year in uni + college number + (0 for student or 1 for professor) + the next available id in their college.
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNationalID() {
        return nationalID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getUniversityID() {
        return universityID;
    }

    public int getFirstYear() {
        return firstYear;
    }

    public CollegeType getCollege() {
        return college;
    }

    public String getUserType() {
        return userType;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setUniversityID(String universityID) {
        this.universityID = universityID;
    }

    public void setFirstYear(int firstYear) {
        this.firstYear = firstYear;
    }

    public void setCollegeType(CollegeType college) {
        this.college = college;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Score> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Score> courseList) {
        this.courseList = courseList;
    }

    public void addScore(Score score){
        this.courseList.add(score);
    }

    public String giveName(){
        return this.firstName + " " + this.lastName;
    }


}
