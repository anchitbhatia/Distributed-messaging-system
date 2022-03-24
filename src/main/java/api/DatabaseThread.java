package api;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import messages.ConsumerRecord;
import messages.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedDeque;

public class DatabaseThread implements Runnable{
    private static final Logger LOGGER = LogManager.getLogger(DatabaseThread.class);
    private boolean isRunning;

    public DatabaseThread() {
        this.isRunning = true;
    }

    public void shutdown(){
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (this.isRunning){
            ProducerRecord.ProducerMessage record = Database.pollMsgQueue();
            while (record == null){
                record = Database.pollMsgQueue();
            }
            String topic = record.getTopic();
            byte[] data = record.getData().toByteArray();
            LOGGER.debug("Received from Producer, Topic: " + topic + ", Data: " + record.getData());
            Long offset = Database.addRecord(topic, data);
            ConcurrentLinkedDeque<Connection> subscribers = Database.getSubscribers(topic);
            if (subscribers!=null){
                for (Connection conn: subscribers) {
                    ConsumerRecord.Message consumerRecord = ConsumerRecord.Message.newBuilder().
                            setOffset(offset).
                            setData(ByteString.copyFrom(data)).
                            build();
                    Any packet = Any.pack(consumerRecord);
                    conn.addQueue(packet.toByteArray());
                    LOGGER.debug("Sending to " + conn + ", Topic: " + topic + ", Offset: " + offset);
                }
            }
        }
    }
}
