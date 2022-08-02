package client.logic;


public class Score{

    private String studentLinked;
    private String courseLinked;
    private double value;
    private ScoreStatus status;

    private String studentProtest;
    private String professorAnswer;

    public Score(){
        this.studentProtest = "";
        this.professorAnswer = "";
    }

    public Score(String studentID, String courseID, double number){
        this.studentLinked = studentID;
        this.courseLinked = courseID;
        this.value = normalize(number, 4);
        this.status = ScoreStatus.PENDING;
        this.studentProtest = "";
        this.professorAnswer = "";
    }

    // returns score rounded to the nearest "1 / precision"th.
    public static double normalize(double x, double precision){
        x *= precision;
        x += 0.5;
        x -= (x % 1);
        x /= precision;
        return x;
    }

    public String getStudentLinked() {
        return studentLinked;
    }

    public void setStudentLinked(String studentLinked) {
        this.studentLinked = studentLinked;
    }

    public String getCourseLinked() {
        return courseLinked;
    }

    public void setCourseLinked(String courseLinked) {
        this.courseLinked = courseLinked;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = normalize(value, 4);
    }

    public ScoreStatus getStatus() {
        return status;
    }

    public void setStatus(ScoreStatus status) {
        this.status = status;
    }

    public String getStudentProtest() {
        return studentProtest;
    }

    public void setStudentProtest(String studentProtest) {
        this.studentProtest = studentProtest;
    }

    public String getProfessorAnswer() {
        return professorAnswer;
    }

    public void setProfessorAnswer(String professorAnswer) {
        this.professorAnswer = professorAnswer;
    }
}
