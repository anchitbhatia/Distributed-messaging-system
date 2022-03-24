package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;

public class PushBasedConsumer extends Consumer implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(PushBasedConsumer.class);

    public PushBasedConsumer(Node brokerNode, String topic) throws ConnectionException {
        super(brokerNode, topic);
        Thread fetchingThread = new Thread(this, "Push");
        fetchingThread.start();
    }

    protected void subscribeBroker(){
        LOGGER.info("Subscribing broker, topic: " + topic);
        Subscribe.SubscribeRequest request = Subscribe.SubscribeRequest.newBuilder().setTopic(topic).build();
        Any packet = Any.pack(request);
        brokerConnection.send(packet.toByteArray());
    }

    @Override
    public void run() {
        this.subscribeBroker();
        while(!this.isClosed()){
            ConsumerRecord.Message record = this.fetchBroker();
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
