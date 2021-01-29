package server.model;

import lombok.Data;
import server.ActionQueue;
import server.comunication.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static server.comunication.IDMessage.ATTACKLOG;

@Data
public abstract class Attack {

    private IDATTACK idattack;
    private Champion owner;
    private Coordinate coordinate;
    private String[] attacksNames;
    private Random random;
    private Champion attacker;

    private ArrayList<Message> attackLogMessages;

    public Attack(IDATTACK idattack, Champion owner, String[] attacksNames) {
        this.idattack = idattack;
        this.owner = owner;
        this.attacksNames = attacksNames;
        random = new Random();
        attackLogMessages = new ArrayList<>();
    }

    abstract void firstAttack(Village village, String[] command);

    abstract void secondAttack(Village village, String[] command);

    abstract void thirdAttack(Village village, String[] command);


    public final boolean attackWith(String attackName, String[] command, Village village, Champion attacker) {

        this.attacker = attacker;
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

        attackLogMessages.add(new Message(
                attacker.getName() + " esta atacando en una punto  (" + coordinate.row+ ", "+ coordinate.column +") en una area de " + area, ATTACKLOG));

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
        attackLogMessages.add(new Message(
                attacker.getName() + " esta atacando en una direccion con velocidad (" + velocity.row+ ", "+ velocity.column +") y un rango de " + range, ATTACKLOG));

        for (int i = 0; i < range; i++) {
            Coordinate next = new Coordinate(coordinate.row + velocity.row * i, coordinate.column + velocity.column * i);
            if (village.isOutOfMatrix(next)) return;
            attackBoxListener.attackBox(village.getMatrix()[next.row][next.column], next);
        }
    }

    public void applyRandomAttack(int amount, int damage, Village village){

        attackLogMessages.add(new Message(
                attacker.getName() + " esta haciendo" + amount +" ataques random con un daño de " + damage, ATTACKLOG));

        for (int i = 0; i < amount; i++) {
            int row = random.nextInt(Village.ROWS);
            int column =  random.nextInt(Village.COLUMNS);
            if(village.getMatrix()[row][column].setPercentage(village.getMatrix()[row][column].getPercentage() - damage)) {
                deadChampion(village.getMatrix()[row][column].getOwner(), getAttacker());
            }
        }
    }


    public void deadChampion(Champion dead, Champion killer) {
       /* attacker.addIdAttacks(box.getOwner().getIdAttacks());
        attacker.addIdAttacks(box.getOwner().getAttacks());

        box.getOwner().setIdAttacks(new IDATTACK[]{});
        box.getOwner().setAttacks(new ArrayList<>());*/
        killer.addIdAttacks(dead.getIdAttacks());
        killer.addIdAttacks(dead.getAttacks());

        attackLogMessages.add(new Message("El campeon " + killer.getName() + " mata a " + dead.getName() + " y obtiene sus poderes", ATTACKLOG));
        attackLogMessages.add(new Message("El campeon " + killer.getName() + " obtiene " + Arrays.toString(dead.getIdAttacks()), ATTACKLOG));

        dead.setIdAttacks(new IDATTACK[]{});
        dead.setAttacks(new ArrayList<>());
    }

}