package server.model;

import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <h1>matrix of each client</h1>
 * */
@Data
public class Village {

    private Box [][] matrix;
    private Random random;
    public static final int ROWS = 20, COLUMNS = 30;

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
                matrix[row][column] = new Box((byte) 100, owner.getName(), owner);

            }
    }

    private Champion assignToRandomChampion(Map<Champion, AtomicInteger> championIntegerMap) {

        int i = random.nextInt(championIntegerMap.size());

        Champion owner = (Champion) championIntegerMap.keySet().toArray()[i];

        if(championIntegerMap.get(owner).decrementAndGet() == 0) // count how many boxes a champion can have,
            championIntegerMap.remove(owner);

        return owner;
    }


    public boolean isOutOfMatrix(Coordinate coordinate){
        return coordinate.column < 0 || coordinate.column >= COLUMNS || coordinate.row < 0 || coordinate.row >= ROWS;
    }

    /**
     * Map each box to the name of its owner
     * */
    public List<List<String>> mapMatrixToNames(){
        return Arrays.stream(matrix) // map list of boxes to their names
                .map(boxes -> Arrays.stream(boxes).map(Box::getName) // each box to string
                        .collect(Collectors.toList())
                ).collect(Collectors.toList()); // each list of boxes to list of strings
    }

    /**
     * Map each box to its percentage
     * */
    public List<List<Byte>> mapMatrixToPercentage(){
        return Arrays.stream(matrix) // map list of boxes to their names
                .map(boxes -> Arrays.stream(boxes).map(Box::getPercentage) // each box to byte
                        .collect(Collectors.toList())
                ).collect(Collectors.toList()); // each list of boxes to list of strings
    }
}
