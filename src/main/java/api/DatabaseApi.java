package api;

import com.google.protobuf.ByteString;
import messages.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseApi {
    private static ConcurrentHashMap<String, Long> currentOffsetMap;
    private static ConcurrentHashMap<String, HashMap<Long, byte[]>> database;

    public static void initializeDatabase(){
        currentOffsetMap = new ConcurrentHashMap<>();
        database = new ConcurrentHashMap<>();
    }

    public synchronized static void addRecord(ProducerRecord.ProducerMessage record){
        String topic = record.getTopic();
        Long currentOffset = currentOffsetMap.getOrDefault(topic, 0L);
        HashMap<Long, byte[]> topicMap = database.getOrDefault(topic, new HashMap<>());
        byte[] data = record.getData().toByteArray();
        System.out.println("\nDatabase: adding, Topic: " + topic + ", Offset: " + currentOffset);
        topicMap.put(currentOffset, data);
        database.put(topic, topicMap);
        currentOffset += data.length;
        currentOffsetMap.put(topic, currentOffset);
    }
    
    public static void printDb(){
        System.out.println("\nDatabase: printing");
        database.forEach((k, v)
                -> System.out.println("Topic: " + k + ", Offsets: " + v.keySet()));
    }
}
