package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;

import java.io.IOException;

/***
 * Pull based consumer class to create pull based consumers
 * @author anchitbhatia
 */
public class PullBasedConsumer extends Consumer implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(PullBasedConsumer.class);
    private Long nextOffset;

    public PullBasedConsumer(Node brokerNode, String topic, Long offset) throws ConnectionException {
        super(brokerNode, topic);
        this.nextOffset = offset;
        Thread fetchingThread = new Thread(this, "Pull");
        fetchingThread.start();
    }

    protected void requestBroker(Long offset) throws IOException {
        LOGGER.info("Requesting topic: " + this.topic + ", offset: " + offset);
        Request.ConsumerRequest request = Request.ConsumerRequest.newBuilder().
                setTopic(this.topic).
                setOffset(offset).
                build();
        Any packet = Any.pack(request);
        brokerConnection.send(packet.toByteArray());
    }

    @Override
    public void run() {
        while(!this.isClosed()){
            try {
                Thread.sleep(50);
                this.requestBroker(nextOffset);
                ConsumerRecord.Message record = this.fetchBroker();
                if (record!=null){
                    ByteString data = record.getData();
                    if (data.size() != 0){
                        this.addMessage(data);
                        LOGGER.info("Received offset: " + record.getOffset() + ", data: " + data);
                        nextOffset += data.size();
                    }
                    else{
                        LOGGER.info("Sleeping for 3 seconds");
                        Thread.sleep(3000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
