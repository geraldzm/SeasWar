package server.comunication;

import lombok.Data;

@Data
public class Message {

    private String text;
    private String[] texts;

    private int number;
    private int[] numbers;
}
