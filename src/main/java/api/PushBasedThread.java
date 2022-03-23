package api;

import com.google.protobuf.ByteString;
import messages.ConsumerRecord;

import java.io.IOException;

public class PushBasedThread implements Runnable{
    private final Consumer consumer;

    public PushBasedThread(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        consumer.subscribeBroker();
        while(!consumer.isClosed()){
            try {
                Thread.sleep(1000);
                ConsumerRecord.Message record = consumer.fetchBroker();
                if (record!=null){
                    ByteString data = record.getData();
                    if (data.size() != 0){
                        consumer.addMessage(data);
                        System.out.println("\nPush Consumer: received from broker, Offset: " + record.getOffset() + ", Data: " + data);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
