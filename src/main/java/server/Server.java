package server;


import com.google.gson.Gson;
import server.comunication.IDMessage;
import server.comunication.Listener;
import server.comunication.Message;
import server.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static server.comunication.IDMessage.*;

public class Server extends RunnableThread implements Listener {

    private ArrayList<Player> players;
    private ArrayList<Player> loosers;
    private Hashtable<Integer, Player> playersByID;
    private Hashtable<Integer, Player> playersByTurn;
    private Hashtable<String, Player> playersByName;

    private boolean playing;

    private final Object turnLocker;

    private Player current;
    private Player toAttackPlayer;
    private int turn;

    public Server() {
        players = connectPlayers();
        playersByID = new Hashtable<>();
        playersByName = new Hashtable<>();
        playersByTurn = new Hashtable<>();
        loosers = new ArrayList<>();

        players.forEach(p ->{
            playersByID.put(p.getId(), p);
            System.out.println("agregando : " + p.getName());
            playersByName.put(p.getName(), p);
        });

        // random turn
        ArrayList<Player> shuffled = new ArrayList<>(players);
        Collections.shuffle(shuffled);
        for (int i = 0; i < shuffled.size(); i++) {
            playersByTurn.put(i, shuffled.get(i));
        }

        // chat mensaje
        // to nombre mensaje

        //chat
        System.out.println("Configurando el chat: ");
        players.forEach(p -> p.setChatListener(
                Optional.of(m -> {
                    if(m.getTexts()[0].equals("chat"))
                        players.forEach(pToSend -> pToSend.sendChatMessage(m.getTexts()[1]));
                    else
                        players.stream()
                                .filter(pToSend -> pToSend.getId() == m.getId() || pToSend.getName().equals(m.getTexts()[1])) // filter the sender and the name
                                .forEach(pToSend -> pToSend.sendChatMessage(m.getTexts()[2])); //
                })
        ));

        turnLocker = new Object();
        turn = 0;
        playing = true;
    }

    private void nextTurn(){
        turn = turn+1 >= playersByTurn.size() ? 0: turn+1; // next turn
        if(loosers.contains(turn)){
            System.out.println("nos saltamos el turno " + turn);
            nextTurn();
        }
    }

    @Override
    public void execute() {

        if(!playing) stopThread();

        current = playersByTurn.get(turn);
        assert current != null: "Turn " + turn +" does not match any player";

        System.out.println("Se notifica el inicio de turno a " + current.getName());
        //notify it is your turn
        ActionQueue.quickActionQueue(new ArrayList<>(Collections.singletonList(current)), new Message(TURN));

        System.out.println("accepta el turno a " + current.getName());
        current.sendChatMessage("Es tu turno!");

        current.removeReceiverFilter();
        current.setGameListener(Optional.of(this));


      /*  System.out.println("\n\n\nHasta ahora tengo: ");
        System.out.println("---------Players------------");
        playersByID.forEach(player -> {
            System.out.println("Player: " + player.getName()+ " " + player.getId());

            System.out.println("Campeones {");
            player.getChampions().forEach(champion -> System.out.println("\t"+champion.toString()));
            System.out.println("}");

            System.out.println("\n\nVillage {");
            printMatrix(player.getVillage().getMatrix());
            System.out.println("} ----");
        });*/

        // wait until he changes turn
        synchronized (turnLocker) {
            try {
                turnLocker.wait();

                if(toAttackPlayer != null){
                    setMatrixTo(toAttackPlayer);
                }

                setMatrixTo(current);

                ActionQueue.quickActionQueue(new ArrayList<>(Collections.singletonList(current)),
                        new Message(FINISHTURN));

                System.out.println("Termina la cola de la FINISHTURN");

                toAttackPlayer = null;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("fin del turno a " + current.getName());

        current.removeListener();
        nextTurn();
    }

    private void setMatrixTo(Player toSend) {
        List<List<Byte>> lists = toSend.getVillage().mapMatrixToPercentage();

        String jsonBytes = new Gson().toJson(lists);
        assert jsonBytes != null: "Se genero una matriz de bytes nula";

        ActionQueue.quickActionQueue(new ArrayList<>(Collections.singletonList(toSend)),
                new Message(jsonBytes, MATRIX));
        System.out.println("Termina la cola de la  MATRIX");
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

    @Override
    public void action(Message message) {
        System.out.println("Llega un comando: " + message.toString());

        switch (message.getIdMessage()) {
            case SKIP -> {
                synchronized (turnLocker) {
                    turnLocker.notify();
                }
            }
            case ATTACK -> {

                // attack <championName> <nombre> tentacle <col> <row> <col> <row> <col> <row>
                String[] texts = message.getTexts();
                System.out.println("Se intenta atacar: " + Arrays.toString(texts));

                String toAttackName = texts[2];
                Player playerAttacking = playersByID.get(message.getId());

                toAttackPlayer = playersByName.get(toAttackName);

                if(toAttackPlayer == null) {
                    System.err.println("No existe el jugador : " + toAttackName);
                    playerAttacking.sendChatMessage("No puede atacar a " + toAttackName);
                    break;
                }

                Optional<Champion> champion = current.getChampions().stream().filter(c -> c.getName().equals(texts[1])).findFirst();

                if(champion.isEmpty()) {
                    System.err.println("El jugador : " + playerAttacking + " no tiene le champion: " + texts[1]);
                    playerAttacking.sendChatMessage("No puede atacar a " + toAttackName);
                    break;
                }

                for(Attack attack : champion.get().getAttacks()){
                    if(attack.attackWith(texts[3], texts, toAttackPlayer.getVillage())){
                        //successful attack
                        synchronized (turnLocker) {
                            turnLocker.notify();
                        }
                        return;
                    }
                }

                playerAttacking.sendChatMessage("No se puede atacar con " + texts[3] + " porque no existe ese ataque");
            }
        }
    }

    public Player getByName(String name){
        return playersByName.get(name);
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

}
