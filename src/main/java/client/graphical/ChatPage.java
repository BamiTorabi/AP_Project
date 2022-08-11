package client.graphical;

import client.Application;
import client.logic.Conversation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ChatPage extends PanelTemplate{

    private JPanel convosPanel, chatPanel;
    private JButton newChatButton = new JButton();
    private List<Conversation> convos;
    private Map<JButton, Conversation> map;
    private JScrollPane convosPane, chatPane;

    private final int SCROLL_WIDTH = 200;
    private final int HEIGHT = 600;
    private final int LABEL_WIDTH = 150;
    private final int LABEL_HEIGHT = 50;
    private final int MARGIN_SIZE = 25;

    public ChatPage(Application app, String userID) {
        super(app, userID);
        this.setLayout(null);
        this.convos = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public void addNewChatButton(){
        this.newChatButton = new JButton();
        this.newChatButton.setText("New chat");
        this.newChatButton.setBackground(Color.GREEN);
        this.newChatButton.setBounds(MARGIN_SIZE, MARGIN_SIZE, LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.newChatButton);
    }

    public void addConversation(Conversation conversation){
        this.chatPanel = new JPanel();

    }

    public void addConvoButtons(){
        this.convosPanel = new JPanel();
        for (Conversation conversation : convos){
            JButton button = new JButton();
            String lastMessage = conversation.getLastMessage();
            if (lastMessage.length() > 20)
                lastMessage = lastMessage.substring(0, 17) + "...";
            button.setText("<HTML>" + conversation.getOtherID() + "<BR>" + lastMessage + "</HTML>");
            button.setSize(180, 50);
            button.setContentAreaFilled(false);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton butt = (JButton) e.getSource();
                    if (butt.isSelected()) {
                        addConversation(map.get(butt));
                        setForeground(Color.RED);
                    }
                }
            });
            map.put(button, conversation);
            this.convosPanel.add(button);
        }
        this.convosPane = new JScrollPane(this.convosPanel);
        this.convosPane.setOpaque(true);
        this.convosPane.setBackground(Color.RED);
        this.convosPane.setBounds(10, LABEL_HEIGHT + 2 * MARGIN_SIZE, LABEL_WIDTH + 2 * MARGIN_SIZE - 10, HEIGHT - LABEL_HEIGHT - 2 * MARGIN_SIZE);
        this.add(this.convosPane);
    }

    @Override
    public void refreshPanel(String info) {
        this.removeAll();
        this.convos = app.unpackConversations(info);
        this.addNewChatButton();
        this.addConvoButtons();
    }
}
