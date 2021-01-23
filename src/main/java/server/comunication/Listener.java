package server.comunication;

@FunctionalInterface
public interface Listener {
    public void action(Message message);
}
