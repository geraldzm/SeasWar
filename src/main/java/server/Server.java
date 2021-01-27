package server;


import server.comunication.Message;
import server.model.Box;
import server.model.Champion;
import server.model.Player;
import server.model.Village;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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


/*
        Champion c1 = new Champion();
        c1.setPercentage(45);
        c1.setName("c1");
        Champion c2 = new Champion();
        c2.setPercentage(25);
        c2.setName("c2");
        Champion c3 = new Champion();
        c3.setPercentage(30);
        c3.setName("c3");

        Village village = new Village();
        village.initVillage(new ArrayList<Champion>(Arrays.asList(c1, c2, c3)));
        Box[][] matrix = village.getMatrix();

        Hashtable<String, AtomicInteger> count = new Hashtable<>();

        for (int row = 0; row < 20; row++) {
            System.out.println("");
            for (int column = 0; column < 30; column++) {
                String name = matrix[row][column].getOwner().getName();
                System.out.print(String.format("[%s]\t", name));

                count.putIfAbsent(name, new AtomicInteger(0));
                count.get(name).incrementAndGet();
            }
        }

        System.out.println("\nAppearance: ");
        count.forEach((s, atomicInteger) -> System.out.println(s + " " +atomicInteger.get() + " times"));
*/
    }



}
