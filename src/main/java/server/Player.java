package server;

import lombok.Data;
import server.comunication.Connection;

import java.io.IOException;
import java.net.Socket;

@Data
public class Player extends Connection {

    private int id;
    private String name;

    public Player(Socket socket) throws IOException {
        super(socket);
    }

}
