package process;

import java.time.LocalDateTime;

public class Request {

    private String requestID;
    private String studentID;
    private String profID;
    private String text;
    private RequestStatus status;
    private String timeSent;

    public Request(String studentID, String profID, String text, RequestStatus status){
        this.studentID = studentID;
        this.profID = profID;
        this.text = text;
        this.status = status;
        LocalDateTime creationTime = LocalDateTime.now();
        this.timeSent = creationTime.toString();
        this.requestID = String.format("%04d%02d%02d%02d%02d%02d", creationTime.getYear(), creationTime.getMonth(), creationTime.getDayOfMonth(), creationTime.getHour(), creationTime.getMinute(), creationTime.getSecond());
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getProfID() {
        return profID;
    }

    public void setProfID(String profID) {
        this.profID = profID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
