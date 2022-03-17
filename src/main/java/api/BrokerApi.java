package api;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BrokerApi {
    private final int port;
    private final ServerSocket socket;
    private boolean isServerRunning;
    private final Thread listenerThread;

    public BrokerApi(int port) throws IOException {
        this.port = port;
        this.socket = new ServerSocket(port);
        this.isServerRunning = false;
        this.listenerThread = new Thread(new ServerListener());
        DatabaseApi.initializeDatabase();
    }

    /***
     * Method to start server
     */
    public void startServer(){
        this.isServerRunning = true;
        this.listenerThread.start();
        System.out.println("\nBroker: Listening at " + port);
    }

    private class ServerListener implements Runnable{
        @Override
        public void run() {
            try {
                while (isServerRunning){
                    Socket clientSocket = socket.accept();
                    Thread client = new Thread(new Client(clientSocket));
                    client.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
