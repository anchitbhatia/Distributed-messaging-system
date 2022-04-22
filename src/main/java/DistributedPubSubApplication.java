import api.broker.Broker;
//import api.broker.BrokerOld;
//
//
//import api.broker.LeaderOld;
import api.consumer.Consumer;
import api.consumer.PullBasedConsumer;
import api.consumer.PushBasedConsumer;
import api.producer.Producer;
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
import java.util.Scanner;

/***
 * Application class
 * @author anchitbhatia
 */
public class DistributedPubSubApplication {
    private static final Logger LOGGER = LogManager.getLogger("App");

    /***
     * Method for producer application
     * @param config : producer configuration
     */
    private static void producerNode(ProducerConfig config){
        Node brokerNode = config.getBroker();
        try {
            Producer producer = new Producer(brokerNode);
            String topic = config.getTopic();
            String file = config.getFile();
            if (producer.sendRequest(topic)) {
                LOGGER.info("Request accepted by leader");
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    Scanner input = new Scanner(System.in);
                    while ((line = br.readLine()) != null) {
                        input.next();
                        LOGGER.info("Publishing, data: " + line);
                        producer.send(topic, line.getBytes());
                        Thread.sleep(1000);
                    }
                } catch (ConnectionException e) {
                    LOGGER.info("Unable to publish, connection closed");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                producer.close();
                LOGGER.info("Finished publishing");
            }
            else {
                LOGGER.info("Request to leader not successful");
            }
        } catch (ConnectionException | IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Method for broker application
     * @param config : broker configuration
     */
    private static void brokerNode(BrokerConfig config) {
        try {
            Broker broker;
            if (config.isLeader()) {
                broker = new Broker(config.getHost());
            }
            else{
                broker = new Broker(config.getHost(), config.getLeader());
            }
            broker.startBroker();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Method for consumer application
     * @param config : consumer configuration
     */
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
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (ConnectionException e) {
            LOGGER.info("Connection closed");
        }
        LOGGER.info("Finished reading");
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


