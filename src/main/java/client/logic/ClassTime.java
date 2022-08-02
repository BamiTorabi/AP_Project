package client.logic;

import java.util.Arrays;

public class ClassTime {

    public static final String[] weekNames = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private int day;
    // 0 = Saturday, ..., 6 = Friday
    private int startHours;
    private int startMins;
    private int endHours;
    private int endMins;

    public final static ClassTime nullTime = new ClassTime();

    public ClassTime(){}

    public ClassTime(int day, int startHours, int startMins, int endHours, int endMins){
        this.day = day;
        this.startHours = startHours;
        this.startMins = startMins;
        this.endHours = endHours;
        this.endMins = endMins;
    }

    public ClassTime(String day, String startTime, String endTime){
        this.day = Arrays.binarySearch(weekNames, day);
        String[] start = startTime.split(":");
        this.startHours = Integer.parseInt(start[0]);
        this.startMins = Integer.parseInt(start[1]);
        String[] end = endTime.split(":");
        this.endHours = Integer.parseInt(end[0]);
        this.endMins = Integer.parseInt(end[1]);
    }

    public static String getDayName(int t){
        return weekNames[t];
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStartHours() {
        return startHours;
    }

    public void setStartHours(int startHours) {
        this.startHours = startHours;
    }

    public int getStartMins() {
        return startMins;
    }

    public void setStartMins(int startMins) {
        this.startMins = startMins;
    }

    public int getEndHours() {
        return endHours;
    }

    public void setEndHours(int endHours) {
        this.endHours = endHours;
    }

    public int getEndMins() {
        return endMins;
    }

    public void setEndMins(int endMins) {
        this.endMins = endMins;
    }

    @Override
    public String toString() {
        return weekNames[day] + ", " +
                String.format("%02d", startHours) + ":" + String.format("%02d", startMins) + " - " +
                String.format("%02d", endHours) + ":" + String.format("%02d", endMins);
    }
}
