package api;

import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PullBasedThread implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(PullBasedThread.class);
    private final Consumer consumer;
    private Long nextOffset;

    public PullBasedThread(Consumer consumer, Long offset) {
        this.consumer = consumer;
        this.nextOffset = offset;
    }

    @Override
    public void run() {
        while(!consumer.isClosed()){
            try {
                Thread.sleep(1000);
                consumer.requestBroker(nextOffset);
                ConsumerRecord.Message record = consumer.fetchBroker();
                if (record!=null){
                    ByteString data = record.getData();
                    if (data.size() != 0){
                        consumer.addMessage(data);
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
