import api.*;
import com.google.protobuf.ByteString;
import configs.ConsumerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;
import configs.ApplicationConfig;
import configs.BrokerConfig;
import configs.ProducerConfig;
import utils.*;

import java.io.*;
import java.util.Objects;

public class DistributedPubSubApplication {
    private static final Logger LOGGER = LogManager.getLogger("Application");

    private static void producerNode(ProducerConfig config){
        Node brokerNode = config.getBroker();
        try {
            Producer producer = new Producer(brokerNode);
            String topic = config.getTopic();
            String file = config.getFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
//                Thread.sleep(3000);
                while ((line = br.readLine()) != null) {
                    LOGGER.info("Publishing, data: " + line);
                    producer.send(topic, line.getBytes());
                    Thread.sleep(50);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            producer.close();
            LOGGER.info("Finished publishing");
        } catch (ConnectionException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void brokerNode(BrokerConfig config) {
        try {
            Broker broker = new Broker(config.getHost().getPort());
            broker.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void consumerNode(ConsumerConfig config){
        Node brokerNode = config.getBroker();
        String topic = config.getTopic();
        String type = config.getType();
        Consumer consumer = null;
        try {
            if (Objects.equals(type, Constants.PUSH_TYPE)) {
                consumer = new PushBasedConsumer(brokerNode, topic);
            }
            else {
                Long startPosition = config.getStartPosition();
                consumer = new PullBasedConsumer(brokerNode, topic, startPosition);
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        String file = config.getFile();
        try (FileOutputStream writer = new FileOutputStream(file)){

            ByteString data;
            while (true){
                data = consumer.poll(config.getTimeout());
                if (data != null){
                    writer.write(data.toByteArray());
                    writer.write("\n".getBytes());
                }
                else{
                    LOGGER.info("NUll data received");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        try {
            ApplicationConfig app = Helper.parseArgs(args);
            Object config = app.getConfig();
            switch (app.getType()) {
                case Constants.TYPE_PRODUCER -> producerNode((ProducerConfig) config);
                case Constants.TYPE_BROKER -> brokerNode((BrokerConfig) config);
                case Constants.TYPE_CONSUMER -> consumerNode((ConsumerConfig) config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


