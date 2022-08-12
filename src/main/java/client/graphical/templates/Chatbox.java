package client.graphical.templates;

import client.logic.Chat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class Chatbox extends JTextPane {

    private String userID;
    private Chat chat;
    private boolean you;
    private String message;
    private final static int MAX_SIZE = 800;
    private SimpleAttributeSet attributeSet = new SimpleAttributeSet();
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final static Color lightBlue = new Color(0xABECE7);
    private final static Color lightGreen = new Color(0x94E7AB);
    private Color colour;

    public Chatbox(String userID, Chat chat){
        setEditable(false);
        setMaximumSize(new Dimension(600, 600));
        this.userID = userID;
        unloadChat(chat);

        this.colour = (you ? lightGreen : lightBlue);
        setBackground(this.colour);

        setMessage();
    }

    public void unloadChat(Chat chat){
        this.chat = chat;
        this.you = chat.getSender().equals(this.userID);
    }

    public void setMessage(){
        try {
            this.setCharacterAttributes(attributeSet, true);
            Document doc = this.getStyledDocument();

            this.attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 14);
            doc.insertString(doc.getLength(), chat.getTimeSent().format(formatter) + "\n", attributeSet);

            this.attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 18);
            doc.insertString(doc.getLength(), chat.getMessage(), attributeSet);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isYou() {
        return you;
    }
}
