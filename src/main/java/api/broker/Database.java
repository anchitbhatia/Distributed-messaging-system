package api.broker;

import api.Connection;
import messages.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import java.util.concurrent.*;

/***
 * Database class to manage database operations
 * @author anchitbhatia
 */
public class Database {
    private static final Logger LOGGER = LogManager.getLogger(Database.class);
//    private BlockingQueue<ProducerRecord.ProducerMessage> msgQueue;
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<Connection>> subscribers;
    private ConcurrentHashMap<String, Long> currentOffsetMap;
    private ConcurrentHashMap<String, ConcurrentHashMap<Long, byte[]>> database;

    /***
     * Method to initialize database
     */
    public void initializeDatabase() {
//        msgQueue = new LinkedBlockingDeque<>();
        subscribers= new ConcurrentHashMap<>();
        currentOffsetMap = new ConcurrentHashMap<>();
        database = new ConcurrentHashMap<>();
    }

//    /***
//     * Method to add producer record to the queue
//     * @param record to be added to database
//     */
//    public void addQueue(ProducerRecord.ProducerMessage record) {
//        msgQueue.add(record);
//        LOGGER.info("Added msg to queue: " + record.getTopic() + ", data: " + record.getData());
//    }

//    /***
//     * Method to poll queue holding producer records
//     * @return first record in the queue
//     */
//    public ProducerRecord.ProducerMessage pollMsgQueue() {
//        try {
//            return msgQueue.poll(Constants.POLL_TIMEOUT, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            return null;
//        }
//    }

    /***
     * Method to add push based consumer to subscribers list
     * @param topic : topic to be subscribed
     * @param connection : push based consumer connection to be added
     */
    public void addSubscriber(String topic, Connection connection){
        LOGGER.info("Adding subscriber to topic : " + topic);
        ConcurrentLinkedDeque<Connection> topicSubscribers = subscribers.getOrDefault(topic, new ConcurrentLinkedDeque<>());
        topicSubscribers.add(connection);
        subscribers.put(topic, topicSubscribers);
    }

    /***
     * Method to remove subscriber from the subscribers list
     * @param topic : topic of the subscriber to be removed
     * @param connection : push based consumer connection to be removed
     */
    public void removeSubscriber(String topic, Connection connection) {
        LOGGER.info("Removing subscriber from topic : " + topic);
        ConcurrentLinkedDeque<Connection> topicSubscribers = subscribers.getOrDefault(topic, null);
        if (topicSubscribers!=null) {
            topicSubscribers.remove(connection);
        }
    }

    /***
     * Method to get list of subscribers
     * @param topic : topic for which subscribers needed
     * @return ConcurrentLinkedDeque list of subscribers
     */
    public ConcurrentLinkedDeque<Connection> getSubscribers(String topic){
        return subscribers.getOrDefault(topic, null);
    }

    /***
     * Method to add record in database
     * @param topic : topic of the record
     * @param data : data
     * @return offset
     */
    public Long addMessage(String topic, byte[] data) {
        Long currentOffset = currentOffsetMap.getOrDefault(topic, 0L);
        ConcurrentHashMap<Long, byte[]> topicMap = database.getOrDefault(topic, new ConcurrentHashMap<>());
        topicMap.put(currentOffset, data);
        database.put(topic, topicMap);
        Long lastOffset = currentOffset;
        currentOffset += data.length;
        currentOffsetMap.put(topic, currentOffset);
        LOGGER.info("Record added topic: " + topic + ", offset: " + currentOffset);
        return lastOffset;
    }

    /***
     * Method to get record from database
     * @param topic : topic of the record
     * @param requiredOffset : offset of the record
     * @return record in byte[]
     */
    public byte[] getRecord(String topic, long requiredOffset) {
        ConcurrentHashMap<Long, byte[]> topicMap = database.getOrDefault(topic, null);
        if (topicMap == null) {
            return null;
        }
        return topicMap.getOrDefault(requiredOffset, null);
    }
}
