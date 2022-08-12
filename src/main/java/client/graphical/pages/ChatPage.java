package client.graphical.pages;

import client.Application;
import client.DataLoader;
import client.graphical.templates.Chatbox;
import client.graphical.templates.PanelTemplate;
import client.logic.Chat;
import client.logic.Conversation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class ChatPage extends PanelTemplate {

    private Font bigBoldFont = new Font("Arial", Font.BOLD, 30);

    private JPanel convosPanel, chatPanel;
    private JButton newChatButton, sendButton;
    private Conversation chatSelected;
    private List<Conversation> convos;
    private Map<JButton, Conversation> map;
    private JScrollPane convosPane, chatPane;
    private JTextArea messageArea;
    private JLabel nameLabel;

    private final int LABEL_WIDTH = DataLoader.getConstraint("chatPanel", "labelWidth");
    private final int LABEL_HEIGHT = DataLoader.getConstraint("chatPanel", "labelHeight");
    private final int FILTER_WIDTH = DataLoader.getConstraint("chatPanel", "filterWidth");
    private final int CHAT_HEIGHT = DataLoader.getConstraint("chatPanel", "chatHeight");
    private final int MARGIN_SIZE = DataLoader.getConstraint("chatPanel", "marginSize");
    private final int BUTTON_SIZE = DataLoader.getConstraint("chatPanel", "buttonSize");

    public ChatPage(Application app, String userID) {
        super(app, userID);
        this.setLayout(null);
        this.convos = new ArrayList<>();
        this.map = new HashMap<>();
        addNewChatButton();
        addMessageBox();
    }

    public void addNewChatButton(){
        this.newChatButton = new JButton();
        this.newChatButton.setText("New chat");
        this.newChatButton.setBackground(Color.GREEN);
        this.newChatButton.setBounds(MARGIN_SIZE, MARGIN_SIZE, LABEL_WIDTH, LABEL_HEIGHT);
        this.newChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.askForInfo(14, app.getUserID());
            }
        });
        this.add(this.newChatButton);
    }

    public void addConversation(Conversation conversation){
        if (this.chatPane != null)
            remove(this.chatPane);
        messageArea.setEditable(false);
        if (conversation == null)
            return;
        messageArea.setEditable(true);

        if (this.nameLabel != null)
            remove(this.nameLabel);
        this.nameLabel = new JLabel(conversation.getOtherName());
        this.nameLabel.setFont(bigBoldFont);
        this.nameLabel.setBounds(FILTER_WIDTH + MARGIN_SIZE, 0, WIDTH - FILTER_WIDTH - 2 * MARGIN_SIZE, LABEL_HEIGHT);
        this.add(this.nameLabel);

        this.chatPanel = new JPanel(new GridBagLayout());
        this.chatPanel.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        ArrayList<Chat> chats = conversation.getChats();
        Collections.reverse(chats);
        gbc.gridy = 0;
        for (Chat chat : chats){
            Chatbox box = new Chatbox(userID, chat);
            box.setSize(WIDTH - MARGIN_SIZE - FILTER_WIDTH, getHeight());
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            this.chatPanel.add(box, gbc);
            gbc.gridy++;
        }
        Collections.reverse(chats);
        this.chatPane = new JScrollPane(this.chatPanel);
        this.chatPane.setAlignmentX(LEFT_ALIGNMENT);
        this.chatPane.setBounds(FILTER_WIDTH + MARGIN_SIZE, LABEL_HEIGHT, WIDTH - FILTER_WIDTH - 2 * MARGIN_SIZE, CHAT_HEIGHT);
        this.add(this.chatPane);
        revalidate();
        repaint();
    }

    public void addMessageBox(){
        this.messageArea = new JTextArea();
        this.messageArea.setBounds(FILTER_WIDTH + 2 * MARGIN_SIZE, LABEL_HEIGHT + CHAT_HEIGHT + MARGIN_SIZE, WIDTH - FILTER_WIDTH - 5 * MARGIN_SIZE, HEIGHT - MARGIN_SIZE - CHAT_HEIGHT);
        this.messageArea.setEditable(false);
        this.add(this.messageArea);

        this.sendButton = new JButton();
        this.sendButton.setIcon(new ImageIcon("resources/icons/send.png"));
        this.sendButton.setBorderPainted(false);
        this.sendButton.setBackground(Color.CYAN);
        this.sendButton.setBounds(WIDTH - 3 * MARGIN_SIZE, LABEL_HEIGHT + CHAT_HEIGHT + MARGIN_SIZE, BUTTON_SIZE, BUTTON_SIZE);
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageArea.getText();
                if (message.equals("") || chatSelected == null)
                    return;
                app.addNewChat(userID, chatSelected.getOtherID(), message);
                messageArea.setText("");
                app.askForInfo(11, userID);
            }
        });
        this.add(this.sendButton);
    }

    public void addConvoButtons(){
        if (this.convosPane != null)
            remove(this.convosPane);
        this.convosPanel = new JPanel(new GridBagLayout());
        this.map = new HashMap<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        for (Conversation conversation : convos){
            JButton button = new JButton();
            String lastMessage = conversation.getLastMessage();
            if (lastMessage.length() > 20)
                lastMessage = lastMessage.substring(0, 17) + "...";
            button.setText("<HTML>" + conversation.getOtherName() + "<BR>" + lastMessage + "</HTML>");
            button.setSize(FILTER_WIDTH, BUTTON_SIZE);
            button.setContentAreaFilled(false);
            button.addActionListener(new ChatListener());
            map.put(button, conversation);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            this.convosPanel.add(button, gbc);
            gbc.gridy++;
        }
        this.convosPane = new JScrollPane(this.convosPanel);
        this.convosPane.setOpaque(true);
        this.convosPane.setBounds(MARGIN_SIZE, LABEL_HEIGHT + 2 * MARGIN_SIZE, FILTER_WIDTH, HEIGHT - LABEL_HEIGHT - 2 * MARGIN_SIZE);
        this.add(this.convosPane);
    }

    @Override
    public void refreshPanel(String info) {
        if (lastUpdate.equals(info))
            return;
        lastUpdate = info;
        convos = app.unpackConversations(info);
        addConvoButtons();
        if (chatSelected != null){
            Conversation convoSelected = null;
            for (Conversation convo : convos)
                if (convo.getOtherID().equals(chatSelected.getOtherID()))
                    convoSelected = convo;
            addConversation(convoSelected);
        }
    }

    public class ChatListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            chatSelected = map.get((JButton) e.getSource());
            addConversation(chatSelected);
            revalidate();
            repaint();
        }
    }
}