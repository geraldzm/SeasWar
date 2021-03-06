package server;

import com.google.gson.Gson;
import lombok.Getter;
import server.comunication.Listener;
import server.comunication.Message;
import server.model.Box;
import server.model.Champion;
import server.model.Player;
import server.model.Village;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        // ask for champions
        new ActionQueue(new ArrayList<>(players))
                .addAction(new Message(REQUESTCHARACTERS), m -> players.get(m.getId()).setChampions(m.getTexts()), RESPONSE)
                .executeQueue();

        // create villages and instance champion powers
        players.forEach(p -> {
            Village village = new Village();
            village.initVillage(p.getChampions()); // create matrix
            p.setVillage(village);

            p.getChampions().forEach(Champion::initPowers);
        });


        initGame();
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

        ActionQueue.quickActionQueue(players, new Message(STARTED));

        // send matrix to clients
        {
            Gson g = new Gson();

            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);

                ActionQueue actionQueue = new ActionQueue(Collections.singletonList(player));

                List<List<String>> justNames = player.getVillage().mapMatrixToNames();

                String s = g.toJson(justNames);

                //divide string in two parts
                String firstPart = s.substring(0, s.length() / 2);
                actionQueue.addAction(new Message(firstPart, INITMATRIX1));

                String secondPart = s.substring(s.length() / 2);
                actionQueue.addAction(new Message(secondPart, INITMATRIX2));

                actionQueue.executeQueue();
            }
        }

        stopThread();
    }
}