package api.broker;

import api.Connection;
import messages.HeartBeat.HeartBeatMessage;
import messages.Follower.FollowerRequest;
import messages.Producer.ProducerMessage;
import messages.Producer.ProducerRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;
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
    protected final Database database;
    protected final Membership membership;
    protected final HeartBeatModule heartBeatModule;
    protected final FailureDetectorModule failureDetectorModule;

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
        this.database = new Database();
        this.membership = new Membership(this);
        this.heartBeatModule = new HeartBeatModule(this);
        this.failureDetectorModule = new FailureDetectorModule(this);
    }

    public void startBroker(){
        LOGGER.info("Starting broker as " + this.state.getClass().getName());
        this.isBrokerRunning = true;
        this.serverThread.start();
        this.state.startBroker();
        this.heartBeatModule.startModule();
        this.failureDetectorModule.startModule();
    }

    public void setNewLeader(Node leader) {
        LOGGER.warn("Setting new leader " + leader.getId());
        this.leader = leader;
    }

    protected void changeState(BrokerState newState) {
        this.state = newState;
        this.state.startBroker();
    }

    protected void addMember(Node node, Connection connection, String connType) {
        this.membership.addMember(node, connection, connType);
    }

    protected void removeMember(int id) {
        this.membership.removeMember(id);
        this.heartBeatModule.removeMember(id);
    }

    protected void addConnection(int id, Connection connection, String type) {

    }

    protected void addMessage(ProducerMessage message) {
        this.database.addMessage(message.getTopic(), message.getData().toByteArray());
    }

    protected void handleProducerRequest(Connection connection, ProducerRequest request) {
        this.state.handleProducerRequest(connection, request);
    }

    protected void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException {
        connection.setNodeFields(request.getNode());
        this.state.handleFollowRequest(connection, request);
    }

    protected void handleHeartBeat(Connection connection, HeartBeatMessage message) {
//        handler.heartBeatCount++;
        messages.Node.NodeDetails node = message.getNode();
        connection.setNodeFields(node);
        this.addMember(connection.getNode(), connection, Constants.CONN_TYPE_HB);
        this.heartBeatModule.parseHeartBeat(message);
//        if (clientHandler.heartBeatCount > 10) {
//            this.broker.membership.replaceMembers(message.getMembersList());
//            clientHandler.heartBeatCount = 0;
//        }
    }
}
