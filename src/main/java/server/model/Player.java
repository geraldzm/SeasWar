package server.model;

import com.google.gson.Gson;
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

    public void setChampions(String[] jsonChampions) {
        Gson gson = new Gson();

        for (String jsonChampion : jsonChampions) {
            Champion champion = gson.fromJson(jsonChampion, Champion.class);

            assert champion != null : "null champion generated with : " + jsonChampion;

            champion.setPercentage(champion.getPercentage());
            // --- > flata instaciar el attaque corerspondiente, si tiene waves entonces hacer un new de waves y as[i
            champions.add(champion);
        }

        System.out.println("Se configuran los champios de: " + name + " champiosn: ");
        champions.forEach(c -> System.out.println("\t" + c.toString()));
    }
}
