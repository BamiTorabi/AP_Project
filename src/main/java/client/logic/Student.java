package client.logic;

public class Student extends User{

    private String counsellor;
    private StudentType type;
    private double totalScore;
    private int educationalStatus; // -1 = dropout, 0 = currently studying, 1 = graduated

    public Student(){
        super();
    }

    public Student(String firstName, String lastName, String ID, String phoneNumber, String emailAddress, CollegeType college, String counsellor, StudentType type){
        super(firstName, lastName, ID, phoneNumber, emailAddress, college, "Student");
        this.counsellor = counsellor;
        this.type = type;
        this.educationalStatus = 0;
        this.totalScore = 0;
    }

    public String getCounsellor() {
        return counsellor;
    }

    public void setCounsellor(String counsellor) {
        this.counsellor = counsellor;
    }

    public StudentType getType() {
        return type;
    }

    public void setType(StudentType type) {
        this.type = type;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getEducationalStatus() {
        return educationalStatus;
    }

    public void setEducationalStatus(int educationalStatus) {
        this.educationalStatus = educationalStatus;
    }

    @Override
    public String toString() {
        return "Student " + getFirstName() + " " + getLastName();
    }
}
