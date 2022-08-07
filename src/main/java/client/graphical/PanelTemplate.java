package client.graphical;

import client.Application;

import javax.swing.*;

public abstract class PanelTemplate extends JPanel {

    protected String userID;
    protected Application app;
    protected String lastUpdate;

    public PanelTemplate(Application app, String userID){
        this.app = app;
        this.userID = userID;
        this.lastUpdate = "";
    }

    public abstract void refreshPanel(String info);

}
