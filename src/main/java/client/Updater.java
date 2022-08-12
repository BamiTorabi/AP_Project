package client;

import java.time.Clock;
import java.util.Stack;

public class Updater implements Runnable{

    private Application app;
    private final Clock clock;
    private Stack<String> pageStack;
    private long prevUpdateTime = 0;
    private final long threshold = 2000;
    private volatile boolean dead = false;

    public Updater(Application app){
        this.app = app;
        this.clock = Clock.systemDefaultZone();
        this.pageStack = new Stack<>();
    }

    synchronized public void addQuery(String s){
        if (!this.pageStack.isEmpty() && s.equals(this.pageStack.peek()))
            return;
        if (!this.pageStack.isEmpty() && app.getPageNumber(s) == app.getPageNumber(this.pageStack.peek()))
            this.pageStack.pop();
        this.pageStack.add(s);
    }

    synchronized public void popQuery(){
        this.pageStack.pop();
    }

    synchronized public String getLastQuery(){
        return this.pageStack.peek();
    }

    synchronized public Stack<String> getPageStack() {
        return pageStack;
    }

    @Override
    public void run() {
        prevUpdateTime = clock.millis();
        while (!dead){
            if (clock.millis() - prevUpdateTime > threshold){
                prevUpdateTime = clock.millis();
                synchronized (this) {
                    String query = pageStack.peek();
                    if (!query.substring(3).equals("") && app.getPageNumber(query) > 0) {
                        app.askForInfo(app.getPageNumber(query), query.substring(3));
                    }
                }
            }
        }
    }
}
