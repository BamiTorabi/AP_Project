package client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

public class Client implements Runnable{

    private final String address = "localhost";
    private final int port = 9000;
    private String authToken;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private Application app;
    private Thread guiThread;

    public Client() {
    }

    public void init() throws IOException {
        this.socket = new Socket(address, port);
        this.input = new Scanner(this.socket.getInputStream());
        this.output = new PrintWriter(this.socket.getOutputStream());
        this.app = new Application(this);
        this.guiThread = new Thread(this.app);
        while (!socket.isClosed()) {
            String message = receive();
            String[] S = message.split("/");
            //if (!S[0].equals("CAPTCHA"))
            //   System.err.println(message);
            switch (S[0]) {
                case "START":
                    this.guiThread.start();
                    break;
                case "AUTH_TOKEN":
                    this.authToken = S[1];
                    break;
                case "LOGIN":
                    if (S[2].equals("true")){
                        app.goodLogIn(S[1]);
                    }
                    else{
                        app.badLogIn();
                    }
                    break;
                case "INFO":
                    System.err.println(message);
                    int pageNumber = Integer.parseInt(S[1]);
                    String info = message.substring(8);
                    app.unpackMessage(pageNumber, info);
                    break;
                case "ERROR":
                    app.raiseError(S[1]);
                    break;
            }
        }
    }

    public void send(String message) throws IOException {
        this.output.println(authToken + "/" + message);
        this.output.flush();
    }

    public String receive() throws IOException {
        return this.input.nextLine();
    }

    public void receiveCaptcha(int num, String fileEncoded){
        byte[] bytes =  Base64.getDecoder().decode(fileEncoded);
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bais);
            File file = new File(String.format("captcha%04d.jpg", num));
            if (file.exists())
                file.delete();
            file.createNewFile();
            ImageIO.write(image, "jpg", file);
        } catch (IOException ignored) {}
    }

    /*public ImageIcon fetchCaptcha(int size) throws IOException {
        byte[] imageAr = new byte[size];
        System.err.println(socket.getInputStream().read(imageAr));
        System.err.println(Arrays.toString(imageAr));
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
        if (image == null){
            //System.err.println("fuck");
            return null;
        }
        return new ImageIcon(image);
    }*/

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
