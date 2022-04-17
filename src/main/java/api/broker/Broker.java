package api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/***
 * Broker server class
 * @author anchitbhatia
 */
public class Broker {
    private static final Logger LOGGER = LogManager.getLogger(Broker.class);
    private final Node node;
    private final String type;
    private final ServerSocket brokerSocket;
    private boolean isBrokerRunning;
    private final DatabaseThread databaseThreadObj;
    private final Thread brokerThread;
    private final Thread databaseThread;

    // Broker server constructor
//    public Broker(int port) throws IOException {
//        this.port = port;
//        this.socket = new ServerSocket(port);
//        this.isServerRunning = false;
//        this.listenerThread = new Thread(new ServerListener());
//        Database.initializeDatabase();
//        this.databaseThreadObj = new DatabaseThread();
//        this.databaseThread = new Thread(databaseThreadObj);
//    }

    public Broker(Node node, String type) throws IOException {
        this.node = node;
        this.type = type;
        this.brokerSocket = new ServerSocket(node.getPort());
        this.brokerThread = new Thread(new BrokerServer());
        this.isBrokerRunning = false;
        Database.initializeDatabase();
        this.databaseThreadObj = new DatabaseThread();
        this.databaseThread = new Thread(databaseThreadObj);
    }

    // Method to start server
    public void startServer(){
        this.isBrokerRunning = true;
        this.brokerThread.start();
        this.databaseThread.start();
        LOGGER.info("Listening at " + node.getPort());
    }

    // Method to shut down server
    public void shutdown(){
        this.isBrokerRunning = false;
        this.databaseThreadObj.shutdown();
        LOGGER.info("Broker shutdown " + node.getPort());
    }

    // Class to listen for incoming connections
    private class BrokerServer implements Runnable{
        @Override
        public void run() {
            try {
                while (isBrokerRunning){
                    if (type==Constants.TYPE_LEADER) {
                        Socket clientSocket = brokerSocket.accept();
                        Connection connection = new Connection(clientSocket);
                        Thread client = new Thread(new BrokerThread(connection));
                        client.start();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
