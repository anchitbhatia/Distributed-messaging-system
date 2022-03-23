import api.BrokerApi;
import api.Consumer;
import com.google.protobuf.ByteString;
import configs.ConsumerConfig;
import utils.ConnectionException;
import utils.Node;
import api.Producer;
import configs.ApplicationConfig;
import configs.BrokerConfig;
import configs.ProducerConfig;
import utils.*;

import java.io.*;

public class DistributedPubSubApplication {

    private static void publisherNode(ProducerConfig config){
        Node brokerNode = config.getBroker();
        try {
            Producer producer = new Producer(brokerNode);
            String topic = config.getTopic();
            String file = config.getFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                Thread.sleep(3000);
                while ((line = br.readLine()) != null) {
                    System.out.println("\nApplication: Publishing, data: " + line);
                    producer.send(topic, line.getBytes());
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            producer.close();
            System.out.println("\nApplication: Finished publishing");
        } catch (ConnectionException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void brokerNode(BrokerConfig config) {
        try {
            BrokerApi broker = new BrokerApi(config.getHost().getPort());
            broker.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void consumerNode(ConsumerConfig config){
        Node brokerNode = config.getBroker();
        String topic = config.getTopic();
        Long startPosition = config.getStartPosition();
        String file = config.getFile();

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
        try (FileOutputStream writer = new FileOutputStream(file)){
            Consumer consumer = new Consumer(brokerNode, topic, startPosition);
            ByteString data;
            while (true){
                data = consumer.poll(config.getTimeout());
                if (data != null){
                    writer.write(data.toByteArray());
                    writer.write("\n".getBytes());
                }
                else{
                    System.out.println("\nApplication: NUll data");
                }
            }
        } catch (IOException | ConnectionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        try {
            ApplicationConfig app = Helper.parseArgs(args);
            Object config = app.getConfig();
            switch (app.getType()) {
                case Constants.TYPE_PRODUCER -> publisherNode((ProducerConfig) config);
                case Constants.TYPE_BROKER -> brokerNode((BrokerConfig) config);
                case Constants.TYPE_CONSUMER -> consumerNode((ConsumerConfig) config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


