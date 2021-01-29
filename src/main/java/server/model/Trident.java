package server.model;

import server.ActionQueue;
import server.Server;
import server.comunication.IDMessage;
import server.comunication.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Trident extends Attack {

    private Random random;

    public Trident(Champion owner) {
        super(IDATTACK.TRIDENT, owner, new String[]{"threelines","threenumbers", "KrakenControl"});
        random = new Random();
    }

    @Override
    void firstAttack(Village village, String[] command) {

        AttackBoxListener attacker = (box, coordinate) -> box.setPercentage((byte) 0);

        for (int i = 4; i < 4+3*2; i+= 2) { // three points
            int area = random.nextInt(4); // 0-3
            int range = random.nextInt(4) + 1; // 1-4

            Coordinate velocity;
            switch (area){
                case 0 -> velocity = new Coordinate(-1, 0);
                case 1 -> velocity = new Coordinate(1, 0);
                case 2 -> velocity = new Coordinate(0, -1);
                case 3 -> velocity = new Coordinate(0, 1);
                default -> velocity = new Coordinate(0, 0);
            }

            try{
                applyToDirection(village, range, new Coordinate(Integer.parseInt(command[i]), Integer.parseInt(command[i+1])), attacker, velocity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    void secondAttack(Village village, String[] command) {
        System.out.println("Trident attack 2 not supported, testing");

        Player owner = village.getMatrix()[0][0].getOwner().getOwner();

        System.out.println("Comienza");
        new ActionQueue(new ArrayList<>(Collections.singletonList(owner)))
                .addAction(new Message(IDMessage.NUMBERS), message -> System.out.println("Responde los numeros del tridente con: " + message.toString()), IDMessage.RESPONSE)
                .executeQueue();
        System.out.println("termina la cola interna");

    }

    @Override
    void thirdAttack(Village village, String[] command) {
        System.out.println("This is not an attack");
    }
}