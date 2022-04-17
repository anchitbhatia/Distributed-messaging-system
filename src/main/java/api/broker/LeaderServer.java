package api.broker;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.*;
import messages.Follower.FollowerRequest;
import utils.ConnectionException;
import utils.Constants;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;

/***
 * Broker Thread class to handle concurrent requests
 * @author anchitbhatia
 */
public class LeaderServer implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(LeaderServer.class);
    private final Broker broker;
    private final Connection connection;
    private String connectionType;

    // Broker thread constructor
    public LeaderServer(Broker broker, Connection connection) throws IOException {
        this.broker = broker;
        this.connection = connection;
        this.connectionType = Constants.TYPE_NULL;
    }

    // Method to set type of connection
    private void setConnectionType(Any packet) {
        if (packet.is(ProducerRecord.ProducerMessage.class)) {
            this.connectionType = Constants.TYPE_MESSAGE;
        } else if (packet.is(Request.ConsumerRequest.class)) {
            this.connectionType = Constants.TYPE_CONSUMER;
        } else if (packet.is(Subscribe.SubscribeRequest.class)) {
            this.connectionType = Constants.TYPE_SUBSCRIBER;
        } else if (packet.is(FollowerRequest.class)) {
            this.connectionType = Constants.TYPE_FOLLOWER;
        } else {
            this.connectionType = Constants.TYPE_NULL;
        }
    }

    // Method to serve request of pull based consumer
    private void serveRequest(Request.ConsumerRequest request) throws IOException {
        String topic = request.getTopic();
        long offset = request.getOffset();
        LOGGER.debug("Consumer requested, topic: " + topic + ", offset: " + offset);
        byte[] data = this.broker.database.getRecord(topic, offset);
        ConsumerRecord.Message record;
        if (data != null) {
            record = ConsumerRecord.Message.newBuilder().
                    setOffset(offset).
                    setData(ByteString.copyFrom(data)).
                    build();
            LOGGER.debug("Responding to consumer, topic: " + topic + ", data: " + ByteString.copyFrom(data));
        } else {
            data = new byte[0];
            record = ConsumerRecord.Message.newBuilder().
                    setOffset(offset).
                    setData(ByteString.copyFrom(data))
                    .build();
            LOGGER.debug("Responding to consumer, topic: " + topic + ", data: null");
        }
        Any packet = Any.pack(record);
        try {
            connection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            connection.close();
            LOGGER.debug("Unable to send to consumer, topic: " + topic + ", offset: " + offset);
            return;
        }
        LOGGER.debug("Sent to consumer, topic: " + topic + ", offset: " + offset);
    }

    // Method to add new subscriber
    private void newSubscriber(Subscribe.SubscribeRequest packet) throws IOException {
        String topic = packet.getTopic();
        this.broker.database.addSubscriber(topic, connection);
        while (!connection.isClosed()) {
            byte[] msg = connection.pollSendQueue();
            while (msg == null) {
                msg = connection.pollSendQueue();
            }
            try {
                connection.send(msg);
            } catch (ConnectionException e) {
                this.broker.database.removeSubscriber(topic, connection);
                connection.close();
            }
        }
    }

    private void newFollower(FollowerRequest request) throws IOException {
        Node follower = new Node(request.getHostName(), request.getPort(), request.getId());
        LOGGER.info("Follow request from " + follower);
        this.broker.newMember(follower);
        connection.setNodeId(request.getId());
        int i = 1;
        while (!connection.isClosed()){
            String msg = "Message " + i;
            BrokerRecord.BrokerMessage record = BrokerRecord.BrokerMessage.newBuilder().
                    setTopic("First").
                    setData(ByteString.copyFrom(msg.getBytes())).
                    setOffset(i).
                    build();
            Any packet = Any.pack(record);
            LOGGER.debug("Sending to follower " + follower.getId() + " : " + msg);
            try {
                connection.send(packet.toByteArray());
                i++;
                Thread.sleep(1000);
            } catch (ConnectionException e) {
                    connection.close();
                    return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void run() {
        LOGGER.info("Connection established " + connection);
        while (!connection.isClosed()) {
            byte[] message =  connection.receive();
            if (message != null) {
                try {
                    Any packet = Any.parseFrom(message);
                    if (connectionType == null) {
                        setConnectionType(packet);
                    }
                    LOGGER.debug("Received packet from " + connectionType);
                    switch (this.connectionType) {
                        case Constants.TYPE_MESSAGE -> this.broker.database.addQueue(packet.unpack(ProducerRecord.ProducerMessage.class));
                        case Constants.TYPE_CONSUMER -> serveRequest(packet.unpack(Request.ConsumerRequest.class));
                        case Constants.TYPE_SUBSCRIBER -> newSubscriber(packet.unpack(Subscribe.SubscribeRequest.class));
                        case Constants.TYPE_FOLLOWER -> newFollower(packet.unpack(FollowerRequest.class));
                        default -> LOGGER.info("Invalid client");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.info("Connection disconnected " + connection);
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
