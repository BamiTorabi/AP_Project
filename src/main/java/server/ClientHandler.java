package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
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
            this.input = new Scanner(this.socket.getInputStream());
            this.output = new PrintWriter(this.socket.getOutputStream());
            send("AUTH_TOKEN/" + authToken);
            send("START");
            while (!socket.isClosed()) {
                String message = receive();
                System.err.println(message);
                String[] S = message.split("/");
                switch (S[1]) {
                    case "CAPTCHA":
                        int prev = Integer.parseInt(S[2]);
                        int x = server.getRandomCaptcha(prev);
                        File file = server.fetchBufferedCaptcha(x);
                        String response = "CAPTCHA/" + String.format("%04d/", x);
                        sendCaptcha(response, file);
                        break;
                    case "LOGIN":
                        String ID = S[2];
                        String password = S[3];
                        boolean flag = server.checkLogIn(ID, password);
                        send("LOGIN/" + S[2] + "/" + flag);
                        break;
                    case "INFO":
                        int pageNumber = Integer.parseInt(S[3]);
                        String info = "";
                        switch (pageNumber){
                            case 1:
                                if (server.isStudent(S[2])){
                                    info = server.getInfo("Students", S[2], new String[]{}, new String[]{
                                            "student",
                                            "firstName",
                                            "lastName",
                                            "emailAddress",
                                            "counsellor"
                                    });
                                }
                                else{
                                    info = server.getInfo("Professors", S[2], new String[]{}, new String[]{"student",
                                            "firstName",
                                            "lastName",
                                            "emailAddress",
                                            "deputy"
                                    });
                                }
                                break;
                            case 2:
                                if (server.isStudent(S[2])){
                                    info = server.getInfo("Students", S[2], new String[]{}, new String[]{
                                            "student",
                                            "firstName",
                                            "lastName",
                                            "nationalID",
                                            "phoneNumber",
                                            "emailAddress",
                                            "college",
                                            "type",
                                            "totalScore",
                                            "entryYear",
                                            "counsellor",
                                            "educationalStatus"
                                    });
                                }
                                else{
                                    info = server.getInfo("Professors", S[2], new String[]{}, new String[]{
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
                        }
                        send("INFO/" + String.format("%02d/", pageNumber) + info);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
