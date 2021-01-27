package server.comunication;

import com.google.gson.Gson;
import lombok.Data;
import server.RunnableThread;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * <h3>This class is use to send Messages</h3>
 * <p>Each Sender object will create a <b>new</b> thread to send a Message</p>
 *
 * @see RunnableThread
 * */
@Data
public class Sender extends RunnableThread {

    private final BufferedWriter writer;
    private final Gson gson;
    private String toSend;

    /**
     * @param socketRef This will be taken as a reference, when the sender is closed the socket will be as well
     * */
    public Sender(Socket socketRef) throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(socketRef.getOutputStream(), StandardCharsets.UTF_8));
        gson = new Gson();
    }

    @Override
    public void execute() {
        try {
            writer.write(toSend);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            toSend = null;
            stopThread();

        }
    }

    public void send(Message message) {
        if(message == null) return;
        toSend = gson.toJson(message);

        if(toSend == null) {
            System.out.println("Se genero un Json nulo con: ");
            System.out.println(message.toString());
        }

        this.startThread();
    }

}