package client.graphical;

import client.Application;

import javax.swing.*;

public abstract class DialogTemplate extends JDialog {

    protected String userID;
    protected Application app;
    protected String lastUpdate;

    public DialogTemplate(Application app, String userID){
        this.app = app;
        this.userID = userID;
        this.lastUpdate = "";
    }

    public abstract void refreshDialog(String info);

}