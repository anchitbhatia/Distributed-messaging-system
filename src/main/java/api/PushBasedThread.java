package api;

import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PushBasedThread implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(PushBasedThread.class);
    private final Consumer consumer;

    public PushBasedThread(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        consumer.subscribeBroker();
        while(!consumer.isClosed()){
            ConsumerRecord.Message record = consumer.fetchBroker();
            if (record!=null){
                ByteString data = record.getData();
                if (data.size() != 0){
                    consumer.addMessage(data);
                    LOGGER.info("\nPush Consumer: received from broker, Offset: " + record.getOffset() + ", Data: " + data);
                }
            }
        }
    }
}
