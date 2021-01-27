package server.model;

import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>matrix of each client</h1>
 * */
@Data
public class Village {

    private Box [][] matrix;
    private Random random;
    private static final int ROWS = 20, COLUMNS = 30;

    public Village() {
        this.matrix = new Box[ROWS][COLUMNS]; // row column
        this.random = new Random();
    }


    public void initVillage(ArrayList<Champion> champions){

        // assign to each box an owner

        Map<Champion, AtomicInteger> championIntegerMap = new HashMap<>();

        for (int i = 0; i < 3; i++)
            championIntegerMap.put(champions.get(i), new AtomicInteger(champions.get(i).getAmountBoxes()));

        for (int row = 0; row < ROWS; row++)
            for (int column = 0; column < COLUMNS; column++) {

                Champion owner = assignToRandomChampion(championIntegerMap);
                matrix[row][column] = new Box((byte) 100, owner);

            }
    }

    private Champion assignToRandomChampion(Map<Champion, AtomicInteger> championIntegerMap) {

        int i = random.nextInt(championIntegerMap.size());

        Champion owner = (Champion) championIntegerMap.keySet().toArray()[i];

        if(championIntegerMap.get(owner).decrementAndGet() == 0) // count how many boxes a champion can have,
            championIntegerMap.remove(owner);

        return owner;
    }


}
