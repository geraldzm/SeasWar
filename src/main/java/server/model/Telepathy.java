package server.model;

import java.util.Random;

public class Telepathy extends Attack {



    public Telepathy(Champion owner) {
        super(IDATTACK.TELEPATHY, owner, new String[]{"cardumen", "sharkattack", "pulp"});
    }

    @Override
    void firstAttack(Village village, String[] command) {

        int amount = getRandom().nextInt(201)+100;

        applyRandomAttack(amount, 33, village);
    }

    @Override
    void secondAttack(Village village, String[] command) {


        int area = getRandom().nextInt(10)+1; // 1-8
        System.out.println("release the kraken area:" + area);

        //reduce 30%
        AttackBoxListener attacker = (box, coordinate) -> {
          if(box.setPercentage(box.getPercentage()-30)){
              deadChampion(box.getOwner(), getAttacker());
          }
        };

        try {

            Coordinate coordinate = new Coordinate(0,0);
            applyToArea(village, area, coordinate, attacker);

            coordinate.row = 0;
            coordinate.column = 29;
            applyToArea(village, area, coordinate, attacker);

            coordinate.row = 19;
            coordinate.column = 0;
            applyToArea(village, area, coordinate, attacker);

            coordinate.row = 19;
            coordinate.column = 29;
            applyToArea(village, area, coordinate, attacker);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    void thirdAttack(Village village, String[] command) {

        int amount = getRandom().nextInt(30)+20;

        applyRandomAttack(amount, 25, village);
    }
}