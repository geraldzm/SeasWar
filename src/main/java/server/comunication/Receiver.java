package server.comunication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Data;
import server.RunnableThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
* <h3>This class will be use to receive Messages</h3>
* <p>This class will start a thread that will run constantly</p>
* <p>It will start reading as soon as the object is instantiated</p>
*/
@Data
public class Receiver extends RunnableThread {

    public final static int MAX_RECEIVER_BUFFER = 4096;

    private Optional<Listener> listener;
    private final BufferedReader reader;
    private  CharBuffer buffy;
    private Gson gson;

    /**
     * @param socketRef This will be taken as a reference, when the receiver is closed the socket will be as well
     * */
    public Receiver(Socket socketRef) throws IOException {
        this.listener = Optional.empty();
        reader = new BufferedReader(new InputStreamReader(socketRef.getInputStream(), StandardCharsets.UTF_8));
        gson = new Gson();
        buffy = CharBuffer.allocate(MAX_RECEIVER_BUFFER);
    }

    @Override
    public void execute() {
        try {

            int bytes = reader.read(buffy);

            if(bytes != -1) return; // didn't read

            String jsonMessage = new String(buffy.array(), 0, bytes);
            buffy.clear();

            System.out.println("Json received: " + jsonMessage);

            Message message = gson.fromJson(jsonMessage, Message.class);
            listener.ifPresent(l -> l.action(message));

        } catch (IOException e) {
            System.err.println("Reading interrupted");
            super.stopThread();
        } catch (JsonSyntaxException e){
            e.printStackTrace();
            System.err.println("Error de sintaxis Json: ");
        }
    }

    @Override
    public synchronized void stopThread() {
        super.stopThread();
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}