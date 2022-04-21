package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.HeartBeat.HeartBeatMessage;
import messages.Follower.FollowerRequest;
import messages.Message;
import messages.Message.NewMessage;
import messages.Message.MessageDetails;
import messages.Producer.ProducerRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
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
    protected final Synchronization synchronization;

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
        this.database.initializeDatabase();
        this.membership = new Membership(this);
        this.heartBeatModule = new HeartBeatModule(this);
        this.failureDetectorModule = new FailureDetectorModule(this);
        this.synchronization = new Synchronization(this);
    }

    public void startBroker(){
        LOGGER.info("Starting broker as " + this.state.getClass().getName());
        this.isBrokerRunning = true;
        this.heartBeatModule.startModule();
        this.failureDetectorModule.startModule();
        this.serverThread.start();
        this.state.startBroker();

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

    protected Long addMessage(MessageDetails message, String type) {

        if (!(Objects.equals(type, Constants.TYPE_SYNC)) && this.synchronization.isSyncing()) {
            this.synchronization.bufferMessage(message);
            return null;
        }
        return this.database.addMessage(message.getTopic(), message.getData().toByteArray(), message.getOffset());
    }

    protected Long addMessage(NewMessage message) {
        return this.database.addMessage(message.getTopic(), message.getData().toByteArray());
    }

    protected List<Connection> getMsgConnections() {
        return this.membership.getMsgConnections();
    }

    protected List<Connection> getHbConnections() {
        return this.membership.getHbConnections();
    }

    protected List<Node> getMembers() {
        return this.membership.getMembers();
    }

    protected Map<String, Long> getCurrentOffsetSnapshot() {
        return this.database.getCurrentOffsetSnapshot();
    }

    protected void initiateSync(Connection connection, String type) {
        if (Objects.equals(type, Constants.SYNC_SEND)) {
            this.synchronization.startSender(connection);
        }
        else if (Objects.equals(type, Constants.SYNC_RECEIVE)) {
            this.synchronization.startReceiver(this.leader);
        }
    }

    protected void serveMessageRequest(Connection connection, Message.MessageRequest request) throws IOException {
        String topic = request.getTopic();
        long offset = request.getOffset();
        LOGGER.info("Message request | topic: " + topic + ", offset: " + offset);
        byte[] data = this.database.getRecord(topic, offset);
        MessageDetails details;
        if (data != null) {
            details = MessageDetails.newBuilder().
                    setTopic(topic).
                    setData(ByteString.copyFrom(data)).
                    setOffset(offset).
                    build();
        } else {
            data = new byte[0];
            details = MessageDetails.newBuilder().
                    setTopic(topic).
                    setData(ByteString.copyFrom(data)).
                    setOffset(offset).
                    build();
        }
        Any packet = Any.pack(details);
        try {
            connection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            connection.close();
            LOGGER.error("Unable to send | topic: " + topic + ", offset: " + offset);
            return;
        }
        LOGGER.info("Sent | topic: " + topic + ", offset: " + offset);
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

    protected void handleSyncRequest(Connection connection, messages.Synchronization.SyncRequest request) {
        this.state.handleSyncRequest(connection, request);
    }
}
