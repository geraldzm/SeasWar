package server.comunication;

import com.google.gson.Gson;
import lombok.Data;
import server.RunnableThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;

/**
* <h3>This class will be use to receive Messages</h3>
* <p>This class will start a thread that will run constantly</p>
* <p>It will start reading as soon as the object is instantiated</p>
*/
@Data
public class Receiver extends RunnableThread {

    private Optional<Listener> listener;
    private final BufferedReader reader;
    private Predicate<Message> filter;
    private Gson gson;

    /**
     * @param socketRef This will be taken as a reference, when the receiver is closed the socket will be as well
     * */
    public Receiver(Socket socketRef) throws IOException {
        this.listener = Optional.empty();
        filter = message -> false;
        reader = new BufferedReader(new InputStreamReader(socketRef.getInputStream(), StandardCharsets.UTF_8));
        gson = new Gson();
    }

    @Override
    public void execute() {
        try {
            Message message = gson.fromJson(reader.readLine(), Message.class);
            if(listener != null && filter.test(message)) listener.action(message);
        } catch (IOException e) {
            System.err.println("Reading interrupted");
            super.stopThread();
        }
    }

    @Override
    public synchronized void stopThread() {
        super.stopThread();
        setFilter(m -> false);
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <h3>The listener will receive any message read.</h3>
     * */
    public void removeFilter(){
        filter = message -> true; // no filter
    }
}