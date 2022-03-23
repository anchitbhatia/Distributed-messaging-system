package api;

import com.google.protobuf.ByteString;
import messages.ConsumerRecord;

import java.io.IOException;

public class PullBasedThread implements Runnable{
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
                        System.out.println("\nConsumer: received from broker, Offset: " + record.getOffset() + ", Data: " + data);
                        nextOffset += data.size();
//                            Thread.sleep(1000);
                    }
                    else{
                        System.out.println("\nConsumer: sleeping for 3 seconds");
                        Thread.sleep(3000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
