package api.broker;

import api.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Broker {
    private static final Logger LOGGER = LogManager.getLogger(Broker.class);
    protected BrokerState state;
    protected Node node;
    protected Node leader;
    private boolean isBrokerRunning;
    private final ServerSocket serverSocket;
    private final Thread serverThread;
    protected final Membership membership;
    private final HeartBeatModule heartBeatModule;

    public Broker(Node node) throws IOException {
        this(node, node);
    }

    public Broker(Node node, Node leader) throws IOException {
        if (Objects.equals(node, leader)) {
            this.state = new Leader(this);
        }
        else {
            this.state = new Follower(this);
        }
        this.node = node;
        this.leader = leader;
        this.isBrokerRunning = false;
        this.serverSocket = new ServerSocket(node.getPort());
        this.serverThread = new Thread(()->{
            try {
                LOGGER.info("Broker - " + this.node.getId() + " listening at " + node.getPort());
                while (this.isBrokerRunning){
                    Socket clientSocket = this.serverSocket.accept();
                    Connection connection = new Connection(clientSocket);
                    Thread client = new Thread(new ClientHandler(this, connection));;
                    client.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "server");
        this.membership = new Membership(this);
        this.heartBeatModule = new HeartBeatModule(this);
    }

    public void startBroker(){
        LOGGER.info("Starting broker as " + this.getClass());
        this.isBrokerRunning = true;
        this.serverThread.start();
        this.state.startBroker();
        this.heartBeatModule.startModule();
    }
}
