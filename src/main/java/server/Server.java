package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Server {
    private static Server server;
    private CaptchaLoader captchaLoader;
    private DatabaseHandler db;
    private Random random;
    private int port = 9000;
    private ServerSocket serverSocket;
    private List<ClientHandler> handlers;
    private List<Thread> handlerThreads;

    private Server(){
        db = DatabaseHandler.getInstance();
        random = new Random();
        captchaLoader = CaptchaLoader.getInstance();
        handlers = new ArrayList<>();
        handlerThreads = new ArrayList<>();
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
        Thread thread = new Thread(handler);
        handlerThreads.add(thread);
        thread.start();
    }

    public int getRandomCaptcha(int x){
        return captchaLoader.getRandomCaptcha(x);
    }

    public File fetchBufferedCaptcha(int x) {
        return captchaLoader.getCaptcha(x);
    }

    public boolean checkLogIn(String userID, String password){
        String[] S = {"universityID=\"" + userID + "\"", "password=\"" + password + "\""};
        try {
            String tableName = (isStudent(userID) ? "Students" : "Professors");
            ResultSet resultSet = db.getResult(tableName, S, new String[]{"password"});
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isStudent(String userID){
        try {
            ResultSet resultSet = db.getResult("Students", new String[]{"universityID=\"" + userID + "\""}, new String[]{});
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getInfo(String tableName, String[] conditions, String[] columns){
        try{
            ResultSet resultSet = db.getResult(tableName, conditions, columns);
            resultSet.next();
            String answer = "";
            for (String column : columns){
                answer += (answer.equals("") ? "" : "/") + column + ":" + resultSet.getObject(column);
            }
            return answer;
        } catch (SQLException e) {
            return null;
        }
    }

    public String getInfoList(String tableName, String[] conditions, String[] columns, String[] joins, String[] additionalCommands){
        try{
            ResultSet resultSet = db.getResult(tableName, conditions, columns, joins, additionalCommands);
            String answer = "";
            while (resultSet.next()) {
                if (!answer.equals(""))
                    answer += "$";
                for (String column : columns) {
                    String[] splitUp = column.split("[ .]+");
                    String alias = splitUp[splitUp.length - 1];
                    answer += (column.equals(columns[0]) ? "" : "/") + alias + ":" + resultSet.getObject(alias);
                }
            }
            System.err.println(answer);
            return answer;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addCompleteRow(String tableName, String[] values){
        try{
            db.addCompleteRow(tableName, values);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateCompleteRow(String tableName, String[] values, String[] conditions){
        try{
            String[] columnNames = db.getColumnNames(tableName);
            for (int i = 0; i < values.length; i++)
                values[i] = columnNames[i] + " = " + values[i];
            db.updateTable(tableName, values, conditions);
            return true;
        } catch (SQLException e){
            return false;
        }
    }

    public boolean updateUserInfo(String tableName, String[] values, String[] conditions){
        try{
            db.updateTable(tableName, values, conditions);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
