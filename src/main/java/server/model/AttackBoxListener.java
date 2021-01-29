package server.model;

@FunctionalInterface
public interface AttackBoxListener {
    public void attackBox(Box box, Coordinate coordinate);
}
