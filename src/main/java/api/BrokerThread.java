package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.ProducerRecord;
import messages.Request;
import messages.Subscribe;
import utils.ConnectionException;
import utils.Constants;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * Broker Thread class to handle concurrent requests
 * @author anchitbhatia
 */
public class BrokerThread implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(BrokerThread.class);
    private final Connection connection;
    private String type;

    // Broker thread constructor
    public BrokerThread(Connection connection) throws IOException {
        this.connection = connection;
        this.type = Constants.TYPE_NULL;
    }

    // Method to set type of connection
    private void setType(Any packet) {
        if (packet.is(ProducerRecord.ProducerMessage.class)) {
            this.type = Constants.TYPE_PRODUCER;
        } else if (packet.is(Request.ConsumerRequest.class)) {
            this.type = Constants.TYPE_CONSUMER;
        } else if (packet.is(Subscribe.SubscribeRequest.class)) {
            this.type = Constants.TYPE_SUBSCRIBER;
        } else {
            this.type = Constants.TYPE_NULL;
        }
    }

    // Method to serve request of pull based consumer
    private void serveRequest(Request.ConsumerRequest request) throws IOException {
        String topic = request.getTopic();
        long offset = request.getOffset();
        LOGGER.debug("Consumer requested, topic: " + topic + ", offset: " + offset);
        byte[] data = Database.getRecord(topic, offset);
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
        Database.addSubscriber(topic, connection);
        while (!connection.isClosed()) {
            byte[] msg = connection.pollSendQueue();
            while (msg == null) {
                msg = connection.pollSendQueue();
            }
            try {
                connection.send(msg);
            } catch (ConnectionException e) {
                Database.removeSubscriber(topic, connection);
                connection.close();
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
                    if (type == null) {
                        setType(packet);
                    }
                    LOGGER.debug("Received packet from " + type);
                    switch (this.type) {
                        case Constants.TYPE_PRODUCER -> Database.addQueue(packet.unpack(ProducerRecord.ProducerMessage.class));
                        case Constants.TYPE_CONSUMER -> serveRequest(packet.unpack(Request.ConsumerRequest.class));
                        case Constants.TYPE_SUBSCRIBER -> newSubscriber(packet.unpack(Subscribe.SubscribeRequest.class));
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
