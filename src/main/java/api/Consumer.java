package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import messages.ConsumerRecord;
import messages.Request;
import messages.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Node;
import utils.ConnectionException;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/***
 * Consumer api class
 * @author anchitbhatia
 */
public class Consumer {
    private static final Logger LOGGER = LogManager.getLogger(Consumer.class);
    protected final Connection brokerConnection;
    protected final BlockingQueue<ByteString> queue;
    protected final String topic;

    public Consumer(Node brokerNode, String topic) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.brokerConnection = new Connection(broker);
            this.topic = topic;
            this.queue = new LinkedBlockingQueue<>();
        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public ByteString poll(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    protected ConsumerRecord.Message fetchBroker(){
        if (!brokerConnection.isClosed()) {
            byte[] record = brokerConnection.receive();
            try {
                Any packet = Any.parseFrom(record);
                return packet.unpack(ConsumerRecord.Message.class);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected void addMessage(ByteString data){
        this.queue.add(data);
    }

    protected boolean isClosed(){
        return brokerConnection.isClosed();
    }

    public void close() throws IOException {
        LOGGER.info("Closing connection to broker at " + brokerConnection.getPort());
        brokerConnection.close();
    }

}
