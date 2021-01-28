package server;


import com.google.gson.Gson;
import server.comunication.Listener;
import server.comunication.Message;
import server.model.Box;
import server.model.Champion;
import server.model.Player;
import server.model.Village;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static server.comunication.IDMessage.*;

public class Server extends RunnableThread implements Listener {

    ArrayList<Player> playersByID;


    public Server() {
        playersByID = connectPlayers();


        /*
        * problema grave :
        * aveces un usuario me manda un ID el cual no se le asigno, yo filtro las respuestas por ID
        * entonces puede que su DONE lo rechace si ya alguno con ese ID mando DONE
        * */
        //chat
        System.out.println("Configurando el chat: ");
        playersByID.forEach(p -> p.setChatListener(
                Optional.of(m -> {
                    ArrayList<String> names = (ArrayList<String>) Arrays.asList(m.getTexts());
                    System.out.println("names is empty: "+names.isEmpty());
                    playersByID.stream().filter(pl -> names.isEmpty() || names.contains(pl.getName())) // filter if it is private
                            .forEach(p2 -> p2.sendChatMessage(p.getName()+ m.getText()));
                })
        ));

        //game listener
        playersByID.forEach(p -> p.setGameListener(Optional.of(this)));

    }

    @Override
    public void execute() {

        System.out.println("\n\n\nHasta ahora tengo: ");
        System.out.println("---------Players------------");
        playersByID.forEach(player -> {
            System.out.println("Player: " + player.getName()+ " " + player.getId());

            System.out.println("Campeones {");
            player.getChampions().forEach(champion -> System.out.println("\t"+champion.toString()));
            System.out.println("}");

            System.out.println("\n\nVillage {");
            printMatrix(player.getVillage().getMatrix());
            System.out.println("} ----");
        });

        stopThread();
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

        System.out.println("Comienza el server");
        return serverConnections.getPlayers();
    }


    public static void main(String[] args) {
        new Server().startThread();
    }

    public static void printMatrix(Box[][] matrix){

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

    }


    @Override
    public void action(Message message) {

    }
}
