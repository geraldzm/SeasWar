package server.model;

public class Thunders extends Attack {

    public Thunders(Champion owner) {
        super(IDATTACK.THUNDERS, owner, new String[]{"thunderrain", "poseidonthunders", "eelattack"});
    }

    @Override
    void firstAttack(Village village, String[] command) {
        System.out.println("Thunders attack 1 not supported");

    }

    @Override
    void secondAttack(Village village, String[] command) {
        System.out.println("Thunders attack 2 not supported");

    }

    @Override
    void thirdAttack(Village village, String[] command) {
        System.out.println("Thunders attack 3 not supported");

    }
}
