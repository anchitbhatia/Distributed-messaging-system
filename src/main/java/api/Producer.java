package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;
import messages.ProducerRecord;
import utils.ConnectionException;
import java.io.IOException;
import java.net.Socket;

/***
 * Producer api class
 * @author anchitbhatia
 */
public class Producer {
    private static final Logger LOGGER = LogManager.getLogger(Producer.class);
    private final Connection brokerConnection;

    public Producer(Node brokerNode) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.brokerConnection = new Connection(broker);
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public void send(String topic, byte[] data) throws IOException {
        LOGGER.info("Publishing topic: " + topic + ", length: " + data.length);
        ProducerRecord.ProducerMessage msg = ProducerRecord.ProducerMessage.newBuilder().setTopic(topic).setData(ByteString.copyFrom(data)).build();
        Any packet = Any.pack(msg);
        brokerConnection.send(packet.toByteArray());
    }

    public void close() throws IOException {
        LOGGER.info("Closing connection to broker at " + brokerConnection.getPort());
        brokerConnection.close();
    }
}