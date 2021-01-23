package server;


import com.google.gson.Gson;
import server.comunication.Connection;
import server.comunication.IDMessage;
import server.comunication.Message;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import static server.comunication.IDMessage.ACCEPTED;
import static server.comunication.IDMessage.ADMIN;

public class Server extends RunnableThread{

    ArrayList<Connection> connections;

    public Server() {
        connections = connectPlayers();
    }

    @Override
    public void execute() {
        Connection connection = connections.get(0);

        connection.setChatListener(Optional.of(m -> System.out.println("Message chat: " + m)));
        connection.setLogbookListener(Optional.of(m -> System.out.println("Message log: " +m)));
        connection.setGameListener(Optional.of(m -> System.out.println("Game instruction: " + m)));

        connection.sendMessage(new Message("Hola mundo!", ACCEPTED));

        stopThread();
    }

    @Override
    public synchronized void stopThread() {
        super.stopThread();

    }

    /**
     * <h1>Connects all players</h1>
     * */
    private ArrayList<Connection> connectPlayers() {

        ServerConnection serverConnections = new ServerConnection();
        serverConnections.startThread();

        try {
            serverConnections.getThread().join(); // wait until they are connected
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return serverConnections.getConnections();
    }


    public static void main(String[] args) {
        new Server().startThread();
    }

}
