package api.consumer;

import api.Connection;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.ConsumerRecord;
import messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;
import utils.ConnectionException;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/***
 * Consumer api class
 * @author anchitbhatia
 */
public class Consumer {
    private static final Logger LOGGER = LogManager.getLogger(Consumer.class);
    protected Connection brokerConnection;
    protected final BlockingQueue<ByteString> queue;
    protected final String topic;
    protected List<Node> allBrokers;
    protected Any requestPacket;

    public Consumer(Connection connection, String topic) throws ConnectionException {
        //            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
        this.brokerConnection = connection;
        this.topic = topic;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void initializeBrokerDetails(List<Node> allBrokers) {
        this.allBrokers = allBrokers;
        this.requestPacket = null;
    }

    /***
     * Method to poll queue to get data received
     * @param timeout : timeout to poll
     * @return data received from broker
     * @throws InterruptedException if interrupted while waiting
     */
    public ByteString poll(long timeout) throws InterruptedException, ConnectionException {
        if (queue.isEmpty() && brokerConnection.isClosed()) {
            throw new ConnectionException("Connection closed!");
        }
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    /***
     * Method to fetch record from broker
     * @return Record fetched
     */
    protected Message.MessageDetails fetchBroker() throws IOException, ConnectionException {
        if (!brokerConnection.isClosed()) {
            byte[] record = brokerConnection.receive();
            try {
                Any packet = Any.parseFrom(record);
                return packet.unpack(Message.MessageDetails.class);
            } catch (NullPointerException e) {
                this.close();
                throw new ConnectionException("Connection closed!");
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /***
     * Method to add message to queue
     * @param data : data to be added
     */
    protected void addMessage(ByteString data){
        this.queue.add(data);
    }

    /***
     * Method to check if connection is closed
     * @return true if connection is closed else false
     */
    protected boolean isClosed(){
        return brokerConnection.isClosed();
    }

    /***
     * Method to close connection
     * @throws IOException if exception occurs while closing
     */
    public void close() throws IOException {
        LOGGER.info("Closing connection to broker at " + brokerConnection.getPort());
        brokerConnection.close();
    }

}
