package server.model;

public class Waves extends Attack {

    public Waves(Champion owner) {
        super(IDATTACK.WAVES, owner, new String[]{"swirlraising", "sendhuman", "radioactive"});
    }

    @Override
    void firstAttack(Village village, String[] command) {
        System.out.println("Waves attack 1 not supported");
    }

    @Override
    void secondAttack(Village village, String[] command) {
        System.out.println("Waves attack 2 not supported");
    }

    @Override
    void thirdAttack(Village village, String[] command) {
        System.out.println("Waves attack 3 not supported");

    }
}