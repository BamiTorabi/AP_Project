package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{
    private Socket socket;
    private Server server;
    private Scanner input;
    private PrintWriter output;

    public ClientHandler(Socket socket){
        this.socket = socket;
        this.server = Server.getInstance();
    }

    private void send(String message) throws IOException {
        this.output = new PrintWriter(this.socket.getOutputStream());
        this.output.println(message);
        this.output.flush();
    }

    private String receive() throws IOException {
        this.input = new Scanner(this.socket.getInputStream());
        return this.input.nextLine();
    }

    @Override
    public void run() {

    }
}
