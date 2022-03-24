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

public class Consumer {
    private static final Logger LOGGER = LogManager.getLogger(Consumer.class);
    private final Connection brokerConnection;
    private final BlockingQueue<ByteString> queue;
    private final String topic;

    public Consumer(Node brokerNode, String topic, Long offset) throws ConnectionException {
        try {
            Socket broker = new Socket(brokerNode.getHostName(), brokerNode.getPort());
            this.brokerConnection = new Connection(broker);
            this.topic = topic;
            this.queue = new LinkedBlockingQueue<>();
            Thread fetchingThread = new Thread(new PullBasedThread(this, offset), "Message Fetcher");
            fetchingThread.start();

        } catch (IOException e) {
            throw new ConnectionException("Unable to establish connection to broker " + brokerNode);
        }
    }

    public ByteString poll(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    protected ConsumerRecord.Message fetchBroker(){
        byte[] record = brokerConnection.receive();
        try {
            Any packet = Any.parseFrom(record);
            return packet.unpack(ConsumerRecord.Message.class);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void requestBroker(Long offset) throws IOException {
        LOGGER.info("Requesting topic: " + topic + ", offset: " + offset);
        Request.ConsumerRequest request = Request.ConsumerRequest.newBuilder().
                setTopic(topic).
                setOffset(offset).
                build();
        Any packet = Any.pack(request);
        brokerConnection.send(packet.toByteArray());
    }

    protected void subscribeBroker(){
        LOGGER.info("Subscribing broker, topic: " + topic);
        Subscribe.SubscribeRequest request = Subscribe.SubscribeRequest.newBuilder().setTopic(topic).build();
        Any packet = Any.pack(request);
        brokerConnection.send(packet.toByteArray());
    }

    protected void addMessage(ByteString data){
        this.queue.add(data);
    }

    protected boolean isClosed(){
        return brokerConnection.isClosed();
    }

    public void close() throws IOException {
        brokerConnection.close();
    }

}
