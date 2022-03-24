package api;

import messages.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import java.util.HashMap;
import java.util.concurrent.*;

public class Database {
    private static final Logger LOGGER = LogManager.getLogger(Database.class);
    private static BlockingQueue<ProducerRecord.ProducerMessage> msgQueue;
    private static ConcurrentHashMap<String, ConcurrentLinkedDeque<Connection>> subscribers;
    private static ConcurrentHashMap<String, Long> currentOffsetMap;
    private static ConcurrentHashMap<String, HashMap<Long, byte[]>> database;

    public static void initializeDatabase() {
        msgQueue = new LinkedBlockingDeque<>();
        subscribers= new ConcurrentHashMap<>();
        currentOffsetMap = new ConcurrentHashMap<>();
        database = new ConcurrentHashMap<>();
    }

    public static void addQueue(ProducerRecord.ProducerMessage record) {
        msgQueue.add(record);
        LOGGER.info("Added msg to queue: " + record.getTopic() + ", data: " + record.getData());
    }

    public static ProducerRecord.ProducerMessage pollMsgQueue() {
        try {
            return msgQueue.poll(Constants.POLL_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static void addSubscriber(String topic, Connection connection){
        LOGGER.info("Adding subscriber to topic : " + topic);
        ConcurrentLinkedDeque<Connection> topicSubscribers = subscribers.getOrDefault(topic, new ConcurrentLinkedDeque<>());
        topicSubscribers.add(connection);
        subscribers.put(topic, topicSubscribers);
    }

    public static ConcurrentLinkedDeque<Connection> getSubscribers(String topic){
        return subscribers.getOrDefault(topic, null);
    }

    public static Long addRecord(String topic, byte[] data) {
        Long currentOffset = currentOffsetMap.getOrDefault(topic, 0L);
        HashMap<Long, byte[]> topicMap = database.getOrDefault(topic, new HashMap<>());
        topicMap.put(currentOffset, data);
        database.put(topic, topicMap);
        Long lastOffset = currentOffset;
        currentOffset += data.length;
        currentOffsetMap.put(topic, currentOffset);
        LOGGER.info("Record added topic: " + topic + ", offset: " + currentOffset);
        return lastOffset;
    }

    public static byte[] getRecord(String topic, long requiredOffset) {
        HashMap<Long, byte[]> topicMap = database.getOrDefault(topic, null);
        if (topicMap == null) {
            return null;
        }
        return topicMap.getOrDefault(requiredOffset, null);
    }
}
