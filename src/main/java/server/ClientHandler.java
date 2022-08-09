package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
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

    public void init() throws IOException {

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
                    switch (S[2]) {
                        case "USER":
                            if (server.isStudent(S[3])) {
                                flag = server.updateInfo("Students", S[3], new String[]{}, S[4], S[5]);
                            } else {
                                flag = server.updateInfo("Professors", S[3], new String[]{}, S[4], S[5]);
                            }
                            if (!flag) {
                                System.err.println("fuck");
                            }
                            break;
                        case "COURSE":
                            break;
                    }
            }
        }
    }

    public void handleInfoQuery(String message) throws IOException {
        String S[] = message.split("/");
        int pageNumber = Integer.parseInt(S[2]);
        String info = "";
        ArrayList<String> T;
        switch (pageNumber) {
            case 0:
                int prev = Integer.parseInt(S[3]);
                int x = server.getRandomCaptcha(prev);
                File file = server.fetchBufferedCaptcha(x);
                String response = String.format("INFO/00/%04d/", x);
                sendCaptcha(response, file);
                return;
            case 1:
                if (server.isStudent(S[3])) {
                    info = server.getInfo("Students", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "student",
                            "firstName",
                            "lastName",
                            "emailAddress",
                            "college",
                            "counsellor"
                    });
                } else {
                    info = server.getInfo("Professors", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "student",
                            "firstName",
                            "lastName",
                            "emailAddress",
                            "college",
                            "deputy"
                    });
                }
                break;
            case 2:
                if (server.isStudent(S[3])) {
                    info = server.getInfo("Students", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "student",
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
                } else {
                    info = server.getInfo("Professors", new String[]{"universityID=" + S[3]}, new String[]{
                            "universityID",
                            "student",
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
                        "student",
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
                if (server.isStudent(S[3])){
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
                else {
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
                if (server.isStudent(S[3])){
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
                else{
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
                if (server.isStudent(S[3])){
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
                else{
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
                if (server.isStudent(S[3])){
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
                else{

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
        }
        send("INFO/" + String.format("%02d/", pageNumber) + info);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
