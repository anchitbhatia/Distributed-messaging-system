package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import messages.Ack;
import messages.Election.ElectionInitiate;
import messages.Election.VictoryMessage;
import messages.Follower.FollowerRequest;
import messages.HeartBeat.HeartBeatMessage;
import messages.Message;
import messages.Producer;
import messages.Subscribe.SubscribeRequest;
import messages.Synchronization.SyncRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import java.io.IOException;

/***
 * Handler class to manage requests coming to broker server socket
 * @author anchitbhatia
 */
public class ClientHandler implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger("Handler");
    private final Broker broker;
    protected final Connection connection;
    private String connectionType;
//    protected int heartBeatCount;

    public ClientHandler(Broker broker, Connection connection) {
        this.broker = broker;
        this.connection = connection;
        this.connectionType = Constants.TYPE_NULL;
    }

    /***
     * Method to set type of connection
     * @param packet : packet received
     */
    private void setConnectionType(Any packet) {
        if (packet.is(Producer.ProducerRequest.class)) {
            this.connectionType = Constants.TYPE_PRODUCER;
        } else if (packet.is(Message.MessageRequest.class)) {
            this.connectionType = Constants.TYPE_CONSUMER;
        } else if (packet.is(SubscribeRequest.class)) {
            this.connectionType = Constants.TYPE_SUBSCRIBER;
        } else if (packet.is(FollowerRequest.class)) {
            this.connectionType = Constants.TYPE_FOLLOWER;
        } else if (packet.is(HeartBeatMessage.class)) {
            this.connectionType = Constants.TYPE_HEARTBEAT;
        } else if (packet.is(SyncRequest.class)) {
            this.connectionType = Constants.TYPE_SYNC;
        } else {
            this.connectionType = Constants.TYPE_NULL;
        }
    }

    @Override
    public void run() {
//        LOGGER.info("Connection established " + connection);
        while (!connection.isClosed()) {
            byte[] message =  connection.receive();
            if (message != null) {
                try {
                    Any packet = Any.parseFrom(message);
                    if (connectionType == null) {
//                        LOGGER.debug("Packet is " + packet);
                        setConnectionType(packet);
                    }
//                    LOGGER.debug("Received packet from " + connectionType);
                    switch (this.connectionType) {
                        case Constants.TYPE_CONSUMER -> this.broker.serveMessageRequest(connection, packet.unpack(Message.MessageRequest.class));
//                        case Constants.TYPE_SUBSCRIBER -> newSubscriber(packet.unpack(Subscribe.SubscribeRequest.class));
                        case Constants.TYPE_PRODUCER -> this.broker.handleProducerRequest(connection, packet.unpack(Producer.ProducerRequest.class));
                        case Constants.TYPE_FOLLOWER -> this.broker.handleFollowRequest(connection, packet.unpack(FollowerRequest.class));
                        case Constants.TYPE_HEARTBEAT -> {
                            if (packet.is(HeartBeatMessage.class)) {
                                this.broker.handleHeartBeat(connection, packet.unpack(HeartBeatMessage.class));
                            } else if (packet.is(ElectionInitiate.class)) {
                                this.broker.handleElectionInitiateMessage(connection, packet.unpack(ElectionInitiate.class));
                            } else if (packet.is(VictoryMessage.class)) {
                                this.broker.handleVictoryMessage(packet.unpack(VictoryMessage.class));
                            } else if (packet.is(Ack.AckMessage.class)) {
                                this.broker.stopElection();
                            } else {
                                LOGGER.error("Invalid packet: " + packet);
                            }
                        }
                        case Constants.TYPE_SYNC -> this.broker.handleSyncRequest(connection, packet.unpack(SyncRequest.class));
                        default -> LOGGER.info("Invalid client");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.info("Connection disconnected " + connection);
                try {
                    connection.close();
//                    if (Objects.equals(this.connectionType, Constants.TYPE_FOLLOWER) || Objects.equals(this.connectionType, Constants.TYPE_HEARTBEAT)) {
//                        this.broker.removeMember(this.connection.getNode().getId());
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
