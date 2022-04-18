package api.broker;

import api.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * Broker server class
 * @author anchitbhatia
 */
public class Broker {
    private BrokerState state;
    public Broker(Node node) {
        state = new Leader(node);
    }
    //    private static final Logger LOGGER = LogManager.getLogger(Broker.class);
//    protected final Node node;
//    private final String type;
//    protected final ServerSocket brokerSocket;
//    protected boolean isBrokerRunning;
//    private final Thread serverThread;
//    protected Database database;
//    private DatabaseThread databaseThreadObj;
//    private Thread databaseThread;
//    protected final Membership membership;
//    protected final HeartBeatModule heartBeatModule;
//
//    public Broker(Node node, String type) throws IOException {
//        this.node = node;
//        this.type = type;
//        this.brokerSocket = new ServerSocket(node.getPort());
//        this.serverThread = new Thread((Runnable) this);
//        this.isBrokerRunning = false;
//        this.setupDatabase();
//        this.membership = new Membership();
//        this.heartBeatModule = new HeartBeatModule(this);
//    }
//
//    private void setupDatabase(){
//        this.database = new Database();
//        this.database.initializeDatabase();
//        this.databaseThreadObj = new DatabaseThread(database);
//        this.databaseThread = new Thread(databaseThreadObj);
//    }
//
//    // Method to start server
//    public void startServer(){
//        this.isBrokerRunning = true;
//        this.databaseThread.start();
//        this.heartBeatModule.startModule();
//        this.serverThread.start();
//        LOGGER.info(this.type + "-" + this.node.getId() + " listening at " + node.getPort());
//    }
//
//    protected void newMember(Connection conn){
//        membership.addMember(conn);
//    }
//
//    // Method to shut down server
//    public void shutdown(){
//        this.isBrokerRunning = false;
//        this.databaseThreadObj.shutdown();
//        this.heartBeatModule.stopModule();
//        LOGGER.info("Broker shutdown " + node.getPort());
//    }
}