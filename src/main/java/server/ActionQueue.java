package server;

import server.comunication.Connection;
import server.comunication.IDMessage;
import server.comunication.Listener;
import server.comunication.Message;
import server.model.Player;

import java.util.*;
import java.util.function.Predicate;

import static server.comunication.IDMessage.*;

public class ActionQueue {

    private final Queue<Optional<ArrayList<Message>>> queueMessages = new ArrayDeque<>();
    private final Queue<Optional<Predicate<Message>>> filters = new ArrayDeque<>();
    private final Queue<Optional<Listener>> actionsQueue = new ArrayDeque<>();

    private final List<Connection> recipients;
    private final Object lock = new Object();
    private int done;

    private Optional<Listener> action;

    private HashSet<Integer> ready;
    private final Listener listener = m -> {
        action.ifPresent(ls -> ls.action(m));
        synchronized (lock){
            done++;
            if(m.getId() != null) ready.add(m.getId());
            lock.notify();
        }
    };

    public ActionQueue(Player recipient) {
        this(Arrays.asList(recipient));
    }

    public ActionQueue(List<Connection> recipients) {
        this.ready = new HashSet<>();
        this.recipients = recipients;
    }

    public ActionQueue(Hashtable<Integer, Player> recipients) {
        this(new ArrayList<>(recipients.values()));
    }

    public ActionQueue addAction(Message message){
        addAction(message, null, DONE);
        return this;
    }

    public ActionQueue addAction(Message message, Listener action){
        addAction(message, action, RESPONSE);
        return this;
    }

    public ActionQueue addAction(Message message, Listener action, IDMessage filter){
        addAction(new ArrayList<>(Collections.nCopies(recipients.size(), message)), action, filter);
        return this;
    }

    /**
     * @param  messages has to have the same size than recipients
     * */
    public void addAction(ArrayList<Message> messages, Listener action, IDMessage filter) {
        queueMessages.add(Optional.ofNullable(messages));
        actionsQueue.add(Optional.ofNullable(action));
        filters.add(Optional.of(message -> message.getIdMessage() == filter));
    }

    private void execute() {

        while( queueMessages.size() > 0) {

            done = 0;

            // POPS
            action = actionsQueue.poll();
            ArrayList<Message> messages = queueMessages.poll().orElse(null);
            Optional<Predicate<Message>> filter = Optional.of(filters.poll().orElse(message -> true).and(m -> !ready.contains(m.getId())));

            // SEND
            for (int i = 0; i < recipients.size(); i++) {
                Connection connection = recipients.get(i);
                connection.setGameListener(Optional.of(listener));
                connection.setFilter(filter); // set filter before sending the message
                Message message = messages.get(i);
                message.setPlayer((Player) connection);
                connection.sendMessage(message);

             //   LogMessageFactory.sendLogBookMessage(message);// bitacora
            }

            // WAIT
            try {
                synchronized (lock) {
                    while (done < recipients.size())
                        lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // clean
            ready.clear();
            action = Optional.empty();
        }

        recipients.forEach(c -> c.setGameListener(Optional.empty()));
    }

    public void executeQueue() {
        execute();
    }

    public static void quickActionQueue(ArrayList<Player> all, Message message) {
        ActionQueue actionQueueAll = new ActionQueue(new ArrayList<>(all));
        actionQueueAll.addAction(message);
        actionQueueAll.executeQueue();
    }

    public static void quickActionQueue(ArrayList<Player> all, List<Message> messages) {
        ActionQueue actionQueueAll = new ActionQueue(new ArrayList<>(all));
        messages.forEach(actionQueueAll::addAction);
        actionQueueAll.executeQueue();
    }

}