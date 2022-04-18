package api.broker;

import api.Connection;
import messages.Follower.FollowerRequest;
import messages.HeartBeat.HeartBeatMessage;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public abstract class BrokerState {
    private static final Logger LOGGER = LogManager.getLogger(BrokerState.class);
    protected Broker broker;
    protected Node node;
    protected Node leader;
    private boolean isBrokerRunning;
    private final ServerSocket serverSocket;
    private final Thread serverThread;
    protected final Membership membership;
    private final HeartBeatModule heartBeatModule;

    public BrokerState(Broker broker, Node node, Node leader) throws IOException {
        this.broker = broker;
        this.node = node;
        this.leader = leader;
        this.serverSocket = new ServerSocket(node.getPort());
        this.serverThread = new Thread(()->{
            try {
                LOGGER.info("Broker - " + this.node.getId() + " listening at " + node.getPort());
                while (this.isBrokerRunning){
                    Socket clientSocket = this.serverSocket.accept();
                    Connection connection = new Connection(clientSocket);
                    Thread client = new Thread(new ClientHandler(broker, connection));;
                    client.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "server");
        this.isBrokerRunning = false;
//        this.setupDatabase();
        this.membership = new Membership();
        this.heartBeatModule = new HeartBeatModule(broker);
        this.heartBeatModule.startModule();
    }

    public void startBroker(){
        LOGGER.info("Starting broker as " + this.getClass());
        this.isBrokerRunning = true;
        this.serverThread.start();
    }

    protected void newMember(Node node) {
        membership.addMember(node);
    }

    abstract void handleFollowRequest(ClientHandler clientHandler, FollowerRequest request) throws IOException;

    protected void handleHeartBeat(ClientHandler clientHandler, HeartBeatMessage message) {
        clientHandler.heartBeatCount++;
        NodeDetails node = message.getNode();
        clientHandler.connection.setNodeFields(node);
        LOGGER.info("Heartbeat received from " + node.getId());
        LOGGER.info("Members received: " + message.getMembersList().size());
        if (clientHandler.heartBeatCount > 10) {
            this.membership.replaceMembers(message.getMembersList());
            clientHandler.heartBeatCount = 0;
        }
    }


}
