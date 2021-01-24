package server;


import server.comunication.Connection;
import server.comunication.IDMessage;
import server.comunication.Message;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static server.comunication.IDMessage.*;

public class Server extends RunnableThread{

    ArrayList<Player> players;

    public Server() {
        players = connectPlayers();
        //chat
        players.forEach(p -> p.setChatListener(
                Optional.of(m -> players.forEach(p2 -> p2.sendChatMessage(p.getName()+ m.getText())))
        ));

        //game listener
        players.forEach(p -> p.setGameListener(
                Optional.of(m -> System.out.println("Game instruction de " + p.getName()+ " : " + m))
        ));


    }

    private String requestString(){
        System.out.println("Digite un mensaje para enviar o 0 para terminar");
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }

    @Override
    public void execute() {

        String message = requestString();
        if(message.equals("0")) stopThread();

        //send to all
        players.forEach(p-> p.sendMessage(new Message(message, ACCEPTED)));
    }

    @Override
    public synchronized void stopThread() {
        super.stopThread();
    }

    /**
     * <h1>Connects all players</h1>
     * */
    private ArrayList<Player> connectPlayers() {

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
