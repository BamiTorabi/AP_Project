package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Server {
    private static Server server;
    private final Clock clock;
    private CaptchaLoader captchaLoader;
    private DatabaseHandler db;
    private Random random;
    private int port = 9000;
    private long lastUpdateTime = 0;
    private long threshold = 50;
    private ServerSocket serverSocket;
    private List<ClientHandler> handlers;

    private Server(){
        clock = Clock.systemDefaultZone();
        db = DatabaseHandler.getInstance();
        random = new Random();
        captchaLoader = CaptchaLoader.getInstance();
        handlers = new ArrayList<>();
    }

    public static Server getInstance(){
        if (server == null)
            server = new Server();
        return server;
    }

    public void init(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()){
                System.err.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                addNewClientHandler(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void authenticate(ClientHandler handler){
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        handler.setAuthToken(Arrays.toString(bytes));
    }

    public void addNewClientHandler(Socket socket){
        ClientHandler handler = new ClientHandler(socket);
        authenticate(handler);
        handlers.add(handler);
        handler.run();
    }

    public int getRandomCaptcha(int x){
        return captchaLoader.getRandomCaptcha(x);
    }

    public File fetchBufferedCaptcha(int x) {
        return captchaLoader.getCaptcha(x);
    }

    public boolean checkLogIn(String userID, String password){
        String[] S = {"password=" + password};
        try {
            String tableName = (isStudent(userID) ? "Students" : "Professors");
            ResultSet resultSet = db.getResult(tableName, userID, S, new String[]{"password"});
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isStudent(String userID){
        try {
            ResultSet resultSet = db.getResult("Students", userID, new String[]{}, new String[]{});
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getInfo(String tableName, String userID, String[] conditions, String[] columns){
        try{
            ResultSet resultSet = db.getResult(tableName, userID, conditions, columns);
            resultSet.next();
            String answer = "universityID:" + userID;
            for (String column : columns){
                answer += "/" + column + ":" + resultSet.getObject(column);
            }
            return answer;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean updateInfo(String tableName, String userID, String[] conditions, String fieldName, String newValue){
        try{
            db.updateTable(tableName, userID, conditions, fieldName, newValue);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
