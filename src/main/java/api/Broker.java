package api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Broker {
    private static final Logger LOGGER = LogManager.getLogger(Broker.class);
    private final int port;
    private final ServerSocket socket;
    private boolean isServerRunning;
    private final DatabaseThread databaseThreadObj;
    private final Thread listenerThread;
    private final Thread databaseThread;

    public Broker(int port) throws IOException {
        this.port = port;
        this.socket = new ServerSocket(port);
        this.isServerRunning = false;
        this.listenerThread = new Thread(new ServerListener());
        Database.initializeDatabase();
        this.databaseThreadObj = new DatabaseThread();
        this.databaseThread = new Thread(databaseThreadObj);
    }

    /***
     * Method to start server
     */
    public void startServer(){
        this.isServerRunning = true;
        this.listenerThread.start();
        this.databaseThread.start();
        LOGGER.info("Listening at " + port);
    }

    public void shutdown(){
        this.isServerRunning = false;
        this.databaseThreadObj.shutdown();
        LOGGER.info("Broker shutdown " + port);
    }

    private class ServerListener implements Runnable{
        @Override
        public void run() {
            try {
                while (isServerRunning){
                    Socket clientSocket = socket.accept();
                    Connection connection = new Connection(clientSocket);
                    Thread client = new Thread(new BrokerThread(connection));
                    client.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
