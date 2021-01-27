package server;

import lombok.Getter;
import server.comunication.Listener;
import server.comunication.Message;
import server.model.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static server.comunication.IDMessage.*;

public class ServerConnection extends RunnableThread {

    private ServerSocket serverSocket;
    @Getter
    private ArrayList<Player> players;
    private Player admin;
    private int maxPlayers;
    private boolean connected, configurations;

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
        else if (configurations) configs();
    }

    // config champions
    private void configs() {
        System.out.println("entra a configs");

        // champions
        Listener championsListener = m -> {
            System.out.println("Llegan los campeones de: " + players.get(m.getId()).getName() + " Json champion: " + Arrays.toString(m.getTexts()));
        };

        new ActionQueue(new ArrayList<>(players))
                .addAction(new Message(REQUESTCHARACTERS), championsListener, RESPONSE)
                .executeQueue();


   /*     // create villages
        players.forEach(p -> {
            Village village = new Village();
            village.initVillage(p.getChampions()); // create matrix
            p.setVillage(village);
        });

*/

        stopThread();
        //   initGame();
    }

    private void connecting() {
        try {

            if(players.size() == 0) System.out.println("waiting for admin to connect...");
            else System.out.println("waiting connection...\tcurrent number of connections: " + players.size());

            Socket newClient  = serverSocket.accept();
            Player player = new Player(newClient);
            player.setId(players.size());

            ActionQueue quickQueue = new ActionQueue(Collections.singletonList(player));
            quickQueue.addAction(new Message(player.getId(), ID));

            if (players.size() == 0) { // the first client is the admin
                players.add(player);

                // ask the amount of players
                admin = player;

                Listener adminListener = m ->{
                    maxPlayers = m.getNumber();
                    requestName(admin);
                };

                quickQueue.addAction(new Message(ADMIN), adminListener, RESPONSE);

            } else if(players.size() < maxPlayers) {

                players.add(player);

                quickQueue.addAction(new Message(ACCEPTED), message -> requestName(player), DONE);

                if(players.size() == maxPlayers) {
                    connected = true;
                }

            } else {
                // lo rechazamos
                player.sendMessageAndWait(REJECTED);
                player.closeConnection();
                return;
            }

            quickQueue.executeQueue();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al connectar un cliente");
        }
    }

    private void requestName(Player player) {

        Listener nameListener = message -> {
            if(players.stream().map(Player::getName).filter(Objects::nonNull).anyMatch(s -> s.equals(message.getText()))){ // if the name already exists
                player.sendMessageAndWait(WRONGNAME);
            }else {
                player.removeListener();
                player.setName(message.getText());
                if(players.size() == maxPlayers) configurations = true; //activate configurations
            }
        };

        player.setGameListener(Optional.of(nameListener));
        player.setFilter(Optional.of(ms -> ms.getIdMessage() == RESPONSE));

        player.sendMessageAndWait(REQUESTNAME);
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