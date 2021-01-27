package server.model;

import lombok.Data;
import server.comunication.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

@Data
public class Player extends Connection {

    private int id;
    private String name;

    private ArrayList<Champion> champions;
    private Village village;

    public Player(Socket socket) throws IOException {
        super(socket);
        champions = new ArrayList<>(3);
    }



}
