package server.model;

public class AttackFactory {

    private AttackFactory(){}

    public static Attack getAttack(IDATTACK idattack, Champion owner){
       return switch (idattack){
            case THUNDERS -> new Thunders(owner);
            case TELEPATHY -> new Telepathy(owner);
            case KRAKEN -> new Kraken(owner);
            case WAVES -> new Waves(owner);
            case TRIDENT -> new Trident(owner);
            case VOLCANO -> new Volcano(owner);
            default -> null;
       };
    }
}
