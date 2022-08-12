package client.graphical.templates;

import client.Application;

import javax.swing.*;

public abstract class PageTemplate extends JFrame {

    protected Application app;

    public PageTemplate(Application app){
        this.app = app;
    }

    public abstract void refreshPage(String info);

}
