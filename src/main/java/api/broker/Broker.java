package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.Ack.AckMessage;
import messages.Node.NodeDetails;
import messages.HeartBeat.HeartBeatMessage;
import messages.Follower.FollowerRequest;
import messages.Synchronization.SyncRequest;
import messages.Message.MessageRequest;
import messages.Message.NewMessage;
import messages.Message.MessageDetails;
import messages.Election.ElectionInitiate;
import messages.Election.VictoryMessage;
import messages.Producer.ProducerRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;
import utils.Helper;
import utils.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/***
 * Broker class to control broker operations
 * @author anchitbhatia
 */
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
    protected final Election election;
    protected boolean electionInProgress;

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
                    Thread client = new Thread(new ClientHandler(this, connection));
                    client.start();
                }
            } catch (IOException e) {
                LOGGER.error("IO exception occurred while starting server socket");
            }
        }, "server");
        this.database = new Database();
        this.database.initializeDatabase();
        this.membership = new Membership(this);
        this.heartBeatModule = new HeartBeatModule(this);
        this.failureDetectorModule = new FailureDetectorModule(this);
        this.synchronization = new Synchronization(this);
        this.election = new Election(this);
    }

    /***
     * Method to start broker
     */
    public void startBroker(){
        LOGGER.info("Starting broker as " + this.state.getClass().getName());
        this.isBrokerRunning = true;
        this.heartBeatModule.startModule();
        this.failureDetectorModule.startModule();
        this.serverThread.start();
        this.state.startBroker();

    }

    /***
     * Method to set new leader
     * @param leader : new leader
     */
    public void setNewLeader(Node leader) {
        LOGGER.warn("Setting new leader " + leader.getId());
        this.leader = leader;
    }

    /***
     * Method to change state of the broker
     * @param newState : new state of the broker
     */
    protected void changeState(BrokerState newState) {
        this.state = newState;
        this.state.startBroker();
    }

    /***
     * Method to add member to membership table
     * @param node : node object of the member to be added
     * @param connection : connection to be added
     * @param connType : type of the connection to be added
     */
    public void addMember(Node node, Connection connection, String connType) {
        if (node.getId()!=this.node.getId()) {
            this.membership.addMember(node, connection, connType);
        }
    }

    /***
     * Method to add member to membership table
     * @param node node object of the member to be added
     * @param connType connection to be added
     */
    public void addMember(Node node, String connType) {
        if (node.getId()!=this.node.getId()) {
            try {
                this.membership.addMember(node, connType);
            } catch (IOException e) {
                LOGGER.error("Unable to add member");
            }
        }
    }

    /***
     * Method to remove member from membership table
     * @param id : id of the broker to be removed
     */
    public void removeMember(int id) {
        LOGGER.warn("Removing broker " + id);
        this.membership.removeMember(id);
        this.heartBeatModule.removeMember(id);
    }

    /***
     * Method to remove member from membership table
     * @param id : id of the broker to be removed
     * @param type : type of the connection of the member to be removed
     */
    public void removeMember(int id, String type) {
        this.membership.removeConnection(id, type);
    }

    public boolean checkMember(Node node) {
        return this.membership.checkMember(node);
    }

    public void printMembers() {
        this.membership.printMembers();
    }

    /***
     * Method to add message in database
     * @param message : message details
     * @param type : type of the message to be added
     * @return required offset
     */
    public Long addMessage(MessageDetails message, String type) {
        if (!(Objects.equals(type, Constants.TYPE_SYNC)) && this.synchronization.isSyncing()) {
            this.synchronization.bufferMessage(message);
            return null;
        }
        return this.database.addMessage(message.getTopic(), message.getData().toByteArray(), message.getOffset());
    }

    /***
     * Method to add message in database
     * @param message : new message to be added
     * @return required offset
     */
    public Long addMessage(NewMessage message) {
        return this.database.addMessage(message.getTopic(), message.getData().toByteArray());
    }

    /***
     * Method to get msg connections
     * @return list of connections
     */
    public List<Connection> getMsgConnections() {
        return this.membership.getMsgConnections();
    }

    /***
     * Method to get heartbeat connections
     * @return list of connections
     */
    public List<Connection> getHbConnections() {
        return this.membership.getHbConnections();
    }

    /***
     * Method to get members in membership table
     * @return list of nodes
     */
    public List<Node> getMembers() {
        return this.membership.getMembers();
    }

    /***
     * Method to get snapshot of curentOffsetMap
     * @return Map with topic as key and currentoffset required as value
     */
    public Map<String, Long> getCurrentOffsetSnapshot() {
        return this.database.getCurrentOffsetSnapshot();
    }

    /***
     * Method to initiate synchronization
     * @param connection : connection object
     * @param type : type of synchronization required
     */
    public void initiateSync(Connection connection, String type) {
        if (Objects.equals(type, Constants.SYNC_SEND)) {
            this.synchronization.startSender(connection);
        }
        else if (Objects.equals(type, Constants.SYNC_RECEIVE)) {
            this.synchronization.startReceiver(connection.getNode());
        }
    }

    /***
     * Method to initiate election
     */
    public synchronized void initiateElection() {
        if (!this.electionInProgress) {
            this.electionInProgress = true;
            this.removeMember(this.leader.getId());
            this.election.initiateElection();
        }
    }

    /***
     * Method to stop election
     */
    public void stopElection() {
        this.electionInProgress = false;
        this.election.stopElectionTask();
    }

    /***
     * Method to serve message request
     * @param connection : connection object
     * @param request : message request containing request details
     * @throws IOException if unable to close connection
     */
    public void serveMessageRequest(Connection connection, MessageRequest request) throws IOException {
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

    /***
     * Method to handle ElectionInitiate message
     * @param connection : connection object
     * @param message : election initiate message
     */
    public void handleElectionInitiateMessage (Connection connection, ElectionInitiate message) {
        LOGGER.info("Received election initiation message from " + connection.getNode().getId());
        AckMessage ackMessage = AckMessage.newBuilder().setAccept(true).build();
        LOGGER.info("Sending Ack to " + connection.getNode().getId());
        Any packet = Any.pack(ackMessage);
        try {
            connection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            LOGGER.error("Unable to send message");
        }
        this.initiateElection();
    }

    /***
     * Method to handle victory message
     * @param message : victory message
     */
    public void handleVictoryMessage(VictoryMessage message) {
        Node node = Helper.getNodeObj(message.getLeader());
        LOGGER.info("Received victory message from " + node.getId());
        this.removeMember(this.leader.getId());
        this.setNewLeader(node);
        this.stopElection();
        this.changeState(new Follower(this));
    }

    /***
     * Method to handle producer request
     * @param connection : connection object
     * @param request : request details of the producer
     */
    public void handleProducerRequest(Connection connection, ProducerRequest request) {
        this.state.handleProducerRequest(connection, request);
    }

    /***
     * Method to handle follow request of a broker
     * @param connection : connection object
     * @param request : request details of the follower
     * @throws IOException if unable to close connection
     */
    public void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException {
        connection.setNodeFields(request.getNode());
        this.state.handleFollowRequest(connection, request);
    }

    /***
     * Method to handle heart beat received
     * @param connection : connection object
     * @param message : heart beat message received
     */
    public void handleHeartBeat(Connection connection, HeartBeatMessage message) {
        NodeDetails node = message.getNode();
        connection.setNodeFields(node);
        this.heartBeatModule.parseHeartBeat(message);
    }

    /***
     * Method to handle sync request
     * @param connection : connection object
     * @param request : request details for synchronization
     */
    public void handleSyncRequest(Connection connection, SyncRequest request) {
        NodeDetails broker = request.getNode();
        LOGGER.info("Sync request from " + broker.getId());
        connection.setNodeFields(broker);
        this.initiateSync(connection, Constants.SYNC_SEND);
    }
}
