package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.*;
import messages.Follower.FollowerRequest;
import messages.Leader.LeaderDetails;
import messages.Node.NodeDetails;
import messages.Replicate.ReplicateMessage;
import messages.Synchronization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;
import utils.Helper;
import utils.Node;

import java.io.IOException;

public class Follower extends BrokerState{
    private static final Logger LOGGER = LogManager.getLogger(Follower.class);
    private Connection leaderConnection;
    private final Thread clientThread;
    private final Thread syncThread;

    public Follower(Broker broker) {
        super(broker);
        LOGGER.info("Starting follower");
        this.leaderConnection = null;
        this.clientThread = new Thread(new ClientThread(), "client");
        this.syncThread = new Thread(() -> broker.initiateSync(this.leaderConnection, Constants.SYNC_RECEIVE));
    }

    /***
     * Method to start broker
     */
    public void startBroker(){
        LOGGER.info("Starting clientThread");
        try {
            connectLeader();
            broker.addMember(broker.leader, leaderConnection, Constants.CONN_TYPE_MSG);
            broker.addMember(broker.leader, Constants.CONN_TYPE_HB);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.clientThread.start();
        this.syncThread.start();
    }


    /***
     * Method to handle producer request
     * @param connection : connection object
     * @param request : request details
     */
    @Override
    void handleProducerRequest(Connection connection, Producer.ProducerRequest request) {
        Ack.AckMessage message = Ack.AckMessage.newBuilder().setAccept(false).build();
        Any packet = Any.pack(message);
        try {
            connection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            try {
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /***
     *
     * @param connection connection object
     * @param request details of the request
     * @throws IOException if unable to close connection
     */
    @Override
    void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException {
        NodeDetails leaderNode = Helper.getNodeDetailsObj(this.broker.leader);
        LeaderDetails leader = LeaderDetails.newBuilder().setLeader(leaderNode).build();
        Any packet = Any.pack(leader);
        try {
            connection.send(packet.toByteArray());
            LOGGER.info("Sending leader details to " + request.getNode().getId());
        } catch (ConnectionException ignored) {
        } finally {
            this.broker.addMember(connection.getNode(), Constants.CONN_TYPE_HB);
            connection.close();
        }
    }

    /***
     * Method to handle leader details
     * @param leaderDetails : details of the leader received
     * @throws IOException if unable to close connection
     */
    @Override
    void   handleLeaderDetails(LeaderDetails leaderDetails) throws IOException {
        Node newLeader = Helper.getNodeObj(leaderDetails.getLeader());
        LOGGER.info("Received details of leader " + newLeader.getId());
        this.broker.removeMember(newLeader.getId(), Constants.CONN_TYPE_MSG);
        this.broker.setNewLeader(newLeader);
        this.broker.changeState(new Follower(this.broker));
    }

    /***
     * Method to connect to leader
     * @throws IOException if unable to close connection
     */
    private void connectLeader() throws IOException {
        this.leaderConnection = new Connection(this.broker.leader);
        NodeDetails follower = messages.Node.NodeDetails.newBuilder().
                setHostName(this.broker.node.getHostName()).
                setPort(this.broker.node.getPort()).
                setId(this.broker.node.getId()).
                build();
        FollowerRequest request = FollowerRequest.newBuilder().
                setNode(follower).
                build();
        Any packet = Any.pack(request);
        try {
            this.leaderConnection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            this.close();
        }
    }

    /***
     * Method to fetch leader
     * @return ANy packet
     * @throws InvalidProtocolBufferException if unable to parse packet
     */
    private Any fetchLeader() throws InvalidProtocolBufferException {
        if (!this.leaderConnection.isClosed()) {
            byte[] record = this.leaderConnection.receive();
            return Any.parseFrom(record);
        }
        return null;
    }

    /***
     * Method to close connection to leader
     * @throws IOException if unable to close connection
     */
    private void close() throws IOException {
        LOGGER.info("Closing connection to broker with id " + this.broker.leader.getId());
        this.leaderConnection.close();
    }

    private class ClientThread implements Runnable{

        @Override
        public void run() {
            LOGGER.debug("Data thread started");

            while (!leaderConnection.isClosed()) {
                Any record = null;
                try {
                    record = fetchLeader();
                    if (record != null) {
                        if (record.is(ReplicateMessage.class)) {
                            ReplicateMessage packet = record.unpack(ReplicateMessage.class);
                            Message.MessageDetails message = packet.getDetails();
                            broker.addMessage(message, Constants.TYPE_FOLLOWER);
//                            if (message.size() != 0) {
//                                LOGGER.info("Received data: " + data.toStringUtf8());
//                            } else {
//                                Thread.sleep(1000);
//                            }
                        } else if (record.is(LeaderDetails.class)) {
                            close();
                            handleLeaderDetails(record.unpack(LeaderDetails.class));
                        }
                    }
                    else{
                        close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
