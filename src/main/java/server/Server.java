package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static Server server;
    private DatabaseHandler db;
    private int port = 9000;
    private ServerSocket serverSocket;
    private List<ClientHandler> handlers;

    private Server(){
        db = DatabaseHandler.getInstance();
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

    public void addNewClientHandler(Socket socket){
        ClientHandler handler = new ClientHandler(socket);
        handlers.add(handler);

    }
}
