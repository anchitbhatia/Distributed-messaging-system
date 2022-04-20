package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.Ack;
import messages.ConsumerRecord;
import messages.Follower.FollowerRequest;
import messages.Message.MessageDetails;
import messages.Node.NodeDetails;
import messages.Producer.ProducerMessage;
import messages.Producer.ProducerRequest;
import messages.Replicate.ReplicateMessage;
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

    private void replicateMessage(MessageDetails message) {
        ReplicateMessage replicateMessage = ReplicateMessage.newBuilder().
                setDetails(message).
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

    private void receiveMessages(Connection connection) {
        while(!connection.isClosed()) {
            byte[] bytes =  connection.receive();
            if (bytes != null) {
                try {
                    ProducerMessage producerMsg = Any.parseFrom(bytes).unpack(ProducerMessage.class);
                    MessageDetails message = producerMsg.getDetails();
                    this.replicateMessage(message);
                    this.broker.addMessage(message);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    void handleProducerRequest(Connection connection, ProducerMessage request) {
        Ack.AckMessage message = Ack.AckMessage.newBuilder().setAccept(true).build();
        Any packet = Any.pack(message);
        receiveMessages(connection);
//        try {
//            connection.send(packet.toByteArray());
//            receiveMessages(connection);
//        } catch (ConnectionException e) {
//            try {
//                connection.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    @Override
    void handleFollowRequest(Connection connection, FollowerRequest request) throws IOException {
        NodeDetails follower = request.getNode();
        LOGGER.info("Follow request from " + follower.getId());
        connection.setNodeFields(follower);
        this.broker.addMember(connection.getNode(), connection, Constants.CONN_TYPE_MSG);
//        int i = 1;
//        while (!connection.isClosed()){
//            String msg = "Message " + i;
//            BrokerMessage record = BrokerMessage.newBuilder().
//                    setTopic("First").
//                    setData(ByteString.copyFrom(msg.getBytes())).
//                    setOffset(i).
//                    build();
//            Any packet = Any.pack(record);
//            LOGGER.debug("Sending to follower " + follower.getId() + " : " + msg);
//            try {
//                connection.send(packet.toByteArray());
//                i++;
//                Thread.sleep(2000);
//            } catch (ConnectionException e) {
//                connection.close();
//                return;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//        }

    }

    @Override
    void handleLeaderDetails(messages.Leader.LeaderDetails leaderDetails) throws IOException {

    }

//    @Override
//    void handleHeartBeat(ClientHandler clientHandler, HeartBeatMessage message) {
//
//    }
}
