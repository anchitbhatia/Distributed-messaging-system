package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.Ack;
import messages.ConsumerRecord;
import messages.Follower.FollowerRequest;
import messages.Message;
import messages.Message.MessageDetails;
import messages.Node.NodeDetails;
import messages.Producer.ProducerMessage;
import messages.Producer.ProducerRequest;
import messages.Replicate.ReplicateMessage;
import messages.Synchronization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Leader extends BrokerState{
    private static final Logger LOGGER = LogManager.getLogger(Leader.class);

    public Leader(Broker broker) {
        super(broker);
    }

    @Override
    void startBroker() {
    }

    @Override
    void handleSyncRequest(Connection connection, Synchronization.SyncRequest request) {

    }

    private void replicateNewMessage(Message.NewMessage message, long offset) {
        MessageDetails details = MessageDetails.newBuilder().
                setTopic(message.getTopic()).
                setData(message.getData()).
                setOffset(offset).
                build();
        ReplicateMessage replicateMessage = ReplicateMessage.newBuilder().
                setDetails(details).
                build();
        Any packet = Any.pack(replicateMessage);
        List<Connection> msgConnections = this.broker.getMsgConnections();
        for (Connection msgConnection : msgConnections) {
            try {
                msgConnection.send(packet.toByteArray());
                LOGGER.debug("Replicated to broker " + msgConnection.getNode().getId());
            } catch (ConnectionException e) {
                try {
                    msgConnection.close();
                } catch (IOException ex) {
                    LOGGER.error("Unable to replicate on broker" + msgConnection.getNode().getId());
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void sendAck(Connection connection) throws ConnectionException {
        Ack.AckMessage ackMessage = Ack.AckMessage.newBuilder().setAccept(true).build();
        Any packet = Any.pack(ackMessage);
        connection.send(packet.toByteArray());
    }

    @Override
    void handleProducerRequest(Connection connection, ProducerRequest request) {
        LOGGER.info("Request from producer with topic : " + request.getTopic());
        try {
            sendAck(connection);
            while(!connection.isClosed()) {
                byte[] bytes =  connection.receive();
                if (bytes != null) {
                    try {
                        ProducerMessage producerMsg = Any.parseFrom(bytes).unpack(ProducerMessage.class);
                        Message.NewMessage message = producerMsg.getMessage();
                        long offset = this.broker.addMessage(message);
                        this.replicateNewMessage(message, offset);
                        sendAck(connection);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ConnectionException e) {
            try {
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException {
        NodeDetails follower = request.getNode();
        LOGGER.info("Follow request from " + follower.getId());
        connection.setNodeFields(follower);
        this.broker.addMember(connection.getNode(), connection, Constants.CONN_TYPE_MSG);
        this.broker.addMember(connection.getNode(), Constants.CONN_TYPE_HB);
    }

    @Override
    void handleLeaderDetails(messages.Leader.LeaderDetails leaderDetails) throws IOException {

    }

//    @Override
//    void handleHeartBeat(ClientHandler clientHandler, HeartBeatMessage message) {
//
//    }
}
