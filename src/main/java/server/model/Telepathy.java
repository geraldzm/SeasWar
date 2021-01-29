package server.model;

public class Telepathy extends Attack {

    public Telepathy(Champion owner) {
        super(IDATTACK.TELEPATHY, owner, new String[]{"cardumen", "pulp", "sharkattack"});
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