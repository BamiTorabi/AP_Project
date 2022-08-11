package client.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Conversation implements Comparable<Conversation> {

    private String userID;
    private String otherID;
    private String lastMessage;
    private LocalDateTime lastTime;
    private ArrayList<Chat> chats;

    public Conversation(){
        chats = new ArrayList<>();
        lastMessage = "";
        lastTime = LocalDateTime.MIN;
    }

    public void addChat(Chat chat){
        chats.add(chat);
        if (chat.getTimeSent().isAfter(lastTime)) {
            lastTime = chat.getTimeSent();
            lastMessage = chat.getMessage();
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }

    public String getOtherID() {
        return otherID;
    }

    public void setOtherID(String otherID) {
        this.otherID = otherID;
    }

    @Override
    public int compareTo(Conversation o) {
        return this.lastTime.compareTo(o.getLastTime());
    }
}
