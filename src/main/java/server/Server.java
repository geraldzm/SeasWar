package server;


import server.comunication.Message;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static server.comunication.IDMessage.*;

public class Server extends RunnableThread{

    ArrayList<Player> playersByID;


    public Server() {
        playersByID = connectPlayers();

        //chat
        playersByID.forEach(p -> p.setChatListener(
                Optional.of(m -> playersByID.forEach(p2 -> p2.sendChatMessage(p.getName()+ m.getText())))
        ));

        //game listener
        playersByID.forEach(p -> p.setGameListener(
                Optional.of(m -> System.out.println("Game instruction de " + p.getName()+ " : " + m))
        ));

    }

    private String requestString(){
        System.out.println("Digite un mensaje para enviar o 0 para terminar");
        Scanner scanner = new Scanner(System.in);
        String ms = scanner.next();
        scanner.close();
        return ms;
    }

    @Override
    public void execute() {

        String message = requestString();
        if(message.equals("0")) stopThread();

        //send to all
        playersByID.forEach(p-> p.sendMessage(new Message(message, ACCEPTED)));
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

        return serverConnections.getPlayers();
    }


    public static void main(String[] args) {
        new Server().startThread();
    }

}
