package server.model;

public class Volcano extends Attack {

    public Volcano(Champion owner) {
        super(IDATTACK.VOLCANO, owner, new String[]{"volcanoraising", "volcanoexplosion", "termalrush"});
    }

    @Override
    void firstAttack(Village village, String[] command) {

    }

    @Override
    void secondAttack(Village village, String[] command) {

    }

    @Override
    void thirdAttack(Village village, String[] command) {

    }
}