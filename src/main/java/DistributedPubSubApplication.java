import api.Connection;
import api.broker.Broker;
//import api.broker.BrokerOld;
//
//
//import api.broker.LeaderOld;
import api.consumer.Consumer;
import api.consumer.PullBasedConsumer;
import api.consumer.PushBasedConsumer;
import api.producer.Producer;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import configs.ConsumerConfig;
import messages.Ack;
import messages.Leader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionException;
import utils.Node;
import configs.ApplicationConfig;
import configs.BrokerConfig;
import configs.ProducerConfig;
import utils.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/***
 * Application class
 * @author anchitbhatia
 */
public class DistributedPubSubApplication {
    private static final Logger LOGGER = LogManager.getLogger("App");


    private static Producer getNewProducerObject(List<Node> allBrokers, String topic, Any packet) {
        LOGGER.debug("Getting new Object");
        Connection brokerConnection = Helper.connectAllBrokers(allBrokers, topic, packet, true);
        return new Producer(brokerConnection);
    }

    /***
     * Method for producer application
     * @param config : producer configuration
     */
    private static void producerNode(ProducerConfig config){
        String topic = config.getTopic();
        String file = config.getFile();
        messages.Producer.ProducerRequest request = messages.Producer.ProducerRequest.newBuilder().setTopic(topic).build();
        Any requestPacket = Any.pack(request);
//        Producer producer = null;
        List<Node> brokersList = config.getBrokers();
        Producer producer = getNewProducerObject(brokersList, topic, requestPacket);
        LOGGER.info("Request accepted by leader");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Scanner input = new Scanner(System.in);
            while ((line = br.readLine()) != null) {
//                input.next();
                LOGGER.info("Publishing, data: " + line);
                try {
                    producer.send(topic, line.getBytes());
                } catch (IOException | ConnectionException e) {
                    producer.close();
                    producer = getNewProducerObject(brokersList, topic, requestPacket);
                    producer.send(topic, line.getBytes());
                }
//                Thread.sleep(1000);
            }
        } catch (IOException  | ConnectionException e) {
            LOGGER.error("Unable to publish");
        }
        try {
            producer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Finished publishing");
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
        List<Node> brokersList = config.getBrokers();
        LOGGER.debug("Brokers size : " + brokersList.size());
        String topic = config.getTopic();
        String type = config.getType();

        Connection connection = Helper.connectAllBrokers(brokersList, topic, null, false);
        LOGGER.debug("Connected to " + connection.getNode());

        Consumer consumer = null;
        try {
            if (Objects.equals(type, Constants.PUSH_TYPE)) {
                consumer = new PushBasedConsumer(connection, topic);
            }
            else {
                Long startPosition = config.getStartPosition();
                consumer = new PullBasedConsumer(connection, topic, startPosition);
            }
            consumer.initializeBrokerDetails(brokersList);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        String file = config.getFile();
        try (FileOutputStream writer = new FileOutputStream(file)){
            ByteString data;
            while (true){
                try {
                    data = consumer.poll(config.getTimeout());
                    if (data != null) {
                        writer.write(data.toByteArray());
                        writer.write("\n".getBytes());
                    }
                } catch (IOException | InterruptedException | ConnectionException e) {

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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


