package server;

import com.google.gson.Gson;
import server.comunication.Message;

public class Server extends RunnableThread{


    @Override
    public void execute() {
        stopThread();
    }

    public static void main(String[] args) {
        new ServerConnection().startThread();
    }

}
