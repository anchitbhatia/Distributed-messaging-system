import api.BrokerApi;
import api.ConnectionException;
import api.Node;
import api.ProducerApi;
import utils.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DistributedPubSubApplication {

    private static void publisherNode(ProducerConfig config){
        Node brokerNode = config.getBroker();
        try {
            ProducerApi producer = new ProducerApi(brokerNode);
            String topic = config.getTopic();
            String file = config.getFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                Thread.sleep(3000);
                while ((line = br.readLine()) != null) {
                    System.out.println("\nApplicationConfig: Publishing, data: " + line);
                    producer.send(topic, line.getBytes());
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            producer.close();
            System.out.println("\nApplicationConfig: Finished publishing");
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
    public static void main(String[] args) {
        try {
            ApplicationConfig app = Helper.parseArgs(args);
            Object config = app.getConfig();
            switch (app.getType()) {
                case Constants.TYPE_PRODUCER -> publisherNode((ProducerConfig) config);
                case Constants.TYPE_BROKER -> brokerNode((BrokerConfig) config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
