import api.*;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.*;
import utils.ConnectionException;
import utils.Node;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SystemTest {
    private static final int brokerPort = 1111;
    private static final int timeout = 500;
    private static final String topic = "testTopic";
    private static Node brokerNode;
    private static Broker broker;
    private static Producer producer;
    private static Consumer pullBasedConsumer;
    private static Consumer pushBasedConsumer;
    private static ArrayList<String> messages;

    @BeforeAll
    static void setUp() throws IOException, ConnectionException {
        brokerNode = new Node("localhost", brokerPort);
        broker = new Broker(brokerPort);
        broker.startServer();
        producer = new Producer(brokerNode);
        pullBasedConsumer = new PullBasedConsumer(brokerNode, topic, 0L);
        messages = new ArrayList<>();
        messages.add("hello");
        messages.add("world");
        messages.add("pub");
        messages.add("sub");
    }

    private void publish(String msg) throws IOException, InterruptedException, ConnectionException {
        producer.send(topic, msg.getBytes());
        Thread.sleep(10);
    }

    private void publishMany() throws IOException, ConnectionException {
        for (String msg: messages) {
            producer.send(topic, msg.getBytes());
        }
    }

    @Test
    @Order(1)
    void pullBasedTest() throws InterruptedException, IOException, ConnectionException {
        String msg = "test1";
        publish(msg);
        ByteString data = pullBasedConsumer.poll(timeout);
        assertEquals(ByteString.copyFrom(msg.getBytes()), data);
    }

    @Test
    @Order(2)
    void pullBasedTest2() throws InterruptedException, IOException, ConnectionException {
        String msg = "test2";
        publish(msg);
        ByteString data = pullBasedConsumer.poll(timeout);
        assertEquals(ByteString.copyFrom(msg.getBytes()), data);
    }

    @Test
    @Order(3)
    void pullBasedTest3() throws InterruptedException, IOException, ConnectionException {
        String msg = "test3";
        publish(msg);
        ByteString data = pullBasedConsumer.poll(timeout);
        assertEquals(ByteString.copyFrom(msg.getBytes()), data);
    }

    @Test
    @Order(4)
    void pullBasedNullTest() throws InterruptedException, ConnectionException {
        ByteString data = pullBasedConsumer.poll(0);
        assertNull(data);
    }

    @Test
    @Order(5)
    void pullBasedManyTest() throws IOException, InterruptedException, ConnectionException {
        publishMany();
        for (String msg: messages) {
            ByteString data = pullBasedConsumer.poll(timeout);
            assertEquals(ByteString.copyFrom(msg.getBytes()), data);
        }
    }

    @Test
    @Order(6)
    void pushBasedTest() throws InterruptedException, IOException, ConnectionException {
        pushBasedConsumer = new PushBasedConsumer(brokerNode, topic);
        Thread.sleep(timeout);
        String msg = "test4";
        publish(msg);
        ByteString data = pushBasedConsumer.poll(timeout);
        assertEquals(ByteString.copyFrom(msg.getBytes()), data);
    }

    @Test
    @Order(7)
    void pushBasedTest2() throws InterruptedException, IOException, ConnectionException {
        String msg = "test5";
        publish(msg);
        ByteString data = pushBasedConsumer.poll(timeout);
        assertEquals(ByteString.copyFrom(msg.getBytes()), data);
    }

    @Test
    @Order(8)
    void pushBasedTest3() throws InterruptedException, IOException, ConnectionException {
        String msg = "test6";
        publish(msg);
        ByteString data = pushBasedConsumer.poll(timeout);
        assertEquals(ByteString.copyFrom(msg.getBytes()), data);
    }

    @Test
    @Order(9)
    void pushBasedNullTest() throws InterruptedException, IOException, ConnectionException {
        ByteString data = pushBasedConsumer.poll(0);
        assertNull(data);
    }

    @Test
    @Order(10)
    void pushBasedManyTest() throws IOException, InterruptedException, ConnectionException {
        publishMany();
        for (String msg: messages) {
            ByteString data = pushBasedConsumer.poll(timeout);
            assertEquals(ByteString.copyFrom(msg.getBytes()), data);
        }
    }

    @AfterAll
    static void shutdown() throws IOException {
        broker.shutdown();
        producer.close();
        pushBasedConsumer.close();
        pullBasedConsumer.close();
    }
}
