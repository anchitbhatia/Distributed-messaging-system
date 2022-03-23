package api;

import com.google.protobuf.ByteString;
import messages.ProducerRecord;
import messages.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static ConcurrentHashMap<String, Long> currentOffsetMap;
    private static ConcurrentHashMap<String, HashMap<Long, byte[]>> database;

    public static void initializeDatabase(){
        currentOffsetMap = new ConcurrentHashMap<>();
        database = new ConcurrentHashMap<>();
    }

    public synchronized static void addRecord(String topic ,byte[] data){
        Long currentOffset = currentOffsetMap.getOrDefault(topic, 0L);
        HashMap<Long, byte[]> topicMap = database.getOrDefault(topic, new HashMap<>());
        System.out.println("\nDatabase: adding, Topic: " + topic + ", Offset: " + currentOffset);
        topicMap.put(currentOffset, data);
        database.put(topic, topicMap);
        currentOffset += data.length;
        currentOffsetMap.put(topic, currentOffset);
    }

    public static byte[] getRecord(String topic, long requiredOffset){
        HashMap<Long, byte[]> topicMap = database.getOrDefault(topic, null);
        if (topicMap==null){
            return null;
        }
        return topicMap.getOrDefault(requiredOffset, null);
    }
    
    public static void printDb(){
        System.out.println("\nDatabase: printing");
        database.forEach((k, v)
                -> System.out.println("Topic: " + k + ", Offsets: " + v.keySet()));
    }
}
