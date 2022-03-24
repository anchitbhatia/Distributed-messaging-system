package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;

import java.io.IOException;

/***
 * Push based consumer class to create push based consumers
 * @author anchitbhatia
 */
public class PushBasedConsumer extends Consumer implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(PushBasedConsumer.class);

    public PushBasedConsumer(Node brokerNode, String topic) throws ConnectionException {
        super(brokerNode, topic);
        Thread fetchingThread = new Thread(this, "Push");
        fetchingThread.start();
    }

    protected void subscribeBroker() throws IOException {
        LOGGER.info("Subscribing broker, topic: " + topic);
        Subscribe.SubscribeRequest request = Subscribe.SubscribeRequest.newBuilder().setTopic(topic).build();
        Any packet = Any.pack(request);
        try {
            brokerConnection.send(packet.toByteArray());
        } catch (ConnectionException e) {
            this.close();
        }
    }

    @Override
    public void run() {
        try {
            this.subscribeBroker();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!this.isClosed()){
            ConsumerRecord.Message record = null;
            try {
                record = this.fetchBroker();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ConnectionException e) {
                try {
                    this.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (record!=null){
                ByteString data = record.getData();
                if (data.size() != 0){
                    this.addMessage(data);
                    LOGGER.info("Received from broker, offset: " + record.getOffset() + ", data: " + data);
                }
            }
        }
    }
}
