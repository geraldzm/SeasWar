package server.model;

import java.util.Random;

public class Kraken extends Attack {

    Random random;

    public Kraken(Champion owner) {
        super(IDATTACK.KRAKEN, owner, new String[]{"tentacle", "breath", "releasekraken"});
        random = new Random();
    }

    // uses the coordinate
    @Override
    void firstAttack(Village village, String[] command) {
        // Tentáculos: permite colocar en el
        // tablero la aparción de 3 tentáculos
        // que destruyen lo que esté en el
        // radio de 1 casilla alrededor.

        AttackBoxListener attacker = (box, coordinate) -> {
            System.out.println("Kraken de"+ getOwner().getName() +" atacando: " + box.getName() + " coordenada "+coordinate.toString() );
            box.setPercentage((byte) 0);
            //falta validar que el mae no tenga un tridente, si lo tiene, entonces el ataque se devuelve
        };

        for (int i = 4; i < 4+3*2; i += 2) {
            System.out.println("Punto numero: " + i);
            Coordinate coordinate = new Coordinate(Integer.parseInt(command[i]), Integer.parseInt(command[i+1]));
            applyToArea(village, 3, coordinate, attacker);
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

        AttackBoxListener attacker = (box, coordinate) -> {
            System.out.println("Kraken de"+ getOwner().getName() +" atacando: " + box.getName() + " coordenada "+ coordinate.toString() );
            box.setPercentage((byte) 0);
            //falta validar que el mae no tenga un tridente, si lo tiene, entonces el ataque se devuelve
        };


        Coordinate coo = getCoordinate();
        assert coo != null: "Coordinate null, first attack, Kraken";

        int range = random.nextInt(8)+1; // 1-8
        //up
        applyToDirection(village, range, coo, attacker, new Coordinate(-1, 0));
        //down
        applyToDirection(village, range, coo, attacker, new Coordinate(1, 0));
        //left
        applyToDirection(village, range, coo, attacker, new Coordinate(0, -1));
        //right
        applyToDirection(village, range, coo, attacker, new Coordinate(0, 1));
    }

    @Override
    void thirdAttack(Village village, String[] command) {
        //      Release the Kraken: el Kraken
        //      aparece en un punto del mapa y
        //      destruye todo en un radio de
        //      1,2,3,4,5,6,7,8,9 casillas.

        Coordinate coo = getCoordinate();
        assert coo != null: "Coordinate null, first attack, Kraken";

        AttackBoxListener attacker = (box, coordinate) -> {
            System.out.println("Kraken de"+ getOwner().getName() +" atacando: " + box.getName() + " coordenada "+coordinate.toString() );
            box.setPercentage((byte) 0);
            //falta validar que el mae no tenga un tridente, si lo tiene, entonces el ataque se devuelve
        };

        int area = random.nextInt(8)+1; // 1-8
        applyToArea(village, area, coo, attacker);
    }
}