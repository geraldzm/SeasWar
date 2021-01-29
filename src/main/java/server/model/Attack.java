package server.model;

import lombok.Data;

@Data
public abstract class Attack {

    private IDATTACK idattack;
    private Champion owner;
    private Coordinate coordinate;
    private String[] attacksNames;

    public Attack(IDATTACK idattack, Champion owner, String[] attacksNames) {
        this.idattack = idattack;
        this.owner = owner;
        this.attacksNames = attacksNames;
    }

    abstract void firstAttack(Village village, String[] command);

    abstract void secondAttack(Village village, String[] command);

    abstract void thirdAttack(Village village, String[] command);


    public boolean attackWith(String attackName, String[] command, Village village) {

        for (int i = 0; i < attacksNames.length; i++) {
            if (attackName.equals(attacksNames[i])) {
                switch (i) {
                    case 0 -> firstAttack(village, command);
                    case 1 -> secondAttack(village, command);
                    case 2 -> thirdAttack(village, command);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Goes over all the area given from the point given. And calls the listener with each box
     */
    public void applyToArea(Village village, int area, Coordinate coordinate, AttackBoxListener attackBoxListener) {

        for (int row = -area; row <= area; row++) {
            for (int column = -area; column <= area; column++) {
                Coordinate coordinateToAttack = new Coordinate(coordinate.row + row, coordinate.column + column);
                if (village.isOutOfMatrix(coordinateToAttack)) continue;
                attackBoxListener.attackBox(village.getMatrix()[coordinateToAttack.row][coordinateToAttack.column], coordinateToAttack);
            }
        }

    }

    /**
     * Goes over that direction from the point given. And calls the listener with each box
     */
    public void applyToDirection(Village village, int range, Coordinate coordinate, AttackBoxListener attackBoxListener, Coordinate velocity) {
        System.out.println("---velocidades  " + velocity.row + " " + velocity.column);
        for (int i = 0; i < range; i++) {
            System.out.println("---" + i);
            Coordinate next = new Coordinate(coordinate.row + velocity.row * i, coordinate.column + velocity.column * i);
            if (village.isOutOfMatrix(next)) return;
            System.out.println("---Se pasa el if " + next.row + " " + next.column);
            attackBoxListener.attackBox(village.getMatrix()[next.row][next.column], next);
        }
    }

}