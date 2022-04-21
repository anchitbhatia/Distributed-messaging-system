package api.producer;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.Ack;
import messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;
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

    private Ack.AckMessage receiveAck() throws InvalidProtocolBufferException {
        byte[] bytes =  this.brokerConnection.receive();
        Ack.AckMessage message = null;
        if (bytes != null) {
            message = Any.parseFrom(bytes).unpack(Ack.AckMessage.class);
        }
        return message;
    }

    public boolean sendRequest(String topic) throws ConnectionException, IOException {
        messages.Producer.ProducerRequest request = messages.Producer.ProducerRequest.newBuilder().setTopic(topic).build();
        Any packet = Any.pack(request);
        try {
            this.brokerConnection.send(packet.toByteArray());
            Ack.AckMessage message = this.receiveAck();
            if (message!=null) {
                return message.getAccept();
            }
        } catch (ConnectionException | InvalidProtocolBufferException e) {
                this.close();
                throw e;
        }
        return false;
    }

    /***
     * Method to send data to broker
     * @param topic : topic of the data
     * @param data : data in bytes
     * @throws IOException if unable to close connection
     * @throws ConnectionException if connection is closed
     */
    public void send(String topic, byte[] data) throws ConnectionException, IOException {
        if (!this.brokerConnection.isClosed()) {
            LOGGER.info("Publishing topic: " + topic + ", length: " + data.length);
            Message.NewMessage msg = Message.NewMessage.newBuilder().
                    setTopic(topic).
                    setData(ByteString.copyFrom(data)).
                    build();
            messages.Producer.ProducerMessage message = messages.Producer.ProducerMessage.newBuilder().setMessage(msg).build();
            Any packet = Any.pack(message);
            try {
                this.brokerConnection.send(packet.toByteArray());
                Ack.AckMessage ackMessage = this.receiveAck();
                if (ackMessage==null || !ackMessage.getAccept()) {
                    throw new ConnectionException("Request declined");
                }
                LOGGER.debug("Ack received");
            } catch (ConnectionException | InvalidProtocolBufferException e) {
                this.close();
                throw e;
            }
        }
    }

    /***
     * Method to close connection to broker
     * @throws IOException if unable to close broker connection
     */
    public void close() throws IOException {
        LOGGER.info("Closing connection to broker at " + brokerConnection.getPort());
        brokerConnection.close();
    }
}