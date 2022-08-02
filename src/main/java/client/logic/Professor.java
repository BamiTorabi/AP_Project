package client.logic;

import java.util.ArrayList;
import java.util.List;

public class Professor extends User{

    private int roomNumber;
    private ProfessorType type;
    private List<Score> courseList;
    private boolean deputy;
    private boolean head;

    public Professor(){
        super();
    }

    public Professor(String firstName, String lastName, String ID, String phoneNumber, String emailAddress, CollegeType college, int room, ProfessorType type, boolean isDeputy, boolean isHead){
        super(firstName, lastName, ID, phoneNumber, emailAddress, college, false);
        this.roomNumber = room;
        this.type = type;
        this.deputy = isDeputy;
        this.head = isHead;
        this.courseList = new ArrayList<>();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public ProfessorType getType() {
        return type;
    }

    public void setType(ProfessorType type) {
        this.type = type;
    }

    public boolean isDeputy() {
        return deputy;
    }

    public void setDeputy(boolean deputy) {
        this.deputy = deputy;
    }

    public boolean isHead() {
        return head;
    }

    public void setHead(boolean head) {
        this.head = head;
    }
}
