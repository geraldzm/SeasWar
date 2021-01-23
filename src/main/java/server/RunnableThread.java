package server;

/**
 * <h3>abstraction of all the while(running) classes</h3>
 * <p>This calls is shall be used when a object needs to represent a thread by itself</p>
 * */
public abstract class RunnableThread implements Runnable{

    private Thread thread;
    private boolean running = false;

    @Override
    public final void run() {
        while (running) execute();
    }

    /**
     * <h3>Starts a <b>new</b> thread</h3>
     * */
    public synchronized void startThread(){
        thread = new Thread(this);
        running = true;
        thread.start();
    }

    public synchronized void stopThread(){
        running = false;
    }

    /**
     * <h3>If the thread is running then this method will be call constantly</h3>
     * */
    public abstract void execute();

    public Thread getThread() {
        return thread;
    }
}

