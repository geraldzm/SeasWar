package server.comunication;

import lombok.Data;
import lombok.ToString;
import server.Player;

@Data
@ToString
public class Message {
    private IDMessage idMessage;

    private String text;
    private String[] texts;

    private Integer number;
    private Integer[] numbers;

    private Integer id;
    private transient Player player;

    public Message(int number, String string, IDMessage idMessage) {
        this.number = number;
        this.text = string;
        this.idMessage = idMessage;
        this.numbers = null;
    }

    public Message(String string, IDMessage idMessage) {
        this.text = string;
        this.idMessage = idMessage;
        this.numbers = null;
    }

    public Message(int number, IDMessage idMessage) {
        this.number = number;
        this.idMessage = idMessage;
    }

    public Message(Integer[] numbers, IDMessage idMessage) {
        this.idMessage = idMessage;
        this.numbers = numbers;
    }

    public Message(Integer[] numbers, String string, IDMessage idMessage) {
        this.idMessage = idMessage;
        this.text = string;
        this.numbers = numbers;
    }

    public Message(IDMessage idMessage) {
        this.idMessage = idMessage;
        this.text = null;
        this.numbers = null;
    }

}
