package server;

import lombok.Getter;
import server.comunication.Listener;
import server.comunication.Message;
import server.model.Player;
import server.model.Village;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static server.comunication.IDMessage.*;

public class ServerConnection extends RunnableThread {

    private ServerSocket serverSocket;
    @Getter
    private ArrayList<Player> players;
    private Player admin;
    private int maxPlayers;
    private boolean connected;

    public ServerConnection() {
        try {
            this.serverSocket = new ServerSocket(42069);
        } catch (IOException e) {
            System.err.println("Puerto 42069 en uso");
            e.printStackTrace();
        }

        this.maxPlayers = -1;
        admin = null;
        this.players = new ArrayList<>();
        connected = false;
    }

    @Override
    public void execute() {
        if(!connected) connecting();
    }

    // config champions
    private void configs() {
        System.out.println("entra a configs");

        // champions
        Listener championsListener = m -> {
           // players.get(m.getId());
            System.out.println("Name: " + players.get(m.getId()) + " Json champion: " + m.getText());
        };

        ActionQueue actionQueue = new ActionQueue(new ArrayList<>(players));
        actionQueue.addAction(new Message(REQUESTCHARACTERS), championsListener, RESPONSE);
        actionQueue.executeQueue();

   /*     // create villages
        players.forEach(p -> {
            Village village = new Village();
            village.initVillage(p.getChampions()); // create matrix
            p.setVillage(village);
        });

*/

        initGame();
    }

    private void connecting() {
        try {

            if(players.size() == 0) System.out.println("waiting for admin to connect...");
            else System.out.println("waiting connection...\tcurrent number of connections: " + players.size());

            Socket newClient  = serverSocket.accept();
            Player player = new Player(newClient);
            player.setId(players.size());

            if (players.size() == 0) { // the first client is the admin
                players.add(player);

                // ask the amount of players
                admin = player;

                Listener adminListener = m ->{
                    System.out.println("Admin responde con: " + m.toString());
                    maxPlayers = m.getNumber();
                    requestName(admin);
                };

                admin.sendMessageAndWait(admin.getId(), ID); // send id

                System.out.println("ID: ");

                admin.setGameListener(Optional.of(adminListener));
                admin.setFilter(Optional.of(m -> m.getIdMessage() == RESPONSE));
                admin.sendMessage(ADMIN);

                System.out.println("ADMIN");

            } else if(players.size() < maxPlayers) {

                player.sendMessageAndWait(admin.getId(), ID); // send id

                players.add(player);
                player.sendMessage(ACCEPTED);
                if(players.size() == maxPlayers){
                    connected = true;
                    System.out.println("Ya se conectaront todos los que tenian queser");
                }
                requestName(player);

            } else {
                // lo rechazamos
                player.sendMessageAndWait(REJECTED);
                player.closeConnection();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al connectar un cliente");
        }
    }

    private void requestName(Player player) {
        System.out.println("Se le pide el nombre al player: " + player.getId());

        Listener nameListener = message -> {
            System.out.println("Me responden el nombre con: " + message.getText());
            if(players.stream().map(Player::getName).filter(Objects::nonNull).anyMatch(s -> s.equals(message.getText()))){ // if the name already exists
                player.sendMessage(WRONGNAME);
            }else {
                player.removeListener();
                player.setName(message.getText());
                if(players.size() == maxPlayers) {
                    configs();
                } // if all players really then start
            }
        };

        player.setGameListener(Optional.of(nameListener));
        player.setFilter(Optional.of(ms -> ms.getIdMessage() == RESPONSE));

        player.sendMessage(REQUESTNAME);
    }


    /**
     * <p>Notify all players that every one is really to start</p>
     * */
    private void initGame() {
        System.out.println("Esperando a que todos esten listos...");

        ActionQueue.quickActionQueue(players, new Message(STARTED));

        System.out.println("Todos listos!");
        stopThread();
    }
}