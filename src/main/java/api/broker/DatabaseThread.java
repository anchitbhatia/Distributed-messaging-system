//package api.broker;
//
//import api.Connection;
//import com.google.protobuf.Any;
//import com.google.protobuf.ByteString;
//import messages.ConsumerRecord;
//import messages.ProducerRecord;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.concurrent.ConcurrentLinkedDeque;
//
///***
// * Database Thread class to manage delivery of messages to subscribers
// * @author anchitbhatia
// */
//public class DatabaseThread implements Runnable{
//    private static final Logger LOGGER = LogManager.getLogger(DatabaseThread.class);
//    private final Database database;
//    private boolean isRunning;
//
//    public DatabaseThread(Database database) {
//        this.database = database;
//        this.isRunning = true;
//    }
//
//    /***
//     * Method to shut down
//     */
//    public void shutdown(){
//        this.isRunning = false;
//    }
//
//    @Override
//    public void run() {
//        while (this.isRunning){
//            ProducerRecord.ProducerMessage record = this.database.pollMsgQueue();
//            while (record == null){
//                record = this.database.pollMsgQueue();
//            }
//            String topic = record.getTopic();
//            byte[] data = record.getData().toByteArray();
//            LOGGER.debug("Received from Producer, Topic: " + topic + ", Data: " + record.getData());
//            Long offset = this.database.addMessage(topic, data);
//            ConcurrentLinkedDeque<Connection> subscribers = this.database.getSubscribers(topic);
//            if (subscribers!=null){
//                for (Connection conn: subscribers) {
//                    ConsumerRecord.Message consumerRecord = ConsumerRecord.Message.newBuilder().
//                            setOffset(offset).
//                            setData(ByteString.copyFrom(data)).
//                            build();
//                    Any packet = Any.pack(consumerRecord);
//                    conn.addQueue(packet.toByteArray());
//                    LOGGER.debug("Sending to " + conn + ", Topic: " + topic + ", Offset: " + offset);
//                }
//            }
//        }
//    }
//}
