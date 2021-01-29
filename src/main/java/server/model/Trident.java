package server.model;

public class Trident extends Attack {

    public Trident(Champion owner) {
        super(IDATTACK.TRIDENT, owner, new String[]{"threelines","threenumbers", "KrakenControl"});
    }

    @Override
    void firstAttack(Village village, String[] command) {
        System.out.println("Trident attack 1 not supported");

    }

    @Override
    void secondAttack(Village village, String[] command) {
        System.out.println("Trident attack 2 not supported");

    }

    @Override
    void thirdAttack(Village village, String[] command) {
        System.out.println("Trident attack 3 not supported");

    }
}