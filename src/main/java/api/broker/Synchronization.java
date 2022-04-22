package api.broker;

import api.Connection;
import api.broker.Broker;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.Ack;
import messages.Message;
import messages.Message.MessageDetails;
import messages.Synchronization.Snapshot;
import messages.Synchronization.SyncRequest;
import messages.Node.NodeDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Constants;
import utils.Node;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Synchronization {
    private static final Logger LOGGER = LogManager.getLogger("Sync");
    private final Broker broker;
    private final BlockingQueue<MessageDetails> bufferQueue;
    private boolean isSyncing;

    public Synchronization(Broker broker) {
        this.broker = broker;
        this.bufferQueue = new LinkedBlockingDeque<>();
        this.isSyncing = false;
    }


    public boolean isSyncing() {
        return isSyncing;
    }

    public void bufferMessage(MessageDetails message) {
        LOGGER.info("Buffering message | " + message.getTopic() + ", offset: " + message.getOffset());
        this.bufferQueue.add(message);
    }

    private void sendSyncRequest(Connection connection) throws ConnectionException {
        Node thisNode = this.broker.node;
        NodeDetails node = NodeDetails.newBuilder().
                setHostName(thisNode.getHostName()).
                setPort(thisNode.getPort()).
                setId(thisNode.getId()).
                build();
        SyncRequest request = SyncRequest.newBuilder().setNode(node).build();
        Any packet = Any.pack(request);
        connection.send(packet.toByteArray());
    }

    private void sendTopicSnapshot(Connection connection) throws ConnectionException {
        Map<String, Long> currentOffsetMap = this.broker.getCurrentOffsetSnapshot();
//        Snapshot.Builder builder = Snapshot.newBuilder();
        Snapshot snapshot = Snapshot.newBuilder().putAllCurrentOffsets(currentOffsetMap).build();
//        for (Map.Entry<String, Long> item : currentOffsetMap.entrySet()) {
//            Snapshot.TopicSnapshot topicSnapshot = Snapshot.TopicSnapshot.newBuilder().
//                    setTopic(item.getKey()).
//                    setLastOffset(item.getValue()).
//                    build();
//            builder.addTopics(topicSnapshot);
//        }
//        Snapshot snapshot = builder.build();
        Any packet = Any.pack(snapshot);
        connection.send(packet.toByteArray());
    }

    private Map<String, Long> receiveTopicSnapshot(Connection connection) throws InvalidProtocolBufferException {
        byte[] record = connection.receive();
        Any packet = Any.parseFrom(record);
        Snapshot snapshot = packet.unpack(Snapshot.class);
        return snapshot.getCurrentOffsetsMap();
    }

    private MessageDetails receiveMessageDetails(Connection connection) throws InvalidProtocolBufferException {
        byte[] record = connection.receive();
        Any packet = Any.parseFrom(record);
        return packet.unpack(MessageDetails.class);
    }

    public void startSender(Connection connection) {
        try {
            this.isSyncing = true;
            sendTopicSnapshot(connection);
            while (!connection.isClosed()) {
                byte[] record = connection.receive();
                Any packet = Any.parseFrom(record);
                if (packet.is(Ack.AckMessage.class)) {
                    this.isSyncing = false;
                    LOGGER.info("Synchronization completed : broker " + connection.getNode().getId());
                    connection.close();
                    return;
                }
                else {
                    Message.MessageRequest request = packet.unpack(Message.MessageRequest.class);
                    this.broker.serveMessageRequest(connection, request);
                }
            }
        } catch (ConnectionException e) {
            try {
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceiver(Node node) {
        LOGGER.info("Initiating synchronization from " + node.getId());
        Connection connection = null;
        try {
            connection = new Connection(node);
        } catch (IOException e) {
            LOGGER.error("Unable to connect to leader");
            LOGGER.error("Synchronization failed");
            return;
        }
        this.isSyncing = true;
        try {
            sendSyncRequest(connection);
        } catch (ConnectionException e) {
            try {
                connection.close();
                this.isSyncing = false;
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Map<String, Long> receivedSnapshot = null;
        try {
            receivedSnapshot = receiveTopicSnapshot(connection);
            LOGGER.info("Received snapshot: " + receivedSnapshot);
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("Unable to receive topic snapshot");
            this.isSyncing = false;
            return;
        }
        Map<String, Long> actualSnapshot = this.broker.getCurrentOffsetSnapshot();
        for (Map.Entry<String, Long> item : receivedSnapshot.entrySet()) {
            String topic = item.getKey();
            long receivedOffset = item.getValue();
            long offset = actualSnapshot.getOrDefault(item.getKey(), 0L);
            while(!connection.isClosed() && (offset < receivedOffset)) {
                try {

                    Message.MessageRequest request = Message.MessageRequest.newBuilder().
                            setTopic(topic).
                            setOffset(offset).
                            build();
                    LOGGER.info("Requesting message | topic: " + topic + ", offset: " + offset);
                    Any packet = Any.pack(request);
                    connection.send(packet.toByteArray());
                    MessageDetails details = receiveMessageDetails(connection);
                    offset = this.broker.addMessage(details, Constants.TYPE_SYNC);

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    try {
                        connection.close();
                        this.isSyncing = false;
                        return;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        Ack.AckMessage message = Ack.AckMessage.newBuilder().setAccept(true).build();
        Any packet = Any.pack(message);
        try {
            LOGGER.info("Sending Ack");
            connection.send(packet.toByteArray());
            connection.close();
        } catch (ConnectionException | IOException e) {
            try {
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        LOGGER.info("Adding buffered data to database");
        while (!bufferQueue.isEmpty()) {
            MessageDetails details = bufferQueue.poll();
            if (details!=null) {
                this.broker.addMessage(details, Constants.TYPE_SYNC);
            }
        }
        LOGGER.info("Synchronization completed");
        this.isSyncing = false;
    }

}
