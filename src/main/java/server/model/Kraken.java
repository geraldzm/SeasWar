package server.model;
import server.comunication.Message;

import java.util.*;

import static server.comunication.IDMessage.ATTACKLOG;

public class Kraken extends Attack {

    private Random random;
    private ArrayList<Coordinate> tridentBoxes;


    public Kraken(Champion owner) {
        super(IDATTACK.KRAKEN, owner, new String[]{"tentacle", "breath", "releasekraken"});
        random = new Random();
        tridentBoxes = new ArrayList<>();
    }

    // uses the coordinate
    @Override
    void firstAttack(Village village, String[] command) {
        // Tentáculos: permite colocar en el
        // tablero la aparción de 3 tentáculos
        // que destruyen lo que esté en el
        // radio de 1 casilla alrededor.

        AttackBoxListener attacker = this::IfTrident;

        for (int i = 4; i < 4+3*2; i += 2) {
            Coordinate coordinate = new Coordinate(Integer.parseInt(command[i]), Integer.parseInt(command[i+1]));
            applyToArea(village, 3, coordinate, attacker);
        }

        killBoxesFromTrident();
    }

    private void IfTrident(Box box, Coordinate coordinate) {

        if(box.getOwner().getAttacks().stream().noneMatch(attack -> attack instanceof Trident)){ // si el enemigo tiene un tridente

            if(box.setPercentage((byte) 0)){ // if owner die
                deadChampion(getAttacker(), box.getOwner());
            }

        }else {
            tridentBoxes.add(coordinate);
        }
    }


    @Override
    void secondAttack(Village village, String[] command) {
        //Kraken Breath: se selecciona una
        //        casilla donde el Kraken lanza su
        //        aliento hacia una dirección: arriba,
        //                abajo, derecha, izquierda. El aliento
        //        destruye entre 1 y 8 casillas en esa
        //        dirección.

        AttackBoxListener attacker = this::IfTrident;

        int range = random.nextInt(8)+1; // 1-8
        Coordinate velocity;

        switch (command[4]){
            case "up" -> velocity = new Coordinate(-1, 0);
            case "down" -> velocity = new Coordinate(1, 0);
            case "left" -> velocity = new Coordinate(0, -1);
            case "right" -> velocity = new Coordinate(0, 1);
           // case "all" -> velocity = new Coordinate(0, 1);
            default -> velocity = new Coordinate(0, 0);
        }

        try{
            applyToDirection(village, range, new Coordinate(Integer.parseInt(command[5]), Integer.parseInt(command[6])), attacker, velocity);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        killBoxesFromTrident();
    }

    @Override
    void thirdAttack(Village village, String[] command) {
        //      Release the Kraken: el Kraken
        //      aparece en un punto del mapa y
        //      destruye todo en un radio de
        //      1,2,3,4,5,6,7,8,9 casillas.

        AttackBoxListener attacker = this::IfTrident;

        int area = random.nextInt(8)+1; // 1-8
        System.out.println("release the kraken area:" + area);

        try {
            Coordinate coordinate = new Coordinate(Integer.parseInt(command[4]), Integer.parseInt(command[5]));
            applyToArea(village, area, coordinate, attacker);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        killBoxesFromTrident();
    }


    private void killBoxesFromTrident() {

        if(tridentBoxes.size() == 0) return;

        Player owner = getOwner().getOwner(); // owner of champion

        // enviamos las casilla que se le retornan
        tridentBoxes.forEach(coordinate -> {
            Box box = owner.getVillage().getMatrix()[coordinate.row][coordinate.column];

            getAttackLogMessages().add(new Message(
                    box.getOwner().getName() + " devuelve el ataque del kraken con el tridente en  (" + coordinate.row+ ", "+ coordinate.column +")" , ATTACKLOG));

            if(box.setPercentage((byte) 0)) {
                deadChampion(box.getOwner(), getAttacker());
            }
        });

        tridentBoxes.clear();
    }
}