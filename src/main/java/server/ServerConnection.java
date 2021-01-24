package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection extends RunnableThread {

    private ServerSocket serverSocket;
    private ArrayList<Player> sockets;


    public ServerConnection() {
        try {
            sockets = new ArrayList<>();
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
            sockets.add(new Player(newClient));
            System.out.println("One client connected!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al connectar un cliente");
        }

        stopThread();
    }

    public ArrayList<Player> getConnections() {
        return sockets;
    }

}