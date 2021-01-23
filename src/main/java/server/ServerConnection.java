package server;

import com.google.gson.Gson;
import server.comunication.Message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnection extends RunnableThread {

    private ServerSocket serverSocket;
    //private Connection admin; //
    //private ArrayList<Player> players;
    //private int maxPlayers;

    public ServerConnection() {
        try {
            this.serverSocket = new ServerSocket(42069);
        } catch (IOException e) {
            System.err.println("Puerto 42069 en uso");
            e.printStackTrace();
        }

    }

    @Override
    public void execute() {
        try {

            System.out.println("waiting connection with client...");
            Socket newClient  = serverSocket.accept();

           if(newClient.isConnected()){
               System.out.println("new client connected!");

               Message message = new Message();
               message.setNumber(83);
               message.setText("Hello world! this is a test");

               Gson gson = new Gson();
               String s = gson.toJson(message);

               System.out.println("Json to be sent: " + s);
               OutputStreamWriter out = new OutputStreamWriter(newClient.getOutputStream(), StandardCharsets.UTF_8);
               out.write(s);

               System.out.println("message sent!");
               out.close();
               newClient.close();
           }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al connectar un cliente");
        }

        stopThread();
    }

    /**
     * <p>Notify all players that every one is really to start</p>
     * */
    private void initGame() {
        stopThread();
    }

}