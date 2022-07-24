package data;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import process.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Data {

    public static String path = "resources";
    public static ObjectMapper jsonWriter = new ObjectMapper();
    public static ObjectMapper jsonReader = new ObjectMapper();
    public static File userFile;
    public static File courseFile;
    public static File requestFile;
    public static File logFile;
    public static File directory;
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static void initialize(){
        jsonWriter.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        jsonReader.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        jsonWriter.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        logFile = new File(path + "/LOG.txt");
        if (!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeToLog(null, " APPLICATION STARTED");
    }

    public static void writeUser(User user){
        userFile = new File(path + "/users/" + user.getUniversityID() + ".json");
        if (!userFile.exists()){
            try {
                userFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonWriter.writerWithDefaultPrettyPrinter().writeValue(userFile, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readUsers(){
        directory = new File(path + "/users");
        File[] users = directory.listFiles();
        if (users == null || users.length == 0)
            return;
        for (File file : users){
            try {
                if (file.getName().charAt(6) == '1') {
                    Professor prof = jsonReader.readValue(file, Professor.class);
                    CollegeType type = prof.getCollege();
                    College.getInstance(type).addToProfList(prof);
                } else {
                    Student student = jsonReader.readValue(file, Student.class);
                    CollegeType type = student.getCollege();
                    College.getInstance(type).addToStudentList(student);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        writeToLog(null, " USERS READ");
    }

    public static void writeCourse(Course course){
        courseFile = new File(path + "/courses/" + course.getCourseID() + ".json");
        if (!courseFile.exists()){
            try {
                courseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonWriter.writerWithDefaultPrettyPrinter().writeValue(courseFile, course);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readCourses(){
        directory = new File(path + "/courses");
        File[] courses = directory.listFiles();
        if (courses == null || courses.length == 0)
            return;
        for (File file : courses){
            try {
                Course course = jsonReader.readValue(file, Course.class);
                CollegeType type = course.getCollegeLinked();
                College.getInstance(type).addToCourseList(course);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        writeToLog(null, " COURSES READ");
    }

    public static void writeRequest(Request request){
        requestFile = new File(path + "/requests/" + request.getRequestID() + ".json");
        if (!requestFile.exists()){
            try {
                requestFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonWriter.writerWithDefaultPrettyPrinter().writeValue(requestFile, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readRequests(){
        directory = new File(path + "/requests");
        File[] requests = directory.listFiles();
        if (requests == null || requests.length == 0)
            return;
        for (File file : requests){
            try {
                Request request = jsonReader.readValue(file, Request.class);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        writeToLog(null, " REQUESTS READ");
    }

    public static void writeToLog(String userID, String message){
        try{
            String text = "";
            Scanner fileIO = new Scanner(logFile);
            while (fileIO.hasNext()) {
                text += fileIO.nextLine() + "\n";
            }
            fileIO.close();
            text += "TIME: ";
            FileWriter fileWriter = new FileWriter(logFile);
            text += formatter.format(new Date(System.currentTimeMillis()));
            if (userID != null)
                text += ",  USER LOGGED IN: " + userID;
            text += ", " + message + "\n";
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException e) {}

    }

}
