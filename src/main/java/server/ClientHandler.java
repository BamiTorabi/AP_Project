package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable{
    private Socket socket;
    private Server server;
    private Scanner input;
    private PrintWriter output;
    private String authToken;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.server = Server.getInstance();
    }

    public void init() throws IOException, NoSuchElementException {
        this.input = new Scanner(this.socket.getInputStream());
        this.output = new PrintWriter(this.socket.getOutputStream());
        send("AUTH_TOKEN/" + authToken);
        send("START");
        while (!socket.isClosed()) {
            String message = receive();
            System.err.println(message);
            String[] S = message.split("/");
            boolean flag;
            switch (S[1]) {
                case "LOGIN":
                    String ID = S[2];
                    String password = S[3];
                    flag = server.checkLogIn(ID, password);
                    send("LOGIN/" + S[2] + "/" + flag);
                    break;
                case "INFO":
                    handleInfoQuery(message);
                    break;
                case "UPDATE":
                    handleUpdateQuery(message);
                    break;
                case "ADD":
                    handleAddQuery(message);
                    break;
            }
        }
    }

    public void handleInfoQuery(String message) throws IOException {
        String S[] = message.split("/");
        int pageNumber = Integer.parseInt(S[2]);
        String info = "";
        String college;
        ArrayList<String> T;
        String userType = "";
        switch (pageNumber) {
            case 0:
                int prev = Integer.parseInt(S[3]);
                int x = server.getRandomCaptcha(prev);
                File file = server.fetchBufferedCaptcha(x);
                String response = String.format("INFO/00/%04d/", x);
                sendCaptcha(response, file);
                return;
            case 1:
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")) {
                    info = server.getInfo("Students", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "userType",
                            "firstName",
                            "lastName",
                            "emailAddress",
                            "college",
                            "counsellor"
                    });
                }
                else if (userType.equals("Professor")) {
                    info = server.getInfo("Professors", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "userType",
                            "firstName",
                            "lastName",
                            "emailAddress",
                            "college",
                            "deputy"
                    });
                }
                else{
                    info = server.getInfo("Specials", new String[]{"userID=\"" + S[3] + "\""}, new String[]{
                            "userType",
                            "userID",
                            "name"
                    });
                }
                break;
            case 2:
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")) {
                    info = server.getInfo("Students", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "userType",
                            "firstName",
                            "lastName",
                            "nationalID",
                            "phoneNumber",
                            "emailAddress",
                            "college",
                            "type",
                            "totalScore",
                            "firstYear",
                            "counsellor",
                            "educationalStatus"
                    });
                }
                else if (userType.equals("Professor")){
                    info = server.getInfo("Professors", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "userType",
                            "firstName",
                            "lastName",
                            "nationalID",
                            "phoneNumber",
                            "emailAddress",
                            "college",
                            "type",
                            "roomNumber"
                    });
                }
                break;
            case 3:
                T = new ArrayList<>();
                for (int i = 3; i < S.length; i++){
                    T.add(S[i]);
                }
                info = server.getInfoList("Courses", T.toArray(new String[0]), new String[]{
                        "Courses.courseID",
                        "courseName",
                        "CONCAT(P.firstName, \" \", P.lastName) AS professorName",
                        "units",
                        "COUNT(DISTINCT S.studentLinked) AS enrolled",
                        "GROUP_CONCAT(DISTINCT CT.day, \" \", CT.startTime, \" \", CT.endTime) AS classTime",
                        "examTime"
                        }, new String[]{
                        "Professors P on P.universityID = Courses.professorID",
                        "Scores S on Courses.courseID = S.courseLinked",
                        "ClassTimes CT on Courses.courseID = CT.courseID"
                        }, new String[]{
                        "GROUP BY Courses.courseID"
                });
                break;
            case 4:
                T = new ArrayList<>();
                for (int i = 3; i < S.length; i++){
                    T.add(S[i]);
                }
                info = server.getInfoList("Professors", T.toArray(new String[0]), new String[]{
                        "userType",
                        "firstName",
                        "lastName",
                        "college",
                        "phoneNumber",
                        "type",
                        "emailAddress",
                        "roomNumber"
                }, null, null);
                break;
            case 5:
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")){
                    info = server.getInfoList("Courses", new String[]{
                            "ST.universityID=" + S[3]
                    }, new String[]{
                            "Courses.courseName",
                            "GROUP_CONCAT(DISTINCT CT.day, \" \", CT.startTime, \" \", CT.endTime) AS classTime"
                    }, new String[]{
                            "Scores S on Courses.courseID = S.courseLinked",
                            "ClassTimes CT on Courses.courseID = CT.courseID",
                            "Students ST on S.studentLinked = ST.universityID"
                    }, new String[]{
                            "GROUP BY Courses.courseID"
                    });
                }
                else if (userType.equals("Professor")) {
                    info = server.getInfoList("Courses", new String[]{
                            "P.universityID=" + S[3]
                    }, new String[]{
                            "Courses.courseName",
                            "GROUP_CONCAT(DISTINCT CT.day, \" \", CT.startTime, \" \", CT.endTime) AS classTime"
                    }, new String[]{
                            "Scores S on Courses.courseID = S.courseLinked",
                            "ClassTimes CT on Courses.courseID = CT.courseID",
                            "Professors P on P.universityID = Courses.professorID"
                    }, new String[]{
                            "GROUP BY Courses.courseID"
                    });
                }
                break;
            case 6:
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")){
                    info = server.getInfoList("Courses", new String[]{
                            "ST.universityID=" + S[3]
                    }, new String[]{
                            "Courses.courseID",
                            "courseName",
                            "CONCAT(P.firstName, \" \", P.lastName) AS professorName",
                            "units",
                            "examTime"
                    }, new String[]{
                            "Scores S on Courses.courseID = S.courseLinked",
                            "Students ST on S.studentLinked = ST.universityID",
                            "Professors P on P.universityID = Courses.professorID"
                    }, new String[]{
                            "ORDER BY examTime ASC"
                    });
                }
                else if (userType.equals("Professor")){
                    info = server.getInfoList("Courses", new String[]{
                            "P.universityID=" + S[3]
                    }, new String[]{
                            "Courses.courseID",
                            "courseName",
                            "CONCAT(P.firstName, \" \", P.lastName) AS professorName",
                            "units",
                            "examTime"
                    }, new String[]{
                            "Professors P on P.universityID = Courses.professorID"
                    }, new String[]{
                            "ORDER BY examTime ASC"
                    });
                }
                break;
            case 8:
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")){
                    info = server.getInfoList("Scores", new String[]{
                            "studentLinked=\"" + S[3] + "\""
                    }, new String[]{
                            "courseLinked",
                            "C.courseName",
                            "CONCAT(P.firstName, \" \", P.lastName) AS professorName",
                            "CONCAT(ST.firstName, \" \", ST.lastName) AS studentName",
                            "ST.universityID",
                            "value",
                            "status",
                            "studentProtest",
                            "professorAnswer"
                    }, new String[]{
                            "Courses C on C.courseID = Scores.courseLinked",
                            "Students ST on Scores.studentLinked = ST.universityID",
                            "Professors P on P.universityID = C.professorID"
                    }, null);
                }
                else if (userType.equals("Professor")){
                    info = server.getInfoList("Scores", new String[]{
                            "C.professorID=\"" + S[3] + "\""
                    }, new String[]{
                            "courseLinked",
                            "C.courseName",
                            "CONCAT(P.firstName, \" \", P.lastName) AS professorName",
                            "CONCAT(ST.firstName, \" \", ST.lastName) AS studentName",
                            "ST.universityID",
                            "value",
                            "status",
                            "studentProtest",
                            "professorAnswer"
                    }, new String[]{
                            "Courses C on C.courseID = Scores.courseLinked",
                            "Students ST on Scores.studentLinked = ST.universityID",
                            "Professors P on P.universityID = C.professorID"
                    }, null);
                }
                break;
            case 9:
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")){
                    info = server.getInfoList("Courses", new String[]{
                            "S.studentLinked=\"" + S[3] + "\""
                    }, new String[]{
                            "S.courseLinked",
                            "courseName",
                            "CONCAT(P.firstName, \" \", P.lastName) AS professorName",
                            "units",
                            "S.value",
                            "S.status"
                    }, new String[]{
                            "Scores S on Courses.courseID = S.courseLinked",
                            "Professors P on P.universityID = Courses.professorID"
                    }, null);
                }
                else if (userType.equals("Professor")){

                }
                break;
            case 10:
                info = server.getInfoList("Notifications", new String[]{
                        "userID=\"" + S[3] + "\""
                }, new String[]{
                        "ID",
                        "sent",
                        "title",
                        "message",
                        "seen"
                }, null, new String[]{
                        "ORDER BY sent DESC"
                });
                break;
            case 11:
                info = server.getInfoList("Chats", new String[]{
                        "sender=\"" + S[3] + "\" OR receiver=\"" + S[3] + "\""
                }, new String[]{
                        "sender",
                        "receiver",
                        "CONCAT(S.name, \",\", R.name) AS names",
                        "timeSent",
                        "message"
                }, new String[]{
                        "Users S on Chats.sender = S.userID",
                        "Users R on Chats.receiver = R.userID"
                }, new String[]{
                        "ORDER BY timeSent DESC"
                });
                break;
            case 12:
                if (S.length == 3){
                    info = "";
                }
                else {
                    info = server.getInfoList("Courses", new String[]{
                            "Courses.courseID=\"" + S[3] + "\""
                    }, new String[]{
                            "Courses.courseID",
                            "courseName",
                            "professorID",
                            "units",
                            "GROUP_CONCAT(DISTINCT S.studentLinked) AS studentList",
                            "GROUP_CONCAT(DISTINCT CT.day, \" \", CT.startTime, \" \", CT.endTime) AS classTime",
                            "examTime",
                            "level"
                    }, new String[]{
                            "Scores S on Courses.courseID = S.courseLinked",
                            "ClassTimes CT on Courses.courseID = CT.courseID"
                    }, new String[]{
                            "GROUP BY Courses.courseID"
                    });
                    if (info == null) {
                        sendError("Course with ID " + S[3] + " doesn't exist.");
                        return;
                    }
                }
                break;
            case 13:
                userType = server.getUserType(S[3]);
                if (userType.equals("Special")){
                    info = server.getInfoList("Users", new String[]{
                            "userID LIKE " + S[4],
                            "userType=\"Student\""
                    }, new String[]{
                            "userType",
                            "userID",
                            "name"
                    }, null, null);
                }
                else{
                    college = server.getCollege(S[3]);
                    info = server.getInfoList("Users", new String[]{
                            "userID LIKE " + S[4],
                            "userType=\"Student\"",
                            "college=\"" + college + "\""
                    }, new String[]{
                            "userType",
                            "userID",
                            "name"
                    }, null, null);
                }
                break;
            case 14:
                userType = server.getUserType(S[3]);
                switch (userType){
                    case "Student":
                        college = server.getCollege(S[3]);
                        String firstYear = S[3].substring(0, 4);
                        String profID = server.getInfo("Students", new String[]{
                                "universityID=\"" + S[3] + "\""
                        }, new String[]{"counsellor"}).split(":")[1];
                        info += server.getInfoList("Professors", new String[]{
                                "universityID=\"" + profID + "\""
                        }, new String[]{
                                "userType",
                                "universityID",
                                "firstName",
                                "lastName"
                        }, null, null);
                        info += (info.equals("") ? "" : "$&$") + server.getInfoList("Students", new String[]{
                                "college=\"" + college + "\"",
                                "firstYear=" + firstYear,
                                "NOT universityID=\"" + S[3] + "\""
                        }, new String[]{
                                "userType",
                                "universityID",
                                "firstName",
                                "lastName"
                        }, null, null);
                        info += (info.equals("") ? "" : "$&$") + server.getInfoList("Specials", null, new String[]{
                                "userType",
                                "userID",
                                "name"
                        }, null, null);
                        break;
                    case "Professor":
                        String deputyOrHead = server.getInfo("Professors", new String[]{
                                "universityID=\"" + S[3] + "\"",
                                "deputy=true OR head=true"
                        }, new String[]{"universityID"});
                        college = server.getCollege(S[3]);
                        if (deputyOrHead == null){
                            info = server.getInfoList("Students", new String[]{
                                    "college=\"" + college + "\"",
                                    "counsellor=\"" + S[3] + "\""
                            }, new String[]{
                                    "userType",
                                    "universityID",
                                    "firstName",
                                    "lastName"
                            }, null, null);
                        }
                        else{
                            info = server.getInfoList("Students", new String[]{
                                    "college=\"" + college + "\""
                            }, new String[]{
                                    "userType",
                                    "universityID",
                                    "firstName",
                                    "lastName"
                            }, null, null);
                        }
                        info += (info.equals("") ? "" : "$&$") + server.getInfoList("Specials", null, new String[]{
                                "userType",
                                "userID",
                                "name"
                        }, null, null);
                        break;
                    case "Special":
                        info = server.getInfoList("Users", new String[]{
                                "NOT userID=\"" + S[3] + "\""
                        }, new String[]{
                                "userType",
                                "userID",
                                "name"
                        }, null, null);
                        break;
                }
                break;
        }
        send("INFO/" + String.format("%02d/", pageNumber) + info);
    }

    public void handleUpdateQuery(String message) throws IOException{
        String S[] = message.split("/");
        boolean flag;
        String query, userType;
        ArrayList<String> values;
        switch (S[2]) {
            case "USER":
                userType = server.getUserType(S[3]);
                if (userType.equals("Student")) {
                    flag = server.updateUserInfo("Students", new String[]{S[4] + " = " + S[5]}, new String[]{"universityID = " + S[3]});
                } else if (userType.equals("Professor")) {
                    flag = server.updateUserInfo("Professors", new String[]{S[4] + " = " + S[5]}, new String[]{"universityID = " + S[3]});
                }
                else flag = false;
                if (!flag) {
                    sendError("Bad information.");
                    return;
                }
                sendError("Profile updated successfully.");
                break;
            case "COURSE":
                values = new ArrayList<>();
                for (int i = 3; i < S.length - 2; i++)
                    values.add(S[i]);
                query = server.getInfoList("Professors", new String[]{"universityID=" + S[9]}, new String[]{"universityID"}, null, null);
                if (query == null || query.equals("")){
                    sendError("Professor with ID " + S[9] + " doesn't exist.");
                    return;
                }
                query = server.getInfoList("Courses", new String[]{"courseID=" + S[6]}, new String[]{"courseID"}, null, null);
                boolean add = false;
                if (query == null || query.equals("")) { // add course
                    flag = server.addCompleteRow("Courses", values.toArray(new String[0]));
                    add = true;
                } else { // edit course
                    flag = server.updateCompleteRow("Courses", values.toArray(new String[0]), new String[]{"courseID=" + S[6]});
                }
                if (!flag) {
                    sendError("Bad information.");
                    return;
                }
                server.deleteCompleteRow("Scores", new String[]{"courseLinked=" + S[6]});
                for (String id : S[11].split(",")){
                    query = server.getInfoList("Students", new String[]{"universityID=" + id}, new String[]{"universityID"}, null, null);
                    if (query == null || query.equals("")){
                        sendError("Student with ID " + id + " doesn't exist.");
                        return;
                    }
                    flag = server.addCompleteRow("Scores", new String[]{
                            S[6],
                            id,
                            "0",
                            "\"PENDING\"",
                            "\"\"",
                            "\"\""
                    });
                    if (!flag) {
                        sendError("Bad information.");
                        return;
                    }
                }
                server.deleteCompleteRow("ClassTimes", new String[]{"courseID=" + S[6]});
                for (String time : S[10].split(",")){
                    String[] parts = time.split(" ");
                    flag = server.addCompleteRow("ClassTimes", new String[]{
                            S[6],
                            parts[0],
                            parts[1],
                            parts[2]
                    });
                    if (!flag) {
                        sendError("Bad information.");
                        return;
                    }
                }
                sendError("Course " + (add ? "added" : "updated") + " successfully.");
                break;
            case "SCORE":
                values = new ArrayList<>();
                for (int i = 3; i < S.length; i++)
                    values.add(S[i]);
                flag = server.updateCompleteRow("Scores", values.toArray(new String[0]), new String[]{
                        "courseLinked = " + S[3],
                        "studentLinked = " + S[4]
                });
                if (!flag) {
                    sendError("Bad information.");
                    return;
                }
                sendError("Score updated successfully.");
                break;
        }
    }

    public void handleAddQuery(String message) throws IOException{
        String[] S = message.split("/");
        String query;
        boolean flag = false;
        ArrayList<String> values;
        switch (S[2]){
            case "STUDENT":
                query = server.getInfoList("Users", new String[]{"userID=\"" + S[8] + "\""}, new String[]{"userID"}, null, null);
                if (query != null && !query.equals("")){
                    sendError("User ID already exists.");
                    return;
                }
                query = server.getInfoList("Professors", new String[]{"universityID=" + S[16]}, new String[]{"universityID"}, null, null);
                if (query == null || query.equals("")){
                    sendError("Professor with ID " + S[16] + " doesn't exist.");
                    return;
                }
                values = new ArrayList<>();
                for (int i = 3; i < S.length; i++)
                    values.add(S[i]);
                flag = server.addCompleteRow("Students", values.toArray(new String[0]));
                if (!flag) {
                    sendError("Bad information.");
                    return;
                }
                flag = server.addCompleteRow("Users", new String[]{S[8], "\"Student\"", "\"" + S[3] + " " + S[4] + "\""});
                if (!flag){
                    sendError("Bad information.");
                    return;
                }
                sendError("User successfully added.");
                break;
            case "PROFESSOR":
                query = server.getInfoList("Users", new String[]{"userID=\"" + S[8] + "\""}, new String[]{"userID"}, null, null);
                if (query != null && !query.equals("")){
                    sendError("User ID already exists.");
                    return;
                }
                values = new ArrayList<>();
                for (int i = 3; i < S.length; i++)
                    values.add(S[i]);
                flag = server.addCompleteRow("Professors", values.toArray(new String[0]));
                if (!flag) {
                    sendError("Bad information.");
                    return;
                }
                flag = server.addCompleteRow("Users", new String[]{S[8], "\"Professor\"", "\"" + S[3] + " " + S[4] + "\""});
                if (!flag){
                    sendError("Bad information.");
                    return;
                }
                sendError("User successfully added.");
                break;
            case "CHAT":
                values = new ArrayList<>();
                for (int i = 3; i < 6; i++)
                    values.add(S[i]);
                values.add(message.split("\\$&\\$")[1]);
                flag = server.addRowWithInfo("Chats", values.toArray(new String[0]), new String[]{
                        "sender",
                        "receiver",
                        "timeSent",
                        "message"
                });
                if (!flag){
                    sendError("User with id " + S[4] + " doesn't exist.");
                    return;
                }
                break;
        }
    }

    public void sendError(String message) throws IOException {
        send("ERROR/" + message);
    }

    public void send(String message) throws IOException {
        this.output.println(message);
        this.output.flush();
    }

    public void sendCaptcha(String message, File file) throws IOException{
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String fileEncoded = Base64.getEncoder().encodeToString(bytes);

            send(message + fileEncoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() throws IOException {
        return this.input.nextLine();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException | NoSuchElementException e) {
            server.close(this);
        }
    }
}
