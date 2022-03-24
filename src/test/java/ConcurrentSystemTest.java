import api.*;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.*;
import utils.ConnectionException;
import utils.Node;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConcurrentSystemTest {
    private static final int brokerPort = 1111;
    private static final int timeout = 500;
    private static final String topic = "testTopic";
    private static Node brokerNode;
    private static Broker broker;
    private static Producer producer;
    private static Consumer pullBasedConsumer;
    private static Consumer pushBasedConsumer;
    private static int producedCount = 0;
    private static int pushConsumedCount = 0;

    @BeforeAll
    static void setUp() throws IOException, ConnectionException {
        brokerNode = new Node("localhost", brokerPort);
        broker = new Broker(brokerPort);
        broker.startServer();
        producer = new Producer(brokerNode);
        pullBasedConsumer = new PullBasedConsumer(brokerNode, topic, 0L);
        pushBasedConsumer = new PushBasedConsumer(brokerNode, topic);

        Thread pushConsumerSampleThread = new Thread(new PushConsumerSampleApp());
        pushConsumerSampleThread.start();

        Thread producerSampleThread = new Thread(new ProducerSampleApp());
        producerSampleThread.start();

    }

    @Test
    @Order(1)
    void NotNullTest() throws InterruptedException, ConnectionException {
        Thread.sleep(4000);
        ByteString data = pullBasedConsumer.poll(timeout);
        assertNotNull(data);
    }

    @Test
    @Order(2)
    void pushCountTest() throws InterruptedException {
        Thread.sleep(1000);
        assertEquals(producedCount, pushConsumedCount);
    }


    @AfterAll
    static void shutdown() throws IOException {
        broker.shutdown();
        producer.close();
        pushBasedConsumer.close();
    }

    private static class ProducerSampleApp implements Runnable {
        @Override
        public void run() {
            String file = "src/test/sampleProducer.txt";
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    producer.send(topic, line.getBytes());
                    producedCount += 1;
                    Thread.sleep(50);
                }
            } catch (IOException | ConnectionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PushConsumerSampleApp implements Runnable {
        @Override
        public void run() {
            String file = "src/test/sampleConsumer.txt";
            try (FileOutputStream writer = new FileOutputStream(file)) {
                ByteString data;
                while (true) {
                    data = pushBasedConsumer.poll(timeout);
                    if (data != null) {
                        writer.write(data.toByteArray());
                        writer.write("\n".getBytes());
                        pushConsumedCount += 1;
                    }
                }
            } catch (IOException | InterruptedException | ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

}
