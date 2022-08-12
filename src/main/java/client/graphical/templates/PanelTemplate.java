package client.graphical.templates;

import client.Application;
import client.DataLoader;

import javax.swing.*;

public abstract class PanelTemplate extends JPanel {

    protected String userID;
    protected Application app;
    protected String lastUpdate;
    protected final int WIDTH;
    protected final int HEIGHT;

    public PanelTemplate(Application app, String userID){
        this.app = app;
        this.userID = userID;
        this.lastUpdate = "";
        this.WIDTH = DataLoader.getConstraint("profilePanel", "width");
        this.HEIGHT = DataLoader.getConstraint("profilePanel", "height");
    }

    public abstract void refreshPanel(String info);

}
